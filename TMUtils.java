import absyn.*;

import java.lang.String;
import java.util.*;
import java.io.*;

/* TM Utils
 * General goal of this class is to abstract the tree traversal and high level generation 
 * from the direct C instruction to TM instruction conversion
 * TODO add utility function to help aid in conversion
 */

public class TMUtils
{
    PrintWriter printer = null;
    //Constant definitions
    final static int SPACES = 4;

    //Special registers
    final static int pc = 7;
    final static int gp = 6;
    final static int fp = 5;
    final static int ac = 0;
    final static int ac1 = 1;

    //Constants
    final static int GLOBAL_SCOPE = 0;

    private static int ins = 0;
    private static int mainLoc = 0;
    //Next available loc after global frame, basically record memory space used by global var
    private static int globalOffset = 0;

    private static int currentFrameOffset = 0;

    public TMUtils(String fileName) {
        try {
            printer = new PrintWriter(fileName.split("\\.")[0]+".tm", "utf-8");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int getCurrINS() {
        return ins;
    }

    //Utility functions, can change where the output is directed here
    public void emitComment(String line) {
        printer.println("* "+line);
    }

    public void emit(String line) {
        printer.println(ins++ + ": "+line);
    }

    public void emit(int ins, String line) {
        printer.println(ins + ": "+line);
    }

    //#region[rgba(150, 10, 10, 0.15)] All instructions are either R0 or RM instructions
    //Slide 3 from TMSim slides
    public void emitR0Instruction(String opCode, int r, int s, int t, String comment) {
        emit(opCode+" "+r+","+s+","+t+"\t"+comment);
    }

    //Slide 4 from TMSim slides
    public void emitRMInstruction(String opCode, int r, int d, int s, String comment) {
        emit(opCode+" "+r+","+d+"("+s+")\t"+comment);
    }
    //#endregion

    //#region[rgba(10,40,120,0.3)] Helper functions
    //Output a comment and skip a line, return skipped position
    public int emitSkip(String msg) {
        emitComment(msg);
        return ins++;
    }

    public void emitJump(String opCode, int r, int startLoc, int endLoc, String msg) {
        int offset = endLoc - startLoc - 1;
        //Set PC Reg to PC REG + offset
        emit(startLoc, opCode+" "+r+","+offset+"("+pc+")"+"\t"+msg);
        if (startLoc==ins)
            ins++;
    }

    public void emitJump(int startLoc, int endLoc, String msg) {
        emitJump("LDA", pc, startLoc, endLoc, msg);
    }

    public void emitJumpToCurrentINS(int startLoc, String msg) {
        emitJump(startLoc, ins, msg);
    }

    public void emitJumpToCurrentINS(String opCode, int r, int startLoc, String msg) {
        emitJump(opCode, r, startLoc, ins, msg);
    }
    
    public int newTemp() {
        int tmp = currentFrameOffset;
        currentFrameOffset -= 1;
        emitRMInstruction("ST", ac, tmp, fp, "op: push tmp left");
        return tmp;
    }
    public void releaseTempVar() {
        currentFrameOffset++;
    }
    //#endregion

    //#region[rgba(90,10,90, 0.1)] High level instruction utils
    public int buildFunction(FunctionDec dec, String name) {
        int loc = emitSkip("Function: "+name);
        emitRMInstruction("ST", ac, -1, fp, "store return");
        if (name.equals("main"))
            mainLoc = loc;
        dec.address = loc;
        //position 0 is ofp
        //position -1 is ret-addr
        currentFrameOffset = -2;
        return loc;
    }
    
    public void finishFunction(int loc, String name) {
        emitRMInstruction("LD", pc, -1, fp, "return to caller");
        emitJump(loc, ins, "Jump around function: "+name);
        emitComment("End function: "+name);
    }

    public void processSimpleDec(SimpleDec var, int depth) {
        if (depth == GLOBAL_SCOPE) {
            emitComment("Processing global var declaration: "+var.name);
            var.offset = globalOffset;
            globalOffset--;
            var.nestLevel = 0;
        } else {
            emitComment("Processing local var declaration: "+var.name);
            var.offset = currentFrameOffset;
            currentFrameOffset--;
            var.nestLevel = 1;
        }
    }

    public void processArrayDec(ArrayDec var, int size, int depth) {
        if (depth == GLOBAL_SCOPE) {
            emitComment("Processing global var[] declaration: "+var.name);
            var.offset = globalOffset;
            globalOffset-=size;
            var.nestLevel = 0;
        } else {
            emitComment("Processing local var[] declaration: "+var.name);
            var.offset = currentFrameOffset;
            currentFrameOffset-=size;
            var.nestLevel = 1;
        }
    }

    public void verifyArrayAccess(int maxSize) {
        emitRMInstruction("JLT", ac, 1, pc, "Halt if subscript < 0");
        emitRMInstruction("LDA", pc, 1, pc, "Jump over if not");
        emitR0Instruction("HALT", 0, 0, 0, "End (rip)");
        //TODO check max size
        //TODO output error
    }

    public void loadArray(IndexVar var, TMDecEntry dec, boolean assign, int tmpVar) {
        //sub offset of base addr
        //load value using addr stored
        emitRMInstruction("LD", ac1, tmpVar, fp, "load array base addr");
        emitR0Instruction("SUB", ac, ac1, ac, "base is at top of array");

        if(!assign)
            emitRMInstruction("LD", ac, 0, ac, "load value at array index");

        //pop tmp var and other shit off the stack
        currentFrameOffset = tmpVar;

    }

    public void loadVar(Var var, TMDecEntry dec, boolean isAddr) {
        if (!(dec.dec instanceof VarDec)) {
            emitComment("Error loading simple var: looking for VarDec, got function dec");
            return;
        }
        VarDec varDec = (VarDec)dec.dec;
        //TODO we need logic in order to determine if we use LD or LDA, GG, kinda working now
        String op = "LD";
        if (isAddr)
            op = "LDA";
        //Global
        emitComment("Looking up: "+var.name);
        if (varDec.nestLevel == 0) {
            emitRMInstruction(op, ac, varDec.offset, gp, "load id");
        }
        else //Local
        {
            emitRMInstruction(op, ac, varDec.offset, fp, "load id");
        }
    }

    public int startCallExp() {
        int frameStart = currentFrameOffset;
        currentFrameOffset-=2;
        return frameStart;
    }

    public int processCallExp(CallExp e, FunctionDec func, int depth, int frameStart) {

        emitComment("-> call of function: " + e.func);
        //int frameStart = currentFrameOffset;
        //currentFrameOffset -= 2;
        //args handled by pushArgOnStack()

        if(func != null) {
            emitRMInstruction("ST", fp, frameStart, fp, "push ofp");
            emitRMInstruction("LDA", fp, frameStart, fp, "push frame");
            emitRMInstruction("LDA", ac, 1, pc, "load ac with ret ptr");
            emitJump(ins, func.address, "Jump to function location");
            emitRMInstruction("LD", fp, 0, fp, "pop frame");
        }
        else
        {
            //report_error(exp.row, exp.col, "function: " + exp.func + " not found");
        }

        //TODO figure out how tf to handle this
        currentFrameOffset = frameStart;

        emitComment("<- call");
        return frameStart;
    }

    public void finishCallExp(int frameStart) {
        currentFrameOffset = frameStart;
    }

    public void pushArgOnStack(int size) {
        emitRMInstruction("ST", ac, currentFrameOffset, fp, "store arg val");
        currentFrameOffset--;
    }


    public void returnToCaller() {
        emitRMInstruction("LD", pc, -1, fp, "return to caller");
    }

    public void processConstant(IntExp e) {
        emitComment("-> cosntant: "+e.value);
        emitRMInstruction("LDC", ac, e.value, ac, "load constant");
        emitComment("<- constant");
    }

    public void processResultAssignExp(int tmpOffset) {
        emitRMInstruction("LD", ac1, tmpOffset, fp, "op: load left");
        emitRMInstruction("ST", ac, 0, ac1, "assign: store value");
    }

    //Builds and operator expressions and puts the result in a tmp var located in the offset
    public void processResultTmpOpExp(OpExp e, int tmpOffset) {
        emitRMInstruction("LD", ac1, tmpOffset, fp, "op: load left");
        emitR0Instruction(getTMExpCode(e.op), ac, ac1, ac, "op " + getOpString(e.op));
    }

    public int processIfJump(OpExp e) {
        if (getTMOpCode(e.op).charAt(0)=='J')
            emitRMInstruction(getTMOpCode(e.op), ac, 2, pc, "br if true");
        else
            emitR0Instruction(getTMOpCode(e.op), ac, 2, pc, "br if true");
        emitRMInstruction("LDC", ac, 0, 0, "false case");
        emitRMInstruction("LDA", pc, 1, pc, "unconditional jump");
        emitRMInstruction("LDC", ac, 1, 0, "true case");
        int jmpLoc = emitSkip("If jump location");
        return jmpLoc;
    }
    //#endregion

    public void prelude(String fileName) {
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: "+fileName);
        emitComment("Standard prelude:");
        
        emitRMInstruction("LD", gp, 0, ac,"load gp with maxaddress");
        emitRMInstruction("LDA", fp, 0, gp,"copy to gp to fp");
        emitRMInstruction("ST", 0, 0, ac,"clear location 0");

        emitComment("Jump around i/o routines here");
        int loc = emitSkip("Function input");

        emitRMInstruction("ST", ac, -1, fp, "store return");
        emitR0Instruction("IN", 0, 0, 0, "Input");
        emitRMInstruction("LD", pc, -1, fp, "Return to caller");

        emitComment("code for output routine");

        emitRMInstruction("ST", ac, -1, fp, "store return");
        emitRMInstruction("LD", ac, -2, fp, "load emitput value");
        emitR0Instruction("OUT", 0, 0, 0, "output");
        emitRMInstruction("LD", pc, -1, fp, "return to caller");

        emitJump(loc, ins, "Jump around I/O code");

        emitComment("End of standard prelude.");
    }

    public void end() {
        //Will need to get the global offset
        emitRMInstruction("ST", fp, globalOffset, fp, "push ofp");
        emitRMInstruction("LDA", fp, globalOffset, fp, "push frame");
        emitRMInstruction("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRMInstruction("LDA", pc, -(ins-mainLoc), pc, "jump to main location");
        emitRMInstruction("LD", fp, 0, fp, "pop frame");
        emitR0Instruction("HALT", 0, 0, 0, "End");
        printer.close();
    }

    public String getTMOpCode(int op) {
        switch(op) {
        case OpExp.PLUS:
            return "ADD";
        case OpExp.MINUS:
            return "SUB";
        case OpExp.MUL:
            return "MUL";
        case OpExp.DIV:
            return "DIV";
        case OpExp.EQ:
            return "JEQ";
        case OpExp.NE:
            return "JNE";
        case OpExp.LT:
            return "JLT";
        case OpExp.LE:
            return "JLE";
        case OpExp.GT:
            return "JGT";
        case OpExp.GE:
            return "JGE";
        default:
            return "Error getting operator asm code";
        }
    }

    //If the code is a boolean expression well do a subtraction in prep for a jump
    public String getTMExpCode(int op) {
        switch(op)
        {
        case OpExp.PLUS:
            return "ADD";
        case OpExp.MINUS:
            return "SUB";
        case OpExp.MUL:
            return "MUL";
        case OpExp.DIV:
            return "DIV";
        default:
            return "SUB";
        }
    }
    
    public String getOpString(int op) {
        switch(op) {
        case OpExp.PLUS:
            return "+";
        case OpExp.MINUS:
            return "-";
        case OpExp.MUL:
            return "*";
        case OpExp.DIV:
            return "/";
        case OpExp.EQ:
            return "==";
        case OpExp.NE:
            return "!=";
        case OpExp.LT:
            return "<";
        case OpExp.LE:
            return "<=";
        case OpExp.GT:
            return ">";
        case OpExp.GE:
            return ">=";
        default:
            return "??? error with operator code: "+op;
        }
    }


}