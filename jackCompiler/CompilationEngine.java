package jackCompiler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class CompilationEngine {
	private File input, output;
	private FileWriter myWriter;
	private String ls; //lineSeparator
	private Tokenizer t;
	private HashSet<String> statementMap;
	private HashSet<String> opMap;
	private HashSet<String> constantMap;
	private SymbolTable symbolTable;
	
	public CompilationEngine(File f, String fileName){
		statementMap = new HashSet<String>(); statementMap.add("let"); statementMap.add("if"); statementMap.add("while"); statementMap.add("do");statementMap.add("return");
		opMap = new HashSet<String>(); opMap.add("+");opMap.add("-");opMap.add("*");opMap.add("/");opMap.add("&");opMap.add("|");opMap.add("<");opMap.add(">");opMap.add("=");
		constantMap = new HashSet<String>(); constantMap.add("true");constantMap.add("false");constantMap.add("null");constantMap.add("this");
		input = f;
		symbolTable = new SymbolTable();
		ls = "\n"; //System.lineSeparator();
		String newFileName = "";
		if(fileName.lastIndexOf(".")>0) {newFileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".xml";}
		else{ newFileName = fileName + File.separator + fileName.substring(0,fileName.length()) + ".xml";}
		//fname = fname.substring(0,fname.lastIndexOf(".")) + ".xml";
		
		
		
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.
		Path filepath = currentDir.resolve(newFileName);
		//System.out.println(filepath);
		
		output = new File( filepath.toString() );
		
		
		
		try { myWriter = new FileWriter(output); 
			}catch (IOException e) {System.out.println("Error Opening File.");	e.printStackTrace();	}
		t = new Tokenizer( input , fileName );
	}
	
	public void closeFile() {
		try { 
			t.closeFile();
			myWriter.close();
			}catch (IOException e) {    System.out.println("Error Closing File."); e.printStackTrace();	}
	}
	//types: identifier, keyword, integerConstant, symbol, StringConstant
	public void CompileClass() {
		try {
		t.advance();
		myWriter.write( "<class>" + ls);
		if(t.type().equals("keyword")) {
			t.print(myWriter);
			t.advance();
			
			identifier();
			
			if(t.value().equals("{")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in Class."); System.exit(-1);  }
			
			
			while(t.value().equals("static") || t.value().equals("field")) {
				myWriter.write( "<classVarDec>" + ls);
				CompileClassVarDec();
				myWriter.write( "</classVarDec>" + ls);
				//t.advance();
			} 
			
			
			while(t.value().equals("constructor") || t.value().equals("function") || t.value().equals("method")) {
				myWriter.write( "<subroutineDec>" + ls);
				CompileSubroutineDec();
				myWriter.write( "</subroutineDec>" + ls);
				//t.advance();
			} 
			
			if(t.value().equals("}")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in Class."); System.exit(-1);  }
			
			//if t.hasMore() //error because only 1 class per file in Jack
			
		} else { System.out.println("Error. Expected keyword in Class"); System.exit(-1);  }
		myWriter.write( "</class>" + ls);
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
	}
	

	public void CompileClassVarDec() {

		
			if(t.value().equals("static") || t.value().equals("field")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in classVarDec."); System.exit(-1);  }
			//type definition
			type();
			identifier();
			
			//
			while(t.value().equals(",") || t.value().equals("field")) {
				t.print(myWriter);
				t.advance();
				identifier();
			} 
			if(t.value().equals(";")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in classVarDec expected ;"); System.exit(-1);  }
			
			return;
	}
			


	
	public void CompileSubroutineDec() {
		symbolTable.startSubroutine();
		try {
		if(t.value().equals("constructor") || t.value().equals("function") || t.value().equals("method")) {	
			t.print(myWriter);	t.advance();
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		//type + void definition
		if(t.type().equals("identifier") || t.value().equals("int") || t.value().equals("char") || 
				t.value().equals("boolean") || t.value().equals("void") ) {
			t.print(myWriter);	t.advance();
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		identifier();
		
		if(t.value().equals("(")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		myWriter.write( "<parameterList>" + ls);
		while(!t.value().equals(")") ) { // 0 to * parameters
			CompileParameterList();
			//t.advance();
		} myWriter.write( "</parameterList>" + ls);
		
		if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		myWriter.write( "<subroutineBody>" + ls);
		CompileSubroutineBody();
		myWriter.write( "</subroutineBody>" + ls);
		
		//if(t.value().equals("}")) {	t.print(myWriter);	t.advance();
		//} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileParameterList() {
		
		if(t.value().equals(")")) {	return; //if no parameters, return and let subroutineDec handle ')'
		} // else compile parameters
		
		type();
		identifier(); // varName
		
		if(t.value().equals(")")) {	return; //if no parameters, return and let subroutineDec handle ')'
		} // else compile parameters
		
		while(t.value().equals(",")) {
			t.print(myWriter);
			t.advance();
			type();
			identifier();// varName
		} 
		return;
	}
	
	public void CompileSubroutineBody() {
		try {
		if(t.value().equals("{")) {	t.print(myWriter);	t.advance(); 
		} else { System.out.println("Error in SubroutineBody expected { ."); System.exit(-1);  }
		
		//boolean varDec = false;
		//if(t.value().equals("var")) {	varDec=true;  myWriter.write( "<varDec>" + ls); } 
		while(t.value().equals("var")) {
			myWriter.write( "<varDec>" + ls);
			CompileVarDec();
			myWriter.write( "</varDec>" + ls);
		} 
		//if(varDec) {	myWriter.write( "</varDec>" + ls); } 
		
		///////////////////////////////////
		
		CompileStatements();
		
		
		if(t.value().equals("}")) {	
			t.print(myWriter);	t.advance();
			return; //if no parameters, return and let subroutineDec handle ')'
		} else { System.out.println("Error in SubroutineBody expected } ."); System.exit(-1);  }
		
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileVarDec() {
		if(t.value().equals("var")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in varDec expected keyword var ."); System.exit(-1);  }
		type();
		identifier();
		while(t.value().equals(",")) {
			t.print(myWriter);
			t.advance();
			identifier();// varName
		} 
		if(t.value().equals(";")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in varDec expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileStatements() {
		try {myWriter.write( "<statements>" + ls);
		while(statementMap.contains(t.value())) {
			if(t.value().equals("let")) {
				myWriter.write( "<letStatement>" + ls);
				CompileLet();
				myWriter.write( "</letStatement>" + ls);
			} 
			if(t.value().equals("if")) {
				myWriter.write( "<ifStatement>" + ls);
				CompileIf();
				myWriter.write( "</ifStatement>" + ls);
			} 
			if(t.value().equals("while")) {
				myWriter.write( "<whileStatement>" + ls);
				CompileWhile();
				myWriter.write( "</whileStatement>" + ls);
			} 
			if(t.value().equals("do")) {
				myWriter.write( "<doStatement>" + ls);
				CompileDo();
				myWriter.write( "</doStatement>" + ls);
			} 
			if(t.value().equals("return")) {
				myWriter.write( "<returnStatement>" + ls);
				CompileReturn();
				myWriter.write( "</returnStatement>" + ls);
			} 
			
		} 
		myWriter.write( "</statements>" + ls);
		}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileLet() {
		if(t.value().equals("let")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileLet expected keyword let ."); System.exit(-1);  }
		identifier();
		//0 or 1 [x]
		if(t.value().equals("[")) {	
			t.print(myWriter);	t.advance();
			expression();
			if(t.value().equals("]")) {t.print(myWriter);	t.advance();}
			else {System.out.println("Error in CompileLet expected ] "); System.exit(-1);}
		}
		if(t.value().equals("=")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileLet expected = "); System.exit(-1);  }
		expression();
		
		if(t.value().equals(";")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileLet expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileIf() {
			if(t.value().equals("if")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileIf expected keyword if ."); System.exit(-1);  }
			if(t.value().equals("(")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
			expression();
			if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			
			if(t.value().equals("{")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
			CompileStatements();
			if(t.value().equals("}")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			
			if(t.value().equals("else")) {	t.print(myWriter);	t.advance();
				if(t.value().equals("{")) {	t.print(myWriter);	t.advance();
				} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
				CompileStatements();
				if(t.value().equals("}")) {	t.print(myWriter);	t.advance();
				} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			} //else is optional
		return;
	}
	
	public void CompileWhile() {
		if(t.value().equals("while")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileWhile expected keyword while ."); System.exit(-1);  }
		if(t.value().equals("(")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileWhile expected ( "); System.exit(-1);  }
		expression();
		if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileWhile expected ) "); System.exit(-1);  }
		
		if(t.value().equals("{")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileWhile expected ( "); System.exit(-1);  }
		CompileStatements();
		if(t.value().equals("}")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileWhile expected ) "); System.exit(-1);  }
			
		return;
	}
	
	public void CompileDo() {
		if(t.value().equals("do")) {	
			t.print(myWriter);	t.advance();
			CompileSubRoutine();
			if(t.value().equals(";")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileDo expected ; "); System.exit(-1);  }
		} else { System.out.println("Error in CompileDo expected keyword do ."); System.exit(-1);  }
		return;
	}
	
	public void CompileReturn() {
		if(t.value().equals("return")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileReturn expected keyword return ."); System.exit(-1);  }
		if(!t.value().equals(";")) {expression();}
		if(t.value().equals(";")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileReturn expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileExpression() {
		//try { //myWriter.write( "<expression>" + ls);
		CompileTerm();
			while(opMap.contains(t.value())) {	
				t.print(myWriter);	t.advance(); // print op term
				CompileTerm();
			} 
			//myWriter.write( "</expression>" + ls);
			//}catch(IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileTerm() {
		try {myWriter.write( "<term>" + ls);
		t.print(myWriter);	                                                            //System.out.println(t.value());
		String TprevValue = t.value();
		String TprevType = t.type();
		t.advance();
		
		if(TprevValue.equals("-")||TprevValue.equals("~")) {	//unary op handling
			//t.print(myWriter);                                                          //System.out.println(t.value());
			CompileTerm();
		} 
		else if(TprevValue.equals("(")) {	//paraenthases expresion handling
			//t.print(myWriter);                                                          //System.out.println(t.value());
			expression();
			if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileTerm  expected ) "); System.exit(-1);  }
		} 
		else if(t.value().equals("[")) {	//array
			t.print(myWriter);	t.advance();
			expression();
			if(t.value().equals("]")) {	t.print(myWriter);	t.advance();
			} else { System.out.println("Error in CompileTerm array expected ] "); System.exit(-1);  }
		} 
		else if(t.value().equals("(") || t.value().equals(".")) {	//subroutine
			t.print(myWriter);	t.advance();
			CompileSubRoutine();
			//if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
			//} else { System.out.println("Error in CompileTerm subroutine expected ) "); System.exit(-1);  }
		} 
		
		
		
			//do nothing if constant or unary op
			myWriter.write( "</term>" + ls);
			}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileExpressionList() {
		try {myWriter.write( "<expressionList>" + ls );
			if(!t.value().equals(")")) {
				
				expression();
				while(t.value().equals(",")) {
					t.print(myWriter);	t.advance(); //print comma
					expression();
				}
			} //0 or more expressions in a list
			myWriter.write( "</expressionList>" + ls);
			}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
	public void CompileSubRoutine() {
		t.print(myWriter);	t.advance();// print subroutineName|className|varName
		if(t.value().equals(".")) {	// if period method is outside this object
			t.print(myWriter);	t.advance(); //print period
			identifier(); //print method name for object
		} //period is optional

		if(t.value().equals("(")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileSubRoutine expected ( "); System.exit(-1);  }
		CompileExpressionList();
		if(t.value().equals(")")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error in CompileSubRoutine expected ) "); System.exit(-1);  }
		return;
	}
	
	public void type() {
		if(t.type().equals("identifier") || t.value().equals("int") || t.value().equals("char") || t.value().equals("boolean") ) {
			t.print(myWriter);	t.advance();
		} else { System.out.println("Error expected type."); System.exit(-1);  }
	}
	public void identifier() {
		if(t.type().equals("identifier")) {	t.print(myWriter);	t.advance();
		} else { System.out.println("Error expected varName."); System.exit(-1);  }
	}
	
	public void expression() {
		try {
			myWriter.write( "<expression>" + ls);
			CompileExpression();
			myWriter.write( "</expression>" + ls);	
			}catch (IOException e) { System.out.println("Error Writing to File."); e.printStackTrace();}
		return;
	}
	
}
