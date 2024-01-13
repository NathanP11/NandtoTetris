package jackCompiler;
import java.io.File;


public class JackCompiler {
	
	public JackCompiler() {
		
		
	}
		
	public static void main(String[] args) {
		System.out.println("Reading File: " + args[0]);
		File dir = new File(args[0]);

		if (dir.isDirectory()) {
            //System.out.println("Directory: " + dir.getAbsolutePath());
         	
            for( File f : dir.listFiles()) {
            	String FileExt = f.getName();
            	FileExt = FileExt.substring(FileExt.lastIndexOf("."), FileExt.length());
            	if(FileExt.equals(".jack") ) {
            		System.out.println("Processing Jack File in Directory: " + f.getAbsolutePath());
            		//String fName = args[0].substring(0,FileExt.lastIndexOf("."));
            		CompilationEngine myEngine = new CompilationEngine(f, dir + File.separator + f.getName() );
            		myEngine.CompileClass();
            		myEngine.closeFile();
            	}
            }
        } else {
            //System.out.println("Processing Single File: " + dir.getAbsolutePath());
    		CompilationEngine myEngine = new CompilationEngine(dir, args[0]);
    		myEngine.CompileClass();
    		myEngine.closeFile();
        }
	}
}
