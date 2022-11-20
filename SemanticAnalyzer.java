import absyn.*;

import java.lang.String;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class SemanticAnalyzer implements AbsynVisitor 
{

    //flag to track -a or -s
    static int sFlag = 0;

    class DecEntry 
    {
        String name; 
        Dec dec; 
        int depth;
        int type;
        public ArrayList<Dec> params = null;
        public DecEntry(String name, Dec dec, int depth, int type) 
        {
            this.name = name;
            this.dec = dec;
            this.depth = depth;
            this.type = type;
        }
        @Override
        public String toString() {
            return name+", "+NameTy.types[type]+", "+dec;
        }
    }

    private int depth;
    private HashMap<String, ArrayList<DecEntry>> symtable;
    private DecEntry currFunction = null;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PrintStream ps = new PrintStream(baos); //creates a new stream to hold output
    private PrintStream prev = System.out; //saves the prevois stream

    public SemanticAnalyzer(int s) {
        System.setOut(ps); //all output now goes here
        sFlag = s;
        depth = 0;
        symtable = new HashMap<>();
        FunctionDec input = new FunctionDec(0,0, new NameTy(0, 0, NameTy.INT), "input", null, null);
        addEntryToTable(input, "input", NameTy.INT);
        FunctionDec output = new FunctionDec(0,0, new NameTy(0, 0, NameTy.INT), "output", null, null);
        addEntryToTable(output, "output", NameTy.INT);
    }

     final static int SPACES = 4;
    
    //adds declaration entries to hash table
    private DecEntry addEntryToTable(Dec dec, String name, int type) {
        indent(depth);

        String decType = "declaration";
        if (dec instanceof FunctionDec)
            decType = "function";

        //Check if the entry has been previously defined
        if (symtable.containsKey(name) && symtable.get(name).size() > 0) {
            ArrayList<DecEntry> list = symtable.get(name);
            if (list.get(list.size()-1).depth == depth) {
                //Redefinition error
                System.out.println("[ERROR] Redefined "+decType+": " + name + " [row: "+(dec.row +1) + " col: "+dec.col+"]");
                System.err.println("[ERROR] Redefined "+decType+": " + name + " [row: "+(dec.row +1) + " col: "+dec.col+"]");
                return null;
            }
        }

        //If -s
        if (sFlag == 1) {
            if (dec instanceof FunctionDec)
                System.out.println("Entering the scope for function "+name + ":");
            else
                System.out.println(name+": "+NameTy.types[type]+(dec instanceof ArrayDec ? "*" : ""));
        }

        //Add declaration to symtable
        ArrayList<DecEntry> entries;
        if (symtable.containsKey(name)) {
            entries = symtable.get(name);
        } else {
            entries = new ArrayList<>();
            symtable.put(name, entries);

        }
        DecEntry entry = new DecEntry(name, dec, depth, type);
        entries.add(entry);
        return entry;
    }

    //clears table
    private void clearSymTable(int depth) {
        for (Map.Entry<String, ArrayList<DecEntry>> var : symtable.entrySet()) {
            DecEntry index = null;
            for (DecEntry entry : var.getValue()) {
               if (entry.depth == depth) {
                   index = entry;
                   break;
               }
            }
            if (index != null) {
                var.getValue().remove(index);
            }
        }
    }

    private int getVarType(String name) {
        if (symtable.containsKey(name)) {
            ArrayList<DecEntry> list = symtable.get(name);
            if (list.size() <= 0)
                return -1;
            return list.get(list.size()-1).type;
        }
        return -1;
    }

    private String formatParamsString(ArrayList<Dec> args) {
        String s = "";
        for (Dec dec : args) {
            s += NameTy.types[dec.type.type] +(dec instanceof ArrayDec ? "*" : "")+", ";
        }
        if (s.length() > 0)
            return s.substring(0, s.length()-2);
        return s;
    }

    private String formatArgsString(ArrayList<Exp> args) {
        String s = "";
        for (Exp e : args) {
            String type;
            if (e instanceof VarExp) {
                String varName = ((VarExp)e).value.name;
                if (symtable.containsKey(varName) && symtable.get(varName).size() > 0) {
                    Dec dec = symtable.get(varName).get(symtable.get(varName).size()-1).dec;
                    type = NameTy.types[dec.type.type] +(dec instanceof ArrayDec ? "*" : "");
                } else {
                    type = "Undefined";
                }
            } else {
                type = NameTy.types[e.type];
            }
            s += type+", ";
        }
        if (s.length() > 0)
            return s.substring(0, s.length()-2);
        return s;
    }

    private void indent(int level) {
        if (sFlag == 1) {
            for (int i = 0; i < level * SPACES; i++)
                System.out.print(" ");
        }
    }

    //visit method for expression list
    public void visit(ExpList expList, int level) {
        while (expList != null && expList.head != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        }
    }

    //visit method for assign expression
    public void visit(AssignExp exp, int level) {
        level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
        int lhsType = getVarType(exp.lhs.value.name);
        if (lhsType >= 0 && exp.rhs.type >= 0 && lhsType != exp.rhs.type) {
            indent(depth);
            System.out.println("[ERROR] Type mismatch in assignment found ("+NameTy.types[exp.rhs.type]+") expected ("+NameTy.types[lhsType]+") [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Type mismatch in assignment found ("+NameTy.types[exp.rhs.type]+") expected ("+NameTy.types[lhsType]+") [row: "+(exp.row +1) + " col: "+exp.col+"]");
        }
    }

    //visit method for if expression
    public void visit(IfExp exp, int level) {
        level++;
        exp.test.accept(this, level);
        if (exp.test.type == NameTy.VOID) {
            indent(depth);
            System.out.println("[ERROR] Void expression found in if [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Void expression found in if [row: "+(exp.row +1) + " col: "+exp.col+"]");
        }

        exp.thenpart.accept(this, level);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level);

    }

    //visit method for int expression
    public void visit(IntExp exp, int level) {
        exp.type = NameTy.INT;
    }

    //visit method for operation expression
    public void visit(OpExp exp, int level) {

        exp.left.accept(this, level);
        exp.right.accept(this, level);

        //If the expression has been defined and their types do not match with one another
        if (exp.left.type >= 0 && exp.right.type >= 0 && exp.left.type != exp.right.type) {
            indent(depth);
            System.out.println("[ERROR] Type mismatch around operator, found ("+NameTy.types[exp.left.type]+") "+OpExp.operators[exp.op]+" ("+NameTy.types[exp.right.type]+") [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Type mismatch around operator, found ("+NameTy.types[exp.left.type]+") "+OpExp.operators[exp.op]+" ("+NameTy.types[exp.right.type]+") [row: "+(exp.row +1) + " col: "+exp.col+"]");
            exp.type = -1;
        } else if (exp.left.type >= 0 && exp.right.type >= 0) {
            //Check if it is a boolean operator
            if (exp.op > 3 && exp.left.type == NameTy.VOID) {
                indent(depth);
                System.out.println("[ERROR] Boolean operation must be between INT not VOID [row: "+(exp.row +1) + " col: "+exp.col+"]");
                System.err.println("[ERROR] Boolean operation must be between INT not VOID [row: "+(exp.row +1) + " col: "+exp.col+"]");
                exp.type = -1;
            } else {
                exp.type = exp.left.type;
            }
        } else {
            exp.type = -1;
        }

    }

    public void visit(RepeatExp exp, int level) {
        exp.exps.accept(this, level);
        exp.test.accept(this, level);
    }

    public void visit(VarExp exp, int level) {
        exp.value.accept(this, level);
        exp.type = getVarType(exp.value.name);
    }

    public void visit(WriteExp exp, int level) {
        exp.output.accept(this, ++level);
    }

    public void visit(NameTy t, int level) {
        
        String typeString = "";
        if (t.type == 0) {
            typeString = "int";
        } else {
            typeString = "void";
        }

    }

    public void visit(VarDecList varDecList, int level) {
        while (varDecList != null && varDecList.head != null) {
            varDecList.head.accept(this, level);
            varDecList = varDecList.tail;
        }
    }

    public void visit(DecList decList, int level) {
        while (decList != null && decList.head != null) {
            decList.head.accept(this, level);
            decList = decList.tail;
        }
    }

    public void visit(ArrayDec arr, int level) {

        arr.type.accept(this, ++level);
        if (symtable.containsKey(arr.name))
            if (symtable.get(arr.name).size() > 0 && symtable.get(arr.name).get(symtable.get(arr.name).size()-1).depth == depth){
                System.out.println("[Error] "+arr.name+" already defined [row: "+(arr.row +1) + " col: "+arr.col+"]");
                System.err.println("[Error] "+arr.name+" already defined [row: "+(arr.row +1) + " col: "+arr.col+"]");
            }
        //Add to table
        addEntryToTable(arr, arr.name, arr.type.type);

        if (arr.size != null)
            arr.size.accept(this, ++level);
    }

    public void visit(SimpleDec dec, int level) {
        if (symtable.containsKey(dec.name))
            if ( symtable.get(dec.name).size() > 0 && symtable.get(dec.name).get(symtable.get(dec.name).size()-1).depth == depth){
                System.out.println("[Error] "+dec.name+" already defined [row: "+(dec.row +1) + " col: "+dec.col+"]");
                System.err.println("[Error] "+dec.name+" already defined [row: "+(dec.row +1) + " col: "+dec.col+"]");
            }
        //Add to table
        addEntryToTable(dec, dec.name, dec.type.type);
        dec.type.accept(this, ++level);
    }

    public void visit(FunctionDec dec, int level) {

        dec.type.accept(this, ++level);
        //Add the function to table
        if (sFlag == 1)
            System.out.println("");
        currFunction = addEntryToTable(dec, dec.func, dec.type.type);
        currFunction.params = new ArrayList<>();

        //Add the parameters to the new block depth and increment depth to go to next block
        indent(++depth);
        if (sFlag == 1)
            System.out.println("Params: ");
            depth++;
        dec.params.accept(this, ++level);

        //decrementing depth 
        depth--;
        indent(depth);
        if (sFlag == 1)
            System.out.println("");
        //Store parameters
        VarDecList list = dec.params;
        while (list != null && list.head != null) {
            currFunction.params.add(list.head);
            list = list.tail;
        }

        //leave parameter depth and outputting
        depth--;
        dec.body.accept(this, ++level);
        System.out.println(dec.func + " -> " + NameTy.types[dec.type.type]);
        currFunction = null; 
    }

    public void visit(CompoundExp exp, int level) {
        //Enter a compound expression block, depth must be 0
        if (depth>0 && sFlag == 1) {
            indent(depth);
            System.out.println("Entering a new block: ");
        }
        depth++;
        if (exp.decs != null)
            exp.decs.accept(this, ++level);
        if (exp.exps != null)
            exp.exps.accept(this, ++level);

        //leave compound block and clearing symbol table
        clearSymTable(depth);
        depth--;
        if (sFlag == 1) {
            indent(depth);
            System.out.println("Leaving the block");
        }
    }

    public void visit(ReturnExp e, int level) {
        if (e.exp != null) {
            e.exp.accept(this, ++level);
            if (e.exp.type >= 0 && currFunction.type >= 0 && e.exp.type != currFunction.type) {
                indent(depth);
                System.out.println("[ERROR] Return type must be ("+NameTy.types[currFunction.type]+"), found ("+NameTy.types[e.exp.type]+") [row: "+(e.exp.row +1) + " col: "+e.exp.col+"]");
                System.err.println("[ERROR] Return type must be ("+NameTy.types[currFunction.type]+"), found ("+NameTy.types[e.exp.type]+") [row: "+(e.exp.row +1) + " col: "+e.exp.col+"]");
            }
        } else if (currFunction!= null && currFunction.type != NameTy.VOID) {
            indent(depth);
        }
    }

    public void visit(WhileExp exp, int level) {
        exp.test.accept(this, ++level);
        if (exp.test.type == NameTy.VOID) {
            indent(depth);
            System.out.println("[ERROR] Void expression found in while [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Void expression found in while [row: "+(exp.row +1) + " col: "+exp.col+"]");
        }
        exp.body.accept(this, ++level);
    }

    public void visit(CallExp exp, int level) {
        if (!symtable.containsKey(exp.func) || symtable.get(exp.func).size() == 0) {
            indent(depth);
            System.out.println("[ERROR] Undefined function: " + exp.func + " [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Undefined function: " + exp.func + " [row: "+(exp.row +1) + " col: "+exp.col+"]");
            exp.type = -1;
            return;
        } else if(!(symtable.get(exp.func).get(symtable.get(exp.func).size()-1).dec instanceof FunctionDec)) {
            indent(depth);
            System.out.println("[ERROR] Function '" + exp.func + "' is a Var [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.println("[ERROR] Function '" + exp.func + "' is a Var [row: "+(exp.row +1) + " col: "+exp.col+"]");
            exp.type = -1;
            return;
        }
        exp.type = getVarType(exp.func);
        exp.args.accept(this, ++level);

        ArrayList<Dec> params = symtable.get(exp.func).get(0).params;
        ExpList args = exp.args;

        //Check for absence of parameters
        if (params == null || params.size() == 0 && args.head == null)
            return;

        ArrayList<Exp> argsArray = new ArrayList<>();
        while (args != null && args.head != null) {
            argsArray.add(args.head);
            args = args.tail;
        }

        //Check if the number of parameters match
        if (params.size() != argsArray.size()) {
            indent(depth);
            System.out.print("[ERROR] Arguments do not match function expected: "+exp.func+"("+formatParamsString(params)+") got "+exp.func+"("+formatArgsString(argsArray)+")");
            System.out.println(" [row: "+(exp.row +1) + " col: "+exp.col+"]");
            System.err.print("[ERROR] Arguments do not match function expected: "+exp.func+"("+formatParamsString(params)+") got "+exp.func+"("+formatArgsString(argsArray)+")");
            System.err.println(" [row: "+(exp.row +1) + " col: "+exp.col+"]");
            return;
        }

        //check if arguments match parameters
        for (int i = 0; i < params.size(); i++) {
            boolean error = false;
            //Check for mismatched types
            if (params.get(i).type.type != argsArray.get(i).type) {
                error = true;
            }
            //Checking for arrays
            else if (params.get(i) instanceof ArrayDec) {
                Exp e = argsArray.get(i);
                if (e instanceof VarExp) {
                    String varName = ((VarExp)e).value.name;
                    if (symtable.containsKey(varName) && symtable.get(varName).size() > 0) {
                        if (!(symtable.get(varName).get(symtable.get(varName).size()-1).dec instanceof ArrayDec))
                            error = true;
                    } else {
                        error = true;
                    }
                }
            }
            if (error) {
                indent(depth);
                System.out.print("[ERROR] Arguments do not match function expected: "+exp.func+"("+formatParamsString(params)+") got "+exp.func+"("+formatArgsString(argsArray)+")");
                System.out.println(" [row: "+(exp.row +1) + " col: "+exp.col+"]");
                System.err.print("[ERROR] Arguments do not match function expected: "+exp.func+"("+formatParamsString(params)+") got "+exp.func+"("+formatArgsString(argsArray)+")");
                System.err.println(" [row: "+(exp.row +1) + " col: "+exp.col+"]");
                return;
            }
        }

    }

    public void visit(NilExp exp, int level) {

    }

    public void visit(IndexVar var, int level) {
        if (!symtable.containsKey(var.name) || symtable.get(var.name).size() == 0) {
            indent(depth);
            System.out.println("[ERROR] Undefined use of: "+var.name + " [row: "+(var.row +1) + " col: "+var.col+"]");
            System.err.println("[ERROR] Undefined use of: "+var.name + " [row: "+(var.row +1) + " col: "+var.col+"]");
        } else if(symtable.get(var.name).get(symtable.get(var.name).size()-1).dec instanceof FunctionDec) {
            indent(depth);
            System.out.println("[ERROR] Var '" + var.name + "' is a function [row: "+(var.row +1) + " col: "+var.col+"]");
            System.err.println("[ERROR] Var '" + var.name + "' is a function [row: "+(var.row +1) + " col: "+var.col+"]");
        }
        var.index.accept(this, ++level);

        if (var.index.type >= 0 && var.index.type != NameTy.INT) {
            indent(depth);
            System.out.println("[ERROR] Index variable not type INT [row: "+(var.row +1) + " col: "+var.col+"]");
            System.err.println("[ERROR] Index variable not type INT [row: "+(var.row +1) + " col: "+var.col+"]");
        }
    }

    public void visit(SimpleVar var, int level) {
        if (!symtable.containsKey(var.name) || symtable.get(var.name).size() == 0) {
            indent(depth);
            System.out.println("[ERROR] Undefined use of: "+var.name + " [row: "+(var.row +1) + " col: "+var.col+"]");
            System.err.println("[ERROR] Undefined use of: "+var.name + " [row: "+(var.row +1) + " col: "+var.col+"]");
        } else if(symtable.get(var.name).get(symtable.get(var.name).size()-1).dec instanceof FunctionDec) {
            indent(depth);
            System.out.println("[ERROR] Var '" + var.name + "' is a function [row: "+(var.row +1) + " col: "+var.col+"]");
            System.err.println("[ERROR] Var '" + var.name + "' is a function [row: "+(var.row +1) + " col: "+var.col+"]");
        }
    }

    public void visit(ErrorExp exp, int level ){
        indent(depth);
        System.out.println("null");
    }

    //method to print to file
    public String tableString(){
        System.out.flush();
        System.setOut(prev);
        return baos.toString();
    }
}
