package jackCompiler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

//import org.apache.commons.io.*;

public class Tokenizer {
	private File output;
	private FileWriter myWriter;
	private StringBuilder curr;
	private StringBuilder file;
	private int index; // file string builder index tracker
	private HashSet<String> keywordMap = new HashSet<String>();;
	private HashSet<String> symbolMap;
	private HashSet<String> whiteMap;
	private boolean hasNext;
	private String type;
	
	public Tokenizer( File f , String fileName){
		try { 
			FileInputStream fisTargetFile = new FileInputStream(f);
			//String s = IOUtils.toString(fisTargetFile, "UTF-8");
			String s = readFiletoString(f);
			
			//s=s.toLowerCase();
			file = new StringBuilder(s);
			
		}catch (IOException e) {    System.out.println("Error Opening File input stream."); e.printStackTrace();	}
		
		// output file for *T.xml (tokens)
	
		String newFileName = "";
		if(fileName.lastIndexOf(".")>0) {newFileName = fileName.substring(0,fileName.lastIndexOf(".")) + "T.xml";}
		else{ newFileName = fileName + File.separator + fileName.substring(0,fileName.length()) + "T.xml";}
		
		
		
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.
		Path filepath = currentDir.resolve(newFileName);
		//System.out.println(filepath);
		
		output = new File( filepath.toString() );
		
		
		
		try { myWriter = new FileWriter(output); 
		}catch (IOException e) {System.out.println("Error Opening File output_T.xml");	e.printStackTrace();	}
		
		keywordMap = new HashSet<String>();	symbolMap = new HashSet<String>(); whiteMap = new HashSet<String>();
		
		keywordMap.add("class");	keywordMap.add("constructor");	keywordMap.add("function");	keywordMap.add("method");		
		keywordMap.add("field");	keywordMap.add("static");	keywordMap.add("var");	keywordMap.add("int");		
		keywordMap.add("char");	keywordMap.add("boolean");	keywordMap.add("void");	keywordMap.add("true");
		keywordMap.add("false");	keywordMap.add("null");		keywordMap.add("this");	keywordMap.add("let");
		keywordMap.add("do");	keywordMap.add("if");		keywordMap.add("else");	keywordMap.add("while");
		keywordMap.add("return");
		
		symbolMap.add("{");	symbolMap.add("}");	symbolMap.add("(");	symbolMap.add(")");
		symbolMap.add("[");	symbolMap.add("]");	symbolMap.add(".");	symbolMap.add(",");
		symbolMap.add(";");	symbolMap.add("+");	symbolMap.add("-");	symbolMap.add("*");
		symbolMap.add("/");	symbolMap.add("&");	symbolMap.add("|");	symbolMap.add("<");
		symbolMap.add(">");	symbolMap.add("=");	symbolMap.add("~");
		
		whiteMap.add(" ");	whiteMap.add("\r");	whiteMap.add("\n");	whiteMap.add(System.lineSeparator());
		
		type = "";
		curr = new StringBuilder();
		hasNext = true;
		
		removeComments();
		removeSpaces();
		
		//System.out.println(file);
		//print entire file as string
		
		try {
		myWriter.write( "<tokens>" + System.lineSeparator());
		//System.out.println("<tokens>");
		advance();
		while( hasMoreTokens() ) {	
			print(myWriter);
			//System.out.println("<" + type + "> " + curr + " </" + type + ">");	
			advance();
			}
		myWriter.write( "</tokens>" + System.lineSeparator());
		//System.out.println("</tokens>");
		
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		index = 0; curr = null; // reset the tokenizer after making the xml file
	}
	
//=============================================================================================
	public String value() { return curr.toString();} //getter
	public String type() { return type;} //getter
//=============================================================================================
	
	//set curr to null when no more tokens
	public void advance() {
		
		StringBuilder sb = new StringBuilder();
		boolean isToken = false;
		type = "";
			
		while( !isToken ) {
			if( index+1 == file.length()) { 
				hasNext = false;	curr = null;
				return;
				}
			else {
				String currChar = String.valueOf(file.charAt(index));
				String nextChar = String.valueOf(file.charAt(index+1));
				if(currChar.equals("\"")) { //grab full string
					type = "stringConstant";
					sb.append(currChar); index++; currChar = String.valueOf(file.charAt(index));
					while(!currChar.equals("\"")) {	
						sb.append(currChar); index++;
						currChar = String.valueOf(file.charAt(index));
					}
					sb.append(currChar); index++;
					isToken = true;
				}
				else {
					if( whiteMap.contains(currChar)) { //currChar.equals("\n") || currChar.equals(System.lineSeparator()) || currChar.equals("\r") ) {
						index ++;
					}
					else {
						if(  (symbolMap.contains(nextChar) || whiteMap.contains(nextChar))   ) { //sb.length() > 0 &&
							//look ahead 1 to see when to stop
							// stop if you have a token and the next char is a space, or symbol
							isToken = true;
							sb.append(currChar);
							index++;
						}
						else {
							if ( !currChar.equals(" ")) {
								sb.append(currChar);
								index++;
							}
							else {index++;}
						}
						if(symbolMap.contains(currChar)) {  type = "symbol"; isToken = true;}
						
					}//else new line
				}//else end string constant
			}//else end of file
		} //while

		//assign type
		boolean isNumber = true;
		for(int i = 0; i < sb.length(); i++) { 
			if( !Character.isDigit(sb.charAt(i)) ) {isNumber=false;}
		}
		if(isNumber) {type = "integerConstant";}
		if(keywordMap.contains(sb.toString())) {type = "keyword";}
		if( type.equals("")) { type = "identifier";	} // default categorization
		
		curr = sb;
	}
	
	public void closeFile() {
		try { myWriter.close();
			}catch (IOException e) {    System.out.println("Error Closing File."); e.printStackTrace();	}
	}
	
	public void print( FileWriter abc ) {
		try { 
			String s = curr.toString();
			if(s.equals("<")) {	abc.write( "<" + type + "> " + "&lt;" + " </" + type + ">"+ "\n");	return;	}
			else if(s.equals(">")) {abc.write( "<" + type + "> " + "&gt;" + " </" + type + ">"+ "\n");return;	}
			else if(s.equals("&")) {abc.write( "<" + type + "> " + "&amp;" + " </" + type + ">"+ "\n");return;	}
			else if(s.equals("\"")) {abc.write( "<" + type + "> " + "&quot;" + " </" + type + ">"+ "\n");return;	}
			else if(type.equals("stringConstant")) {
				String temp = s.substring(1,s.length()-1);
				abc.write( "<" + type + "> " + temp + " </" + type + ">"+ "\n");return;	}
			else abc.write( "<" + type + "> " + curr + " </" + type + ">"+ "\n");
		
		
		
			}catch (IOException e) {    System.out.println("Error Writing to File."); e.printStackTrace();	}
	}
	
	public boolean hasMoreTokens() {return hasNext;}
	
	private void removeComments() {
		while( file.indexOf("/*") != -1 ) {	file.delete(file.indexOf("/*"), file.indexOf("*/")+2); }
		
		
		while( file.indexOf("//") != -1 ) {	
			int start = file.indexOf("//");
			String ls = System.lineSeparator();
			int stop = file.indexOf(ls,start+1);
			if(stop < 0 ) {stop = file.indexOf("\n",start+1);}
			if(stop < 0 ) {stop = file.indexOf("\r",start+1);}
			stop+=1;
			//String deleteThis = file.substring(start,stop);
			file.delete(start, stop);	} //System.lineSeparator()
	}
	private void removeSpaces() {
		//delete all spaces at the begining of a line
		int i = 0;		
		while( file.indexOf(System.lineSeparator(), i ) != -1) { // if there is no next line stop
			while( file.charAt(0) == ' ' );
			i = 1 + file.indexOf(System.lineSeparator(), i );
		}
		
		// delete all duplicate spaces by copying into new string builder
		StringBuilder abc = new StringBuilder();
		i = 0;
		while(i<file.length()) {
			if( !( file.charAt(i) == '\t')  ){
				abc.append(file.charAt(i));
			}
			i++;
		}
		file = abc;
	}
	
	
	
	
	
	
	
	
	
	private String readFiletoString(File file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	
	
}
