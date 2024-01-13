package jackCompiler;
import java.util.ArrayList;


public class TestClass {

	public static void main(String[] args) {
		ArrayList<Integer> myList = new ArrayList<Integer>();
		myList.add(2);
		myList.add(4);
		myList.add(6);
		myList.add(8);
		myList.add(10);

		while(!myList.isEmpty()) {
			System.out.println("Left Most Element of List is: " + myList.get(0)); 
			myList.remove(0);
		}
		
	}

}
