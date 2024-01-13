package VMTranslator;
import java.io.File;
import org.apache.commons.io.*;

public class parser {
	File f;
	LineIterator it; //file iterator
	String line; //current command
	
	parser(File inputFile){//constructor
		f = inputFile;
		try{		it = FileUtils.lineIterator(f, "UTF-8"); }
		catch (Exception ignore) {};
		line = "";
	}
	
	public boolean hasMoreCommands() {
		return it.hasNext();
	}
	
	public void advance() { //advance to next command ignoring comments
		line = it.nextLine();
		line = removeSpace(line);
		while(line.isEmpty()) {
			line = it.nextLine();
			line = removeSpace(line);
		}
	}
	
	public String commandType() {
		String cmd = "";
		if(line.indexOf(' ')==-1) {cmd = line.substring(0,line.length());}
		else {cmd = line.substring(0,line.indexOf(' ')); }
		//done initializing
		
		if(cmd.toLowerCase().equals("push")) {return "C_PUSH";}
		if(cmd.toLowerCase().equals("pop")) {return "C_POP";}
		if(cmd.toLowerCase().equals("add")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("sub")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("neg")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("eq")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("gt")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("lt")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("and")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("or")) {return "C_ARITHMETIC";}
		if(cmd.toLowerCase().equals("not")) {return "C_ARITHMETIC";}
		//more arithmetic here
		if(cmd.toLowerCase().equals("label")) {return "C_LABEL";}
		if(cmd.toLowerCase().equals("goto")) {return "C_GOTO";}
		if(cmd.toLowerCase().equals("if-goto")) {return "C_IF";}
		if(cmd.toLowerCase().equals("function")) {return "C_FUNCTION";}
		if(cmd.toLowerCase().equals("return")) {return "C_RETURN";}
		if(cmd.toLowerCase().equals("call")) {return "C_CALL";}
		return cmd;
	}
	
	public String arg1() {//substring of first space to second space
		String t = commandType();
		if(t.equals("C_ARITHMETIC")) { //if arithmetic, return add,sub, etc...
			return line.substring(0,line.length());
		}
		else if (t.equals("C_LABEL") || t.equals("C_GOTO") || t.equals("C_IF")) {return line.substring(line.indexOf(' ')+1,line.length());}
		String output = line.substring(line.indexOf(' ')+1,line.lastIndexOf(' '));
		if(output.equals("local")) {output = "LCL";}
		if(output.equals("argument")) {output = "ARG";}
		if(output.equals("this")) {output = "THIS";}
		if(output.equals("that")) {output = "THAT";}
		return output;
	}
	
	public int arg2() {
		String abc  = "-1";
		if(commandType().equals("C_PUSH") || commandType().equals("C_POP") || commandType().equals("C_CALL") || commandType().equals("C_FUNCTION")) {
			abc = line.substring(line.lastIndexOf(' ')+1,line.length());
		}
		return Integer.parseInt(abc);
	}
	
    private String removeSpace( String s){
        StringBuilder sb = new StringBuilder();
        if( s.indexOf("//") != -1){ // delete comments
            s = s.substring(0,s.indexOf("//"));
        }     
        for (int k = 0; k < s.length();k++){
                sb.append(s.charAt(k));
       }
        if( sb.length()>0) {
        	while( sb.charAt(sb.length()-1) == ' ' ) { sb.deleteCharAt(sb.length()-1);}
        	while( sb.charAt(sb.length()-1) == '\t' ) { sb.deleteCharAt(sb.length()-1);}
        }
        return sb.toString();
    }
    
	public static void main(String[] args) {	}

}
