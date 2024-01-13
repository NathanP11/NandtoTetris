package VMTranslator;
import java.io.File;
import org.apache.commons.io.*;

public class VMTranslator {
	static CodeWriter myWriter;
	
	public VMTranslator(){
		
	}
	
	public static void main(String[] args) {
		//System.out.println("Reading File: " + args[0]);
		myWriter = new CodeWriter(args[0]);
		File dir = new File(args[0]);
		
        if (dir.isDirectory()) {
            //System.out.println("Directory: " + dir.getAbsolutePath());
        	
        	myWriter.writeInit(); // bootstrap call
        	
            for( File f : dir.listFiles()) {
            	String FileExt = f.getName();
            	FileExt = FileExt.substring(FileExt.lastIndexOf("."), FileExt.length());
            	if(FileExt.equals(".vm") ) {
            		System.out.println("Processing VM File in Directory: " + f.getAbsolutePath());
            		translateFile(f);
            	}
            }
        } else {
            System.out.println("Processing Single File: " + dir.getAbsolutePath());
            translateFile(dir);
        }
    
		

		myWriter.closeFile();
	}
	
	public static void translateFile(File f) {
		parser myParser = new parser(f);	
		String fileName = f.getName().substring(0,f.getName().lastIndexOf("."));
		while(myParser.hasMoreCommands()) {
			//iterate thru all commands
			myParser.advance();
			myWriter.writeLine("//" + myParser.line);
			
			System.out.println("//" + myParser.line);
			System.out.println("commandType: " +myParser.commandType());
			//System.out.println("arg1: " +myParser.arg1());
			//System.out.println("arg2: " + myParser.arg2());
			
			String C_TYPE = myParser.commandType();
			if(C_TYPE.equals("C_ARITHMETIC")) {	myWriter.writeArithmetic(myParser.arg1());	}
			if(C_TYPE.equals("C_PUSH") || C_TYPE.equals("C_POP") ) { myWriter.writePushPop(C_TYPE,myParser.arg1(),myParser.arg2(),fileName);	}
			
			if(C_TYPE.equals("C_IF")) {		myWriter.writeIf(myParser.arg1());	}
			if(C_TYPE.equals("C_LABEL")) {	myWriter.writeLabel(myParser.arg1());	}
			if(C_TYPE.equals("C_GOTO")) {	myWriter.writeGoto(myParser.arg1());}
			
			if(C_TYPE.equals("C_FUNCTION")) {myWriter.writeFunction(myParser.arg1(),myParser.arg2());}
			if(C_TYPE.equals("C_RETURN")) {	myWriter.writeReturn(fileName);	}
			if(C_TYPE.equals("C_CALL")) {	myWriter.writeCall(myParser.arg1(),myParser.arg2());}
		}
	}
}
