package VMTranslator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.io.*;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class CodeWriter {
	int k = 0; // program counter 
	File f;
	FileWriter myWriter;
	int j = 0;
	
	CodeWriter(String fileName){ //constructor
		try {
		String newFileName = "";
		if(fileName.lastIndexOf(".")>0) {newFileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".asm";}
		else{ newFileName = fileName + File.separator + fileName.substring(0,fileName.length()) + ".asm";}
        f = new File(newFileName);
        myWriter = new FileWriter(f);
		} catch (IOException e) {
			System.out.println("Error Opening File.");
			e.printStackTrace();	}
        ////////////////////////////////////////////////////////////////////////////////// 
	}
	
	public void writePushPop(String cmd, String type, int index, String fileName) {
		//to do
		Boolean trigger = false;
		try {
			if(type.equals("pointer")) {
				index = index + 3;
				type = "constant"; 			
				trigger = true;}
			if(type.equals("temp")) {
				index = index + 5;
				type = "constant"; 			
				trigger = true;}
			
		if(cmd.equals("C_PUSH")) {
			if(type.equals("static")) {
				myWriter.write("@"+ fileName + "." + index  + "\r\n");
				System.out.println("static output push: @"+ fileName + "." + index  + "\r\n");
				trigger = true;
				type = "constant";
			}
			else {myWriter.write("@"+ index + "\r\n");}  //@index  
			
			myWriter.write("D=A"+ "\r\n"); 
			if(!(type.equals("constant"))) {
				myWriter.write("@"+ type + "\r\n");  //@LCL,ARG,THIS,THAT
				myWriter.write("A=D+M"+ "\r\n"); //BASE ADDRESS + INDEX
				myWriter.write("D=M"+ "\r\n"); 	//SAVE OUTPUT TO D REGISTER
			}
			if(trigger) {   //pointer & temp
				myWriter.write("D=M"+ "\r\n"); //BASE ADDRESS + INDEX
				//myWriter.write("D=A"+ "\r\n"); 	//SAVE OUTPUT TO D REGISTER
			}
			myWriter.write("@SP"+ "\r\n"); 		//GET STACK PTR
			myWriter.write("A=M"+ "\r\n"); 		//ACCESSES STACK PTR
			myWriter.write("M=D"+ "\r\n"); 		//WRITE TO STACK
			myWriter.write("@SP"+ "\r\n"); 		//
			myWriter.write("M=M+1"+ "\r\n"); 	//SP++
		}
		if(cmd.equals("C_POP")) {
			if(type.equals("static")) {
				myWriter.write("@"+ fileName + "." + index  + "\r\n");
				System.out.println("static output pop: @"+ fileName + "." + index  + "\r\n");
				type = "constant";
			}
			else {myWriter.write("@"+ index + "\r\n"); 		}
			
			myWriter.write("D=A"+ "\r\n"); 
			
			if(!(type.equals("constant"))) {
				myWriter.write("@"+ type + "\r\n"); 		
				myWriter.write("D=D+M"+ "\r\n"); 
			}
			myWriter.write("@SP" + "\r\n"); 		
			myWriter.write("M=M-1"+ "\r\n"); 
			myWriter.write("A=M+1"+ "\r\n"); 
			myWriter.write("M=D"+ "\r\n"); 
			myWriter.write("A=A-1"+ "\r\n"); 
			myWriter.write("D=M"+ "\r\n"); 
			myWriter.write("A=A+1"+ "\r\n");
			myWriter.write("A=M"+ "\r\n");
			myWriter.write("M=D"+ "\r\n");
		}

		}catch (IOException e) {
		     System.out.println("Error Writing to File.");
		     e.printStackTrace();}
	}
	
	public void writeArithmetic(String cmd) {
		// pop 1 value into temporary memory, 1 value into D register
		
		try {//2 parameter computations
			if( !(cmd.equals("neg") || cmd.equals("not")) )  {
				//do this in main myWriter.write("//" + cmd + "\r\n");
				myWriter.write("@SP"+ "\r\n");     
				myWriter.write("AM=M-1"+ "\r\n");  //SP--
				myWriter.write("D=M"+ "\r\n");     //put stack pointer tgt in D
				myWriter.write("A=A-1"+ "\r\n");   //get 2nd stack data pointer
				myWriter.write("A=M"+ "\r\n");	   //get 2nd stack data
				
				if(cmd.equals("add")) { myWriter.write("D=D+A"+ "\r\n"); }
				if(cmd.equals("sub")) { myWriter.write("D=A-D"+ "\r\n"); }
				
				if(cmd.equals("eq")) { 
					myWriter.write("D=D-A"+ "\r\n"); 
					myWriter.write("@assign1"+ k + "\r\n");
					myWriter.write("D;JEQ"+ "\r\n");
					myWriter.write("@assign0"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
					
					myWriter.write("(assign1"+ k + ")" + "\r\n");
					myWriter.write("D=-1"+ "\r\n");
					myWriter.write("@stackWrite"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
					
					myWriter.write("(assign0"+ k + ")" + "\r\n");
					myWriter.write("D=0"+ "\r\n");
					myWriter.write("@stackWrite"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n"); 
					
				}
				
				//not done yet
				if(cmd.equals("gt")) { 
					myWriter.write("D=D-A"+ "\r\n");
					myWriter.write("@assign1"+ k + "\r\n");
					myWriter.write("D;JLT"+ "\r\n"); //important line
					myWriter.write("@assign0"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
	
					myWriter.write("(assign1"+ k + ")" + "\r\n");
					myWriter.write("D=-1"+ "\r\n");
					myWriter.write("@stackWrite"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
					
					myWriter.write("(assign0"+ k + ")" + "\r\n");
					myWriter.write("D=0"+ "\r\n");
					myWriter.write("@stackWrite"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n"); 
					
				}
					
				if(cmd.equals("lt")) { 
					myWriter.write("D=D-A"+ "\r\n");
					myWriter.write("@assign1"+ k + "\r\n");
					myWriter.write("D;JGT"+ "\r\n");//important line
					myWriter.write("@assign0"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
	
					myWriter.write("(assign1"+ k + ")" + "\r\n");
					myWriter.write("D=-1"+ "\r\n");
					myWriter.write("@stackWrite"+ k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n");
					
					myWriter.write("(assign0"+ k + ")" + "\r\n");
					myWriter.write("D=0"+ "\r\n");
					myWriter.write("@stackWrite" + k + "\r\n");
					myWriter.write("0;JMP"+ "\r\n"); 
					
					}
				
				if(cmd.equals("and")) { myWriter.write("D=D&A"+ "\r\n"); }
				if(cmd.equals("or")) { myWriter.write("D=D|A"+ "\r\n"); }
				// write to stack
				
				myWriter.write("(stackWrite" + k + ")"+ "\r\n");
				myWriter.write("@SP"+ "\r\n");
				myWriter.write("A=M-1"+ "\r\n");
				myWriter.write("M=D"+ "\r\n");
				k++;
				return;
			}
			else {//1 parameter computations
				myWriter.write("@SP"+ "\r\n");
				myWriter.write("A=M-1"+ "\r\n");
				if(cmd.equals("neg")) { myWriter.write("M=-M"+ "\r\n"); }
				if(cmd.equals("not")) { myWriter.write("M=!M"+ "\r\n"); }
				return;
			}
		}catch (IOException e) {
		     System.out.println("Error Arithmetic to file Writing to File.");
		     e.printStackTrace();
		}
		return;
	}
	
	public void writeIf(String s) {
		try {
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			//decrement SP
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M-1" + "\r\n");
			
			myWriter.write("@" + s + "\r\n");
			myWriter.write("D;JNE" + "\r\n"); //-1 is true
		}catch (IOException e) {System.out.println("Error Writing If to File.");  e.printStackTrace();}
	}
	
	public void writeGoto(String s) {
		try {
			myWriter.write("@" + s + "\r\n");
			myWriter.write("0;JMP" + "\r\n");
		}catch (IOException e) {System.out.println("Error Writing If to File.");  e.printStackTrace();}
	}
	
	public void writeLabel(String s) {
		try {
			myWriter.write("(" + s + ")" + "\r\n");
		}catch (IOException e) {System.out.println("Error Writing If to File.");  e.printStackTrace();}
	}
	
	public void writeLine(String s) {
		try {
			myWriter.write(s + "\r\n");
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();
		}
	}
	
//===================================================================================================================================================
	
	public void setFileName(String s) {
		try {
			myWriter.write(s + "\r\n");
			//create new static
		}catch (IOException e) {System.out.println("Error setFileName to File."); e.printStackTrace();	}
	}
	
	public void writeInit() {
		try { //bootstrap code called once at begining of .asm writing
			//SP = 256
			myWriter.write( "@256" + "\r\n");
			myWriter.write( "D=A" + "\r\n");
			myWriter.write( "@SP" + "\r\n");
			myWriter.write( "M=D" + "\r\n");
			//LCL = 256
			myWriter.write( "@LCL" + "\r\n");
			myWriter.write( "M=D" + "\r\n");
						
			
			myWriter.write( "//Call Sys.init 0" + "\r\n");
			writeCall("Sys.init" , 0);
			
		}catch (IOException e) {System.out.println("Error setFileName to File."); e.printStackTrace();	}
	}
	
	public void writeFunction(String arg1, int nVars) {
		try { //initialize local segment
			myWriter.write("(" + arg1 + ")" + "\r\n");
			myWriter.write("@" + nVars + "\r\n");
			myWriter.write("D=A" + "\r\n");			//D is counter
			
			myWriter.write("(StartLoop" + j + ")" + "\r\n");	
			myWriter.write("@EndLoop" + j + "\r\n");	
			
			myWriter.write("D;JEQ" + "\r\n");		// END LOOP IF D == 0
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=0" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			myWriter.write("D=D-1" + "\r\n");
			myWriter.write("@StartLoop" + j + "\r\n");
			myWriter.write("0;JMP" + "\r\n");
			myWriter.write("(EndLoop" + j + ")" + "\r\n");	
			
			j++;
			
		}catch (IOException e) {System.out.println("Error setFileName to File."); e.printStackTrace();	}
	}
	
	
	//=----------------------------------------------------------------------------------
	public void writeCall(String arg1, int nArgs) {
		try { //PUSH RETURN ADDRESS
			myWriter.write("@" + arg1 + "$callRet" + j +"\r\n");
			myWriter.write("D=A" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			//PUSH LCL
			myWriter.write("@LCL" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			//PUSH ARG
			myWriter.write("@ARG" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			//PUSH THIS
			myWriter.write("@THIS" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			//PUSH THAT
			myWriter.write("@THAT" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=M+1" + "\r\n");
			//ARG=SP-5-nArgs
			myWriter.write("@5" + "\r\n");
			myWriter.write("D=A" + "\r\n");
			myWriter.write("@"+ nArgs + "\r\n");
			myWriter.write("D=D+A" + "\r\n"); //D=(5+nArgs)
			
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=A" + "\r\n");// initialize to SP (then count down to SP-5-nArg)
			
			//loop start
				myWriter.write("(LoopStart-call-" + j + ") " + "\r\n");
				myWriter.write("@SP" + "\r\n");
				myWriter.write("A=M" + "\r\n");
				myWriter.write("M=M-1" + "\r\n");
				myWriter.write("@LoopStart-call-" + j + "\r\n");
				myWriter.write("D=D-1;JNE" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@ARG" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//LCL=SP
			myWriter.write("@SP" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@LCL" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//goto functionName
			myWriter.write("@" + arg1 + "\r\n");
			myWriter.write("0;JMP" + "\r\n");
			// return address 
			myWriter.write("(" + arg1 + "$callRet" + j + ")" +"\r\n");
			
			j++;
			
		}catch (IOException e) {System.out.println("Error writeCall to File."); e.printStackTrace();	}
	}
	
	
	
	public void writeReturn(String fileName ) {
		try { //endFrame
			myWriter.write("@LCL" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@" + fileName + "$endFrame" + j  + "\r\n");  //endFrame Addr
			myWriter.write("M=D" + "\r\n");
			// return Address
			myWriter.write("@LCL" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("A=A-1" + "\r\n");
			myWriter.write("A=A-1" + "\r\n");
			myWriter.write("A=A-1" + "\r\n");
			myWriter.write("A=A-1" + "\r\n");
			myWriter.write("A=A-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@" + fileName + "$ret."+ j + "\r\n");   // ret addr
			myWriter.write("M=D" + "\r\n");
			//ARG = pop()
			myWriter.write("@SP" + "\r\n");
			myWriter.write("A=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@ARG" + "\r\n");
			myWriter.write("A=M" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//SP=ARG+1
			myWriter.write("@ARG" + "\r\n");
			myWriter.write("D=M+1" + "\r\n");
			myWriter.write("@SP" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//THAT = *(endFrame-1)
			myWriter.write("@" + fileName + "$endFrame" + j  + "\r\n");
			myWriter.write("AM=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@THAT" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//THIS = *(endFrame-2)
			myWriter.write("@" + fileName + "$endFrame" + j  + "\r\n");
			myWriter.write("AM=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@THIS" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//ARG = *(endFrame-3)
			myWriter.write("@" + fileName + "$endFrame" + j  + "\r\n");
			myWriter.write("AM=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@ARG" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//LCL = *(endFrame-4)
			myWriter.write("@" + fileName + "$endFrame" + j  + "\r\n");
			myWriter.write("AM=M-1" + "\r\n");
			myWriter.write("D=M" + "\r\n");
			myWriter.write("@LCL" + "\r\n");
			myWriter.write("M=D" + "\r\n");
			//GOTO RETURN ADDRESS
			myWriter.write("@" + fileName + "$ret."+ j + "\r\n");   // ret addr
			myWriter.write("A=M" + "\r\n");
			myWriter.write("0;JMP" + "\r\n");
			
			
			j++;
		}catch (IOException e) {System.out.println("Error setFileName to File."); e.printStackTrace();	}
	}
	
	
	
	
	
	
	public void closeFile() {
		try {
		myWriter.close();
		}catch (IOException e) {
		     System.out.println("Error Closing File.");
		     e.printStackTrace();
		}
	}
	
	
	//new class here
	
}
