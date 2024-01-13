package jackCompiler;

import java.util.HashMap;

public class SymbolTable {

	// varName // type // kind // number
	
	private HashMap<String,values> classMap;
	private HashMap<String,values> subMap;
	
	SymbolTable(){
		classMap = new HashMap<String,values>();
		subMap = new HashMap<String,values>();
	}//constructor
	
	
	public void startSubroutine() { subMap.clear(); }
	
	public void define( String name, String type, String kind ) {
		//does not check for repeated variable names
		values v = new values( type, kind, varCount(kind)+1 );
		if(kind.equals("static") || type.equals("field") ) {	
			classMap.put(name,v);	}
		else {subMap.put(name,v);	}
	}
	
	
	public int varCount( String k ) { //kind
		int count = 0;
		for(values v : classMap.values() ) {
			if(v.getKind().equals(k)) {	count ++;}
		}
		for(values v : subMap.values() ) {
			if(v.getKind().equals(k)) {	count ++;}
		}
		return count;
	} //varCount
	
	public String kindOf( String n ) { //name
		String s = "NONE";
		values v = null;
		if(classMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getKind();
		}
		if(subMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getKind();
		}
		return s;
	} //kindOf
	
	public String typeOf( String n ) { //name
		String s = "NONE";
		values v = null;
		if(classMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getType();
		}
		if(subMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getType();
		}
		return s;
	} //typeOf
	
	public int indexOf( String n ) { //name
		int i = -1;
		values v = null;
		if(classMap.containsKey(n)) { 
			v =  classMap.get(n);
			i = v.getIndex();
		}
		if(subMap.containsKey(n)) { 
			v =  classMap.get(n);
			i = v.getIndex();
		}
		return i;
	} //indexOf
	
} //end class


final class values {
	private String type;
	private String kind;
	private int number;
	
	values( String t , String k , int n ){
		type = t;
		kind = k;
		number = n;
	}//constructor
	
	public String getType() { return type;	}
	public String getKind() { return kind;	}
	public int getIndex() { return number;	}
}
