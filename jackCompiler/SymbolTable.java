package jackCompiler;

import java.util.HashMap;
import java.util.HashSet;

public class SymbolTable {

	// varName // type // kind // number
	
	private HashMap<String,values> classMap;
	private HashMap<String,values> subMap;
	private HashSet<String> systemSet;
	
	SymbolTable(){
		classMap = new HashMap<String,values>();
		subMap = new HashMap<String,values>();
		systemSet = new HashSet<String>();
		systemSet.add("Math");
		systemSet.add("Screen");
		systemSet.add("Output");
		systemSet.add("Keyboard");
		systemSet.add("Array");
		systemSet.add("Sys");
		systemSet.add("Memory");
		systemSet.add("String");
	}//constructor
	
	
	////////need to consider how objects are handled
	
	
	public void startSubroutine() { 
		subMap.clear(); 
		//values v = new values("Point","argument", 0);
		//subMap.put("this", v);
		//v = new values("Point","argument", 1);
		//subMap.put("this", v);
	}
	
	public void define( String name, String type, String kind ) {
		//does not check for repeated variable names
		values v;
		if(varCount(kind) == 0 ) { v = new values( type, kind, 0 ); }
		else { v = new values( type, kind, varCount(kind) );  }
		if(kind.equals("static") || kind.equals("field") ) {	
			classMap.put(name,v);	}
		else {subMap.put(name,v);	}
	}
	
	public boolean isSystemFunction( String f ) {
		return systemSet.contains(f);
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
		if(systemSet.contains(n)) {return n;}
		if(classMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getKind();
		}
		if(subMap.containsKey(n)) { 
			v =  subMap.get(n);
			s = v.getKind();
		}
		if(s.equals("field")) { s="this";}
		return s;
	} //kindOf
	
	public String typeOf( String n ) { //name
		String s = "NONE";
		values v = null;
		if(systemSet.contains(n)) {return n;}
		if(classMap.containsKey(n)) { 
			v =  classMap.get(n);
			s = v.getType();
		}
		if(subMap.containsKey(n)) { 
			v =  subMap.get(n);
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
			v =  subMap.get(n);
			i = v.getIndex();
		}
		return i;
	} //indexOf
	
	public void print( String f_name ) {
		System.out.println();
		System.out.println("ClassMap for " + f_name + ": ");
		System.out.println("name      type       kind        # ");
		for(String s : classMap.keySet() ) {
			values v = classMap.get(s);
			System.out.println( s +"   " +  v.getType()   +"   " +  v.getKind()  +"   " +  v.getIndex()  );
		}
		
		System.out.println();
		System.out.println("SubMap for " + f_name + ": ");
		System.out.println();
		
		for(String s : subMap.keySet() ) {
			values v = subMap.get(s);
			System.out.println( s +"   " +  v.getType()   +"   " +  v.getKind()  +"   " +  v.getIndex()  );
		}
	}
	
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
