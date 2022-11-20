/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the scanner, tiny.flex, and the parser, tiny.cup, have been created.
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import absyn.*;
   
class Main {
  public final static boolean SHOW_TREE = true;
  static public void main(String argv[]) {    
    boolean SHOW_TREE = false;
    boolean generateTM = false;
		int sFlag = 0; // Assume the -s was not passed in
		String fileName = "";
    File toWriteTo = null;
    OutputStream outputStreamTable= null;
    OutputStream outputStreamTree = null;

    for(int i=0; i< argv.length; i++)
		{
			if (argv[i].equals("-s")) {
				sFlag = 1;
			} else if (argv[i].equals("-a")) {
				SHOW_TREE = true;
      } else if (argv[i].equals("-c")) {
				generateTM = true;
			} else {
				fileName = argv[i];
			}
		}
    
    //removing .cm file extension
    fileName = fileName.replaceAll(".cm", "");
    if (sFlag == 1){
      try {
        //adding .sym extension
        toWriteTo = new File(fileName + ".sym");  
        //changing output stream to file
        outputStreamTable= new FileOutputStream(toWriteTo);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (SHOW_TREE){
      try {
        //adding .abs extension
        toWriteTo = new File(fileName + ".abs");
        //changing output stream to file
        outputStreamTree = new FileOutputStream(toWriteTo); 
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    try {
      parser p = new parser(new Lexer(new FileReader(fileName + ".cm")));
      Absyn result = (Absyn)(p.parse().value);
      if (result != null) {
				SemanticAnalyzer analyzer = new SemanticAnalyzer(sFlag);
				result.accept(analyzer, 0);
        if (sFlag == 1){
          outputStreamTable.write(analyzer.tableString().getBytes());
        }
			}      
      if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         ShowTreeVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
         outputStreamTree.write(visitor.treeString().getBytes());
      }
      if (generateTM){
        TMGenerator asm = new TMGenerator(fileName);
				result.accept(asm, 0);
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}


