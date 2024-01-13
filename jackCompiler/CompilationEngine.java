package jackCompiler;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CompilationEngine {
	private File input;
	private String ls; //lineSeparator
	private Tokenizer t;
	private HashSet<String> statementMap;
	private HashSet<String> opMap;
	private HashSet<String> constantMap;
	private SymbolTable mySymbolTable;
	private VMWriter myVMWriter;
	private int whileCounter, ifCounter;
	private String className;
	private HashMap<String,Integer> myMap;
	
	public CompilationEngine(File f, String fileName){
		statementMap = new HashSet<String>(); statementMap.add("let"); statementMap.add("if"); statementMap.add("while"); statementMap.add("do");statementMap.add("return");
		opMap = new HashSet<String>(); opMap.add("+");opMap.add("-");opMap.add("*");opMap.add("/");opMap.add("&");opMap.add("|");opMap.add("<");opMap.add(">");opMap.add("=");
		constantMap = new HashSet<String>(); constantMap.add("true");constantMap.add("false");constantMap.add("null");constantMap.add("this");
		input = f;
		mySymbolTable = new SymbolTable();
		ls = "\n"; //System.lineSeparator();
		t = new Tokenizer( input , fileName );
		myVMWriter = new VMWriter(f, fileName);
		whileCounter = 0;
		ifCounter = 0;
		myMap = new HashMap<String,Integer>();
		
		int k = 32;
		myMap.put(" ",k); k++; myMap.put("!",k); k++; myMap.put("\"",k); k++; myMap.put("#",k); k++;
		myMap.put("$",k); k++; myMap.put("%",k); k++; myMap.put("&",k); k++; myMap.put("\'",k); k++;
		myMap.put("(",k); k++; myMap.put(")",k); k++; myMap.put("*",k); k++; myMap.put("+",k); k++;
		myMap.put(",",k); k++; myMap.put("-",k); k++; myMap.put(".",k); k++; myMap.put("/",k); k++;
		
		k=58;
		myMap.put(":",k); k++; myMap.put(";",k); k++; myMap.put("<",k); k++; myMap.put("=",k); k++;
		myMap.put(">",k); k++; myMap.put("?",k); k++; myMap.put("@",k); k++; 
		
		//0 thru 10 starting at 48
		for( int i = 0 ; i < 10 ; i++ ) {	myMap.put( "" + i , i + 48 );	}
		for( int i = 0 ; i < 12 ; i++ ) {	myMap.put( "f" + i , i + 141 );	}
		
		k = 65;
		myMap.put("A",k); k++; myMap.put("B",k); k++; myMap.put("C",k); k++; myMap.put("D",k); k++;
		myMap.put("E",k); k++; myMap.put("F",k); k++; myMap.put("G",k); k++; myMap.put("H",k); k++;
		myMap.put("I",k); k++; myMap.put("J",k); k++; myMap.put("K",k); k++; myMap.put("L",k); k++;
		myMap.put("M",k); k++; myMap.put("N",k); k++; myMap.put("O",k); k++; myMap.put("P",k); k++;
		myMap.put("Q",k); k++; myMap.put("R",k); k++; myMap.put("S",k); k++; myMap.put("T",k); k++;
		myMap.put("U",k); k++; myMap.put("V",k); k++; myMap.put("W",k); k++; myMap.put("X",k); k++;
		myMap.put("Y",k); k++; myMap.put("Z",k); 
		
		for( int i = 0 ; i < 26 ; i++ ) {
			String lowerCase = "";
			int a = i + 65;
			int b = i + (65+32);
			for( String s: myMap.keySet() ) {
				if(myMap.get(s) == a ) {
					lowerCase = s;
				}
			}
			lowerCase = lowerCase.toLowerCase();
			myMap.put(lowerCase, b);
		}
		
		k = 91;
		myMap.put("[",k); k++; myMap.put("\\",k); k++; myMap.put("]",k); k++; myMap.put("^",k); k++;
		myMap.put("_",k); k++; myMap.put("",k); k++; myMap.put("",k); k++; myMap.put("",k); k++;
		
		k=128;
		myMap.put("\n",k); k++; myMap.put("\b",k); k++; 
		
		if(fileName.indexOf("\\") > -1 ) {
			// is directory 
			className = fileName.substring(fileName.indexOf("\\")+1, fileName.lastIndexOf("."));
		}
		else { className = fileName.substring( fileName.lastIndexOf(".")); }
	}
	
	public void closeFile() {	myVMWriter.closeFile(); }
	
	//types: identifier, keyword, integerConstant, symbol, StringConstant
	public void CompileClass() {

		t.advance();//step into file
		if(t.type().equals("keyword")) {
			t.advance(); //advance past keyword class
			t.advance();;//advance past class name (className will be the .vm file name)
			if(t.value().equals("{")) {		t.advance();		} else { System.out.println("Error in Class."); System.exit(-1);  }

			while(t.value().equals("static") || t.value().equals("field")) { CompileClassVarDec();	} 
			while(t.value().equals("constructor") || t.value().equals("function") || t.value().equals("method")) {
				CompileSubroutineDec();
			} 
			if(t.value().equals("}")) {	t.advance();			} else { System.out.println("Error in Class."); System.exit(-1);  }			
			//if t.hasMore() //error because only 1 class per file in Jack
		} else { System.out.println("Error. Expected keyword in Class"); System.exit(-1);  }
	}
	

	public void CompileClassVarDec() {
		String name = ""; String type = ""; String kind = "";
			if(t.value().equals("static") || t.value().equals("field")) {	
				kind=t.value(); ;	t.advance();
			} else { System.out.println("Error in classVarDec."); System.exit(-1);  }
			type=t.value(); ;	t.advance(); 	//type();
			name=t.value(); ;	t.advance(); 	//identifier();
			mySymbolTable.define(name,type,kind);
			while(t.value().equals(",") || t.value().equals("field")) {
				t.advance(); //advance past the comma
				name=t.value(); ;	t.advance();
				mySymbolTable.define(name,type,kind);
			} 
			if(t.value().equals(";")) {	t.advance();
			} else { System.out.println("Error in classVarDec expected ;"); System.exit(-1);  }
	}
			


	
	public void CompileSubroutineDec() {
		mySymbolTable.startSubroutine();
		boolean void_ = false;
		String f_type = ""; // constructor || function || method
		String f_name = "";
		int nVars = 0;
		
		if(t.value().equals("constructor") || t.value().equals("function") || t.value().equals("method")) {		
			f_type = t.value();  t.advance();			
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		
		//type + void definition
		if(t.type().equals("identifier") || t.value().equals("int") || t.value().equals("char") || 
			t.value().equals("boolean") || t.value().equals("void") ) {
			if(t.value().equals("void")) { void_ = true; }
			t.advance();
		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		f_name = t.value(); t.advance(); 	//identifier();
		if(f_type.equals("constructor")) { f_name = "new"; }
		
		if(t.value().equals("(")) {		t.advance();		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }
		
		
		while(!t.value().equals(")") ) { // 0 to * parameters
			nVars = nVars + CompileParameterList(f_type);		
		} 
		
		//testing
		nVars = 0;
		
		//if(f_type.equals("method")) { nVars = nVars + 1; }
		
		if(t.value().equals(")")) {		t.advance();		} else { System.out.println("Error in SubroutineDec."); System.exit(-1);  }

		CompileSubroutineBodyVar();
		
		myVMWriter.writeFunction(className + "." + f_name, nVars + mySymbolTable.varCount("local") );
		if(f_type.equals("constructor")) {
			myVMWriter.writePush("constant", mySymbolTable.varCount("field")); //number of field variables determines object size
			myVMWriter.writeCall("Memory.alloc", 1);
			myVMWriter.writePop("pointer", 0);
		}
		if(f_type.equals("method")) {
			myVMWriter.writePush("argument", 0);  		//push THIS pointer
			myVMWriter.writePop("pointer", 0);			
		}
		
		CompileSubroutineBody();

		//after return toss away  return value if void
		//if(t.value().equals("void")) { myVMWriter.writePop("temp", 0); } //assume void methods are only called by do
		//mySymbolTable.print(f_name);
		return;
	}
	
	public int CompileParameterList( String f_type ) {
		int nVars = 0;
		
		String name = ""; String type = ""; String kind = "argument";
		
		// constructor and methods take this as a parameter
		if( f_type.equals( "method" )) {
			mySymbolTable.define( "this", "pointer" ,  kind ); }
		
		if(t.value().equals(")")) {	return nVars; //if no parameters, return and let subroutineDec handle ')'
		} // else compile parameters

		type=t.value(); t.advance(); //type();
		name=t.value(); t.advance(); //identifier(); // varName
		mySymbolTable.define(name,type,kind);		nVars++;
		
		if(t.value().equals(")")) {	return nVars; 	} // (1 parameter)
		
		while(t.value().equals(",")) {
			t.advance(); // advance past the comma
			type=t.value(); t.advance(); //type();
			name=t.value(); t.advance(); //identifier();// varName
			mySymbolTable.define(name,type,kind);			nVars++;
		} 
		return nVars;
	}
	
	public void CompileSubroutineBodyVar() {

		if(t.value().equals("{")) {	t.advance(); 		} else { System.out.println("Error in SubroutineBody expected { ."); System.exit(-1);  }
		
		while(t.value().equals("var")) {	CompileVarDec();	} 
		//mySymbolTable.print();
	}
	
	public void CompileSubroutineBody() {
		CompileStatements();
				
		if(t.value().equals("}")) {	t.advance();	 	} else { System.out.println("Error in SubroutineBody expected } ."); System.exit(-1);  }
		
		return;
	}
	
	public void CompileVarDec() {
		String name = ""; String type = ""; String kind = "";
		if(t.value().equals("var")) {	kind = "local" ;	t.advance();
		} else { System.out.println("Error in varDec expected keyword var ."); System.exit(-1);  }
		type=t.value(); ;	t.advance(); //type();
		name=t.value(); ;	t.advance(); //identifier();
		mySymbolTable.define(name,type,kind);
		
		while(t.value().equals(",")) {
			t.advance(); //advance past the comma
			name=t.value(); ;	t.advance();
			mySymbolTable.define(name,type,kind);
		} 
		if(t.value().equals(";")) {	t.advance();
		} else { System.out.println("Error in varDec expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileStatements() {
		while(statementMap.contains(t.value())) {
			if(t.value().equals("let")) {		CompileLet();		} 
			if(t.value().equals("if")) {		CompileIf();		} 
			if(t.value().equals("while")) {		CompileWhile();		} 
			if(t.value().equals("do")) {		CompileDo();		} 
			if(t.value().equals("return")) {	CompileReturn();	} 
		} 
		return;
	}
	
	public void CompileLet() {
		if(t.value().equals("let")) {		t.advance();		} else { System.out.println("Error in CompileLet expected keyword let ."); System.exit(-1);  }
		String varName = t.value();  t.advance();
		boolean isArray = false;
		//0 or 1 [x]
		if(t.value().equals("[")) {	
			t.advance();
			myVMWriter.writePush(mySymbolTable.kindOf(varName), mySymbolTable.indexOf(varName));
			expression();
			myVMWriter.writeArithmetic( "add" );
			isArray = true;
			if(t.value().equals("]")) {	t.advance();			} else {System.out.println("Error in CompileLet expected ] "); System.exit(-1);}
		}
		if(t.value().equals("=")) {		t.advance();			} else { System.out.println("Error in CompileLet expected = "); System.exit(-1);  }
		expression();
		
		//if( mySymbolTable.typeOf(varName).equals("Array") ) { 
		if( isArray ) { 
			myVMWriter.writePop( "temp" , 0 );
			myVMWriter.writePop( "pointer" , 1 );
			myVMWriter.writePush( "temp" , 0 );
			myVMWriter.writePop( "that" , 0 );
		} 
		else { myVMWriter.writePop(mySymbolTable.kindOf(varName), mySymbolTable.indexOf(varName)); } 
		
		if(t.value().equals(";")) {		t.advance();			} else { System.out.println("Error in CompileLet expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileIf() {
			if(t.value().equals("if")) {	t.advance();			} else { System.out.println("Error in CompileIf expected keyword if ."); System.exit(-1);  }
			if(t.value().equals("(")) {		t.advance();			} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
			
			int ifCounter1 = ifCounter; ifCounter++;
			int ifCounter2 = ifCounter; ifCounter++;
			
			expression();
			myVMWriter.writeArithmetic( "not" ); 
			myVMWriter.writeIf( "if_" + ifCounter1 );  
			
			if(t.value().equals(")")) {		t.advance();			} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			
			if(t.value().equals("{")) {		t.advance();			} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
			CompileStatements();
			myVMWriter.writeGoto( "if_" + ifCounter2 ); 
			
			if(t.value().equals("}")) {		t.advance();			} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			
			myVMWriter.writeLabel( "if_" + ifCounter1 ); 
			if(t.value().equals("else")) {	t.advance();				
			if(t.value().equals("{")) {		t.advance();			} else { System.out.println("Error in CompileIf expected ( "); System.exit(-1);  }
				CompileStatements();
				
				if(t.value().equals("}")) {	t.advance();			} else { System.out.println("Error in CompileIf expected ) "); System.exit(-1);  }
			} //else is optional
			
			myVMWriter.writeLabel( "if_" + ifCounter2 ); 
			
		return;
	}
	
	public void CompileWhile() {
		if(t.value().equals("while")) {		t.advance();		} else { System.out.println("Error in CompileWhile expected keyword while ."); System.exit(-1);  }
		if(t.value().equals("("	   )) {		t.advance();		} else { System.out.println("Error in CompileWhile expected ( "); System.exit(-1);  }
		
		int whileCounter1 = whileCounter; whileCounter++;
		int whileCounter2 = whileCounter; whileCounter++;
		
		myVMWriter.writeLabel( "while_" + whileCounter1 ); 
		expression();
		if(t.value().equals(")")) {			t.advance();		} else { System.out.println("Error in CompileWhile expected ) "); System.exit(-1);  }
		myVMWriter.writeArithmetic( "not" ); 
		myVMWriter.writeIf( "while_" + whileCounter2 );    
		
		if(t.value().equals("{")) {			t.advance();		} else { System.out.println("Error in CompileWhile expected ( "); System.exit(-1);  }
		CompileStatements();
		myVMWriter.writeGoto( "while_" + (whileCounter1) ); 
		if(t.value().equals("}")) {			t.advance();		} else { System.out.println("Error in CompileWhile expected ) "); System.exit(-1);  }
		
		myVMWriter.writeLabel( "while_" + whileCounter2 ); 
		
		return;
	}
	
	public void CompileDo() {
		if(t.value().equals("do")) {	
			t.advance(); //advance past do
			CompileSubRoutine("do");
			myVMWriter.writePop("temp", 0); // throw away returned value
			//myVMWriter.writePush("constant", 0);
			if(t.value().equals(";")) {		t.advance();		} else { System.out.println("Error in CompileDo expected ; "); System.exit(-1);  }
		} else { System.out.println("Error in CompileDo expected keyword do ."); System.exit(-1);  }
		return;
	}
	
	public void CompileReturn() {
		if(t.value().equals("return")) {		
			t.advance();
		} else { System.out.println("Error in CompileReturn expected keyword return ."); System.exit(-1);  }
		if(!t.value().equals(";")) {
			expression();}
		else { myVMWriter.writePush("constant", 0); }
		myVMWriter.writereturn(); 
		if(t.value().equals(";")) {		
			t.advance();
		} else { System.out.println("Error in CompileReturn expected ; "); System.exit(-1);  }
		return;
	}
	
	public void CompileExpression() {
		
		CompileTerm();
			while(opMap.contains(t.value())) {	
				String op = t.value();
				t.advance(); // print op term
				CompileTerm();
				if(op.equals("-")) {myVMWriter.writeArithmetic("sub"); }	
				if(op.equals("+")) {myVMWriter.writeArithmetic("add"); }	
				if(op.equals("=")) {myVMWriter.writeArithmetic("eq"); }	
				if(op.equals(">")) {myVMWriter.writeArithmetic("gt"); }	
				if(op.equals("<")) {myVMWriter.writeArithmetic("lt"); }	
				if(op.equals("&")) {myVMWriter.writeArithmetic("and"); }	
				if(op.equals("|")) {myVMWriter.writeArithmetic("or"); }	
				if(op.equals("~")) {myVMWriter.writeArithmetic("not"); }	
				if(op.equals("*")) { myVMWriter.writeArithmetic("call Math.multiply 2"); }	
				if(op.equals("/")) { myVMWriter.writeArithmetic("call Math.divide 2"); }
			} 
		return;
	}
	
	public void CompileTerm() {
		//myWriter.write( "<term>" + ls);
		String TprevValue = t.value();
		String TprevType = t.type();
		t.advance();
		if(TprevType.equals("integerConstant")) {	
			myVMWriter.writePush("constant", Integer.parseInt(TprevValue) );
		} 
		else if(TprevType.equals("stringConstant")) {
			TprevValue = TprevValue.substring(1,TprevValue.length()-1);
			System.out.println(TprevValue);
			myVMWriter.writePush( "constant" , TprevValue.length()  );
			myVMWriter.writeCall( "String.new" , 1 );
			for( int i = 0 ; i < TprevValue.length() ; i++ ) {
				String myString = String.valueOf( TprevValue.charAt(i));
				System.out.println(myString);
				myVMWriter.writePush( "constant" ,  myMap.get(  myString ) );
				myVMWriter.writeCall( "String.appendChar" , 2 );
			}
		} 
		else if(TprevValue.equals("null") || TprevValue.equals("false") || TprevValue.equals("true") || TprevValue.equals("this")   ) {	
			//unfinished
			if(TprevValue.equals("null") || TprevValue.equals("false") ) { myVMWriter.writePush("constant", 0);}
			if(TprevValue.equals("true")) { myVMWriter.writePush("constant", 1); myVMWriter.writeArithmetic("neg");	}
			if(TprevValue.equals("this")) { myVMWriter.writePush("pointer", 0); }
		} 
		
		else if(TprevValue.equals("-")||TprevValue.equals("~")) {	//unary op handling
			CompileTerm();
			if(TprevValue.equals("-")) {myVMWriter.writeArithmetic("neg");}
			if(TprevValue.equals("~")) {myVMWriter.writeArithmetic("not");}
		} 
		else if(TprevValue.equals("(")) {	//paraenthases expresion handling
			expression();
			if(t.value().equals(")")) {		t.advance();
			} else { System.out.println("Error in CompileTerm  expected ) "); System.exit(-1);  }
		} 
		else if(t.value().equals("(") || t.value().equals(".")) {	//subroutine
			//t.advance(); //advance past (
			CompileSubRoutine( TprevValue );
			// handled in compileSubRoutine -> myVMWriter.writeCall()
		} 
		/////////////////////////////////////////////////////////////////////////////////////////
		else if(t.value().equals("[")) {	//array
			//t.print(myWriter);	
			t.advance(); //advance past {
			myVMWriter.writePush(mySymbolTable.kindOf(TprevValue), mySymbolTable.indexOf(TprevValue));
			
			expression();
			myVMWriter.writeArithmetic( "add" );
			
			myVMWriter.writePop("pointer" , 1 );
			myVMWriter.writePush("that" , 0 );
			//myVMWriter.writePop("temp" , 0 );
			// MISSING CODE
			//access array and push var
			
			if(t.value().equals("]")) {	
				//t.print(myWriter);	
				t.advance();
			} else { System.out.println("Error in CompileTerm array expected ] "); System.exit(-1);  }
			
		} else { //else varName
			//if( mySymbolTable.typeOf(TprevValue).equals("Array") ) {		} 
			myVMWriter.writePush(mySymbolTable.kindOf(TprevValue), mySymbolTable.indexOf(TprevValue));
		}
		//////////////////////////////////////////////////////////////////////////////////////////

			//myWriter.write( "</term>" + ls);
		return;
	}
	
	public int CompileExpressionList() {
		//myWriter.write( "<expressionList>" + ls );
		int nArgs = 0;
			if(!t.value().equals(")")) {
				nArgs = 1;
				expression();
				while(t.value().equals(",")) {
					t.advance(); //print comma
					expression();
					nArgs++;
				}
			} //0 or more expressions in a list
			//myWriter.write( "</expressionList>" + ls);
		return nArgs;
	}
	
	public void CompileSubRoutine( String name ) {   
		String dotname = ""; // name.dotname(nArgs)
		String f_type = ""; //
		boolean doBool = false;
		if(name.equals("do")) {
			name = t.value(); 
			t.advance();
			doBool = true; 
			}
		// print subroutineName|className|varName
		if(t.value().equals(".")) {	// if period method is outside this object
			t.advance(); //print period
			dotname =  t.value();
			if(dotname.equals("new")) { f_type = "constructor"; }
			else if ( doBool && mySymbolTable.isSystemFunction(name) ) {f_type = "function"; } // OS methods
			else if ( mySymbolTable.kindOf(name).equals("NONE") ) {	name = name;
				f_type = "constructor";
			}
			else {						f_type = "method"; }
			t.advance();
		} else 	{ 						f_type = "inmethod"; }	//in same class method call
		if( mySymbolTable.isSystemFunction(name) ) {f_type = "function"; }
		if( className.equals(name) ) {f_type = "function"; }
		
		if(t.value().equals("(")) {	t.advance();		} else { System.out.println("Error in CompileSubRoutine expected ( "); System.exit(-1);  }
		
		if( f_type.equals("inmethod")) {	myVMWriter.writePush("pointer", 0 );	}
		if( f_type.equals("method")) {	myVMWriter.writePush(mySymbolTable.kindOf(name), mySymbolTable.indexOf(name));	}
		
		int nArgs = CompileExpressionList(); //pushes all arguments onto the stack
		
		if( f_type.equals("constructor")) {
			myVMWriter.writeCall(name+"."+dotname, nArgs);
		}
		
		else if( f_type.equals("method") ) {
			String object_type = mySymbolTable.typeOf(name);
			myVMWriter.writeCall(object_type+"."+dotname, nArgs+1);
		}
		
		else if (f_type.equals("inmethod")) {
			myVMWriter.writeCall(className+"."+name, nArgs+1);
		} else { //function
			//if( doBool ) {
				String object_type = mySymbolTable.typeOf(name);
				if( name.equals(className) ) { object_type=className; }
				myVMWriter.writeCall(object_type+"."+dotname, nArgs);
			//}
			//else myVMWriter.writeCall(name, nArgs);
		}
		
		if(t.value().equals(")")) {	t.advance();
		} else { System.out.println("Error in CompileSubRoutine expected ) "); System.exit(-1);  }
		//myVMWriter.writeCall(name, nArgs);
		return;
	}
	
	public void expression() {
			//myWriter.write( "<expression>" + ls);

			CompileExpression();
/*
				if( myET.type().equals("symbol")) {	
						
				}
				else if( myET.type().equals("identifier") ) {
					//if in symbol table
						// pushVariable
					//else writeCall
				}
				else if( myET.type().equals("stringConstant") ) {
					//do something
				}
				else if( myET.type().equals("integerConstant") ) {
					//do something
				}
*/

			
			//myWriter.write( "</expression>" + ls);	
		return;
	}
	
}
