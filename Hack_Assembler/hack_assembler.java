import edu.duke.*;
import java.util.*;
import java.lang.*;
import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

/**
 * Write a description of hack_assembler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class hack_assembler {
    ArrayList<String> codeList; //contains instructions and Program Line
    HashMap<String,Integer> SymbolMap; //contains <labels,line> and <variables,memoryLocation>
    int memTracker;
    
    hack_assembler(){ //class initializer
        memTracker = 16;
        codeList = new ArrayList<String>();
        SymbolMap = new HashMap<String,Integer>();
        // preDefined variables
        for(int i = 0; i < 16 ; i++){
            SymbolMap.put("R"+i,i); //R0 .. R15
        }
        SymbolMap.put("SCREEN",16384);
        SymbolMap.put("KBD",24576);
        SymbolMap.put("SP",0);
        SymbolMap.put("LCL",1);
        SymbolMap.put("ARG",2);
        SymbolMap.put("THIS",3);
        SymbolMap.put("THAT",4);
    }
       
    public void tester(){
        System.out.println(SymbolMap);
    }
    
    public boolean isNum(String strNum) {
    boolean ret = true;
        try { Integer.parseInt(strNum);
        }catch (NumberFormatException e) {
            ret = false;}
    return ret;
     }
   
    private void replaceVariables(){
        //only a commands can use variables
        for( int i = 0; i < codeList.size(); i++){
            String line = codeList.get(i);
            if( line.indexOf("@") != -1){ // A command
                String a = line.substring(1,line.length());
                if(!isNum(a)){
                    if(SymbolMap.containsKey(a)){
                        //replace key mem location
                        String newLine = "@" + SymbolMap.get(a);
                        codeList.set(i,newLine);
                    }
                    else{
                        SymbolMap.put(a,memTracker);
                        String newLine = "@" + SymbolMap.get(a);
                        codeList.set(i,newLine);
                        memTracker++;
                    }
                }
            }
            
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    public void main( String Filename){
        String newFileName = Filename.substring(0,Filename.lastIndexOf(".")) + ".hack";
        FileResource fr = new FileResource(Filename);
        //String FileName = fr.getName();
        
        ////////////first pass
        for ( String s : fr.lines()){
            s = removeSpace(s);
            if(s.isEmpty()){}//do nothing
            else if(s.indexOf("(")!=-1){
                if(!SymbolMap.containsKey(s.substring(s.indexOf("(")+1,s.indexOf(")")))){
                    // label refers to next line of instruction
                    // add label to map only if it is a new label
                    String L = s.substring(s.indexOf("(")+1,s.indexOf(")"));
                    SymbolMap.put(L,codeList.size()); 
                }
            }
            else{ //if not empty, and not label, it is an instruction
                codeList.add(s);
            }
        }
        
        //System.out.println("symbol map: " + SymbolMap);
        //System.out.println("codeList: " + codeList);
        replaceVariables();
        //System.out.println("codeList: " + codeList);
        
        try {
              File myObj = new File(newFileName);
              FileWriter myWriter = new FileWriter(myObj);
              
              int PC = 0;
            //////////second Pass -save to file
                for ( String s : codeList ){
                    //System.out.println(PC + ": " + s);
                    s = parseLine(s);
                    //System.out.println(PC + ": Length: " + s.length() + "   " + s);
                    PC+= 1;
                    //fileoutput
                    myWriter.write(s + "\n");
            }
                myWriter.close();
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
              ////////////////////////////////////////////////////////////////////////////////// 
        }
    }
    
    private String parseLine( String s){
        StringBuilder sb = new StringBuilder(s);
        StringBuilder output = new StringBuilder("");
        /*
        if(isPointer){
            add pointer to Hashmap
            return ""; //empty
        }
        if(isLabel){
            add label to Hashmap
            return ""; //empty
        }
         */
        
        if( sb.indexOf("@") != -1){ // A command
            int a = Integer.parseInt(sb.substring(1,sb.length()));
            String b = Integer.toBinaryString(a);
            output.append(b);
            while( output.length() < 16){
                output.insert(0,'0');
            }
            return output.toString();
        }
        
        else{
            int eIndex = sb.indexOf("="); //equals index
            int sIndex = sb.indexOf(";"); //semicolon index
            output.append("111");
            
            if(eIndex > -1){
                String dest = sb.substring(0,eIndex);
                String comp = "";
                if(sIndex > -1){ comp = sb.substring(eIndex+1,sIndex);}
                else{ comp = sb.substring(eIndex+1,sb.length());}
                
                //COMP
                if(comp.equals("0")){output.append("0101010");}
                if(comp.equals("1")){output.append("0111111");}
                if(comp.equals("-1")){output.append("0111010");}
                if(comp.equals("D")){output.append("0001100");}
                if(comp.equals("A")){output.append("0110000");}
                if(comp.equals("!D")){output.append("0001101");}
                if(comp.equals("!A")){output.append("0110001");}
                if(comp.equals("-D")){output.append("0001111");}
                if(comp.equals("-A")){output.append("0110011");}
                if(comp.equals("D+1")){output.append("0011111");}
                if(comp.equals("A+1")){output.append("0110111");}
                if(comp.equals("D-1")){output.append("0001110");}
                if(comp.equals("A-1")){output.append("0110010");}
                if(comp.equals("D+A")){output.append("0000010");}
                if(comp.equals("D-A")){output.append("0010011");}
                if(comp.equals("A-D")){output.append("0000111");}
                if(comp.equals("D&A")){output.append("0000000");}
                if(comp.equals("D|A")){output.append("0010101");}
                if(comp.equals("M")){output.append("1110000");}
                if(comp.equals("!M")){output.append("1110001");}
                if(comp.equals("-M")){output.append("1110011");}
                if(comp.equals("M+1")){output.append("1110111");}
                if(comp.equals("M-1")){output.append("1110010");}
                if(comp.equals("D+M")){output.append("1000010");}
                if(comp.equals("D-M")){output.append("1010011");}
                if(comp.equals("M-D")){output.append("1000111");}
                if(comp.equals("D&M")){output.append("1000000");}
                if(comp.equals("D|M")){output.append("1010101");}
                
                if(output.length() < 4){output.append("0000000");}
                
                //DEST
                output.append("000");
                if(dest.contains("A")){output.setCharAt(10,'1');}
                if(dest.contains("D")){output.setCharAt(11,'1');}
                if(dest.contains("M")){output.setCharAt(12,'1');}
            }
            else{
                //no equals
                String comp = "";
                if(sIndex > -1){ comp = sb.substring(0,sIndex);}
                else{ comp = sb.substring(0,sb.length());}
                
                //COMP
                if(comp.equals("0")){output.append("0101010");}
                if(comp.equals("1")){output.append("0111111");}
                if(comp.equals("-1")){output.append("0111010");}
                if(comp.equals("D")){output.append("0001100");}
                if(comp.equals("A")){output.append("0110000");}
                if(comp.equals("!D")){output.append("0001101");}
                if(comp.equals("!A")){output.append("0110001");}
                if(comp.equals("-D")){output.append("0001111");}
                if(comp.equals("-A")){output.append("0110011");}
                if(comp.equals("D+1")){output.append("0011111");}
                if(comp.equals("A+1")){output.append("0110111");}
                if(comp.equals("D-1")){output.append("0001110");}
                if(comp.equals("A-1")){output.append("0110010");}
                if(comp.equals("D+A")){output.append("0000010");}
                if(comp.equals("D-A")){output.append("0010011");}
                if(comp.equals("A-D")){output.append("0000111");}
                if(comp.equals("D&A")){output.append("0000000");}
                if(comp.equals("D|A")){output.append("0010101");}
                if(comp.equals("M")){output.append("1110000");}
                if(comp.equals("!M")){output.append("1110001");}
                if(comp.equals("-M")){output.append("1110011");}
                if(comp.equals("M+1")){output.append("1110111");}
                if(comp.equals("M-1")){output.append("1110010");}
                if(comp.equals("D+M")){output.append("1000010");}
                if(comp.equals("D-M")){output.append("1010011");}
                if(comp.equals("M-D")){output.append("1000111");}
                if(comp.equals("D&M")){output.append("1000000");}
                if(comp.equals("D|M")){output.append("1010101");}
                
                if(output.length() < 4){output.append("0000000");}
                
                //no equals = null DEST
                output.append("000");
            }
            
            
            if(sIndex > -1){
                String jump = sb.substring(sIndex+1,s.length());
                if(jump.equals("JGT")){output.append("001");}
                else if(jump.equals("JEQ")){output.append("010");}
                else if(jump.equals("JGE")){output.append("011");}
                else if(jump.equals("JLT")){output.append("100");}
                else if(jump.equals("JNE")){output.append("101");}
                else if(jump.equals("JLE")){output.append("110");}
                else if(jump.equals("JMP")){output.append("111");}
                else{output.append("000");}
            }
            if( output.length() != 16 ){
              output.append("000");  
            }
            String abc = output.toString();
            return abc;
        }
        
        //return null; // error if you make it this far
    }
    
    private String removeSpace( String s){
        StringBuilder sb = new StringBuilder();
        if( s.indexOf("//") != -1){ // delete comments
            s = s.substring(0,s.indexOf("//"));
        }     
        for (int k = 0; k < s.length();k++){
            if(!(s.charAt(k) == ' ')){
                sb.append(s.charAt(k));
            }
        }
        
        return sb.toString();
    }
    
}
