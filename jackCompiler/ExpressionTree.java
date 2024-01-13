package jackCompiler;

import java.util.ArrayList;

public class ExpressionTree {
	
	private String value, type;
	private ExpressionTree root;
	private ArrayList<ExpressionTree> leaves;
	
	public ExpressionTree( ) {
		value = null;
		type = null;
		root= null;
		leaves = new ArrayList<ExpressionTree>();
	}
	
	public ExpressionTree( String e , String t ) {
		value = e;
		root= null;
		type = t;
		leaves = new ArrayList<ExpressionTree>();
	}
	
	public void addRoot( String e , String t ) {	root = new ExpressionTree( e , t );	}
	
	public ExpressionTree moveUp() { 
		if(root == null) { return this;}
		return root; 
		} 		// moves up - tree has only 1 root

	public ExpressionTree moveDown( String d ) { 			// "right" or "left"  (default is right)
		if(d.equals("right")) { leaves.get(leaves.size()); }
		return leaves.get(0); //left
	}


	public ExpressionTree eatLeaf() { //eats farthest LEFT element if it has no leaves
		if(!hasLeaves() ) {	System.out.println("No Leaves Left to eat !!! "); }
		leaves.remove(0);
		if(!hasLeaves() ) {	return root; }
		return this;
	}
	
	

	
	public boolean hasLeaves() { return !leaves.isEmpty(); }
	public int numberLeaves() { return leaves.size(); }
	public boolean hasRoot() { 
		if(root == null) { return false;}
		return true;
		}
	
	public void setValue( String e ) { value = e; }
	public String value() { return value; }
	public String type() { return type; }
		
	public void addLeaf( String e , String t ) { 
		//adds leaf to the right this expression tree and returns a pointer to it
		if( value == null ) { value = e; return; } // if tree is empty, the first leaf become the first value
		
		ExpressionTree ET = new ExpressionTree(e , t);
		leaves.add(ET);
	}
	
}
