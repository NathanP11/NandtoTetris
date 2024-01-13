package jackCompiler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



public class VMWriter {
	private File output;
	private FileWriter myWriter;
	private String ls; //lineSeparator
	private boolean test; 
	
	VMWriter(File f, String fileName){
		ls = "\n"; //System.lineSeparator();
		String newFileName = "";
		if(fileName.lastIndexOf(".")>0) {newFileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".vm";}
		else{ newFileName = fileName + File.separator + fileName.substring(0,fileName.length()) + ".vm";}
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.
		Path filepath = currentDir.resolve(newFileName);
		output = new File( filepath.toString() );
		try { myWriter = new FileWriter(output); 
			}catch (IOException e) {System.out.println("Error Opening File.");	e.printStackTrace();	}
		test = true;
	}//constructor
	
	public void writePush(String segment, int index) { try { //push local 5
		myWriter.write( "push " + segment + " " + index + ls);
		if(test) {	System.out.print("push " + segment + " " + index + ls);	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writePop(String segment, int index) { try {
		myWriter.write( "pop " + segment + " " + index + ls);
		if(test) {	System.out.print( "pop " + segment + " " + index + ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeArithmetic(String cmd ) { try {
		//consider analyzing here
		myWriter.write( cmd + ls);
		if(test) {	System.out.print( cmd + ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeLabel( String label ) { try {
		myWriter.write( "label " + label + ls);
		if(test) {	System.out.print( "label " + label + ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeGoto(String label) { try {
		myWriter.write( "goto " + label +  ls);
		if(test) {	System.out.print( "goto " + label +  ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeIf(String label) { try {
		myWriter.write( "if-goto " + label +  ls );
		if(test) {	System.out.print( "if-goto " + label +  ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeCall( String name, int nArgs) { try {
		myWriter.write( "call " + name + " " + nArgs + ls );
		if(test) {	System.out.print( "call " + name + " " + nArgs + ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writeFunction(String name, int nLocals) { try {
		myWriter.write( "function " + name + " " + nLocals + ls );
		if(test) {	System.out.print( "function " + name + " " + nLocals + ls );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }
	
	public void writereturn() { try {
		myWriter.write( "return" + ls );
		if(test) {	System.out.print(  "return" + ls  );	}
	}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();} }

	
	public void closeFile() {try { 
		myWriter.close();
	}catch (IOException e) {    System.out.println("Error Closing File."); e.printStackTrace();	} }
}
