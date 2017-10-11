import java.util.*;

import java.io.*;
class Variable{
	public String identifier;
	public int val;
	public Variable(String identifier, int val){
		this.identifier = identifier;
		this.val = val;
	}
}

public class InterpretBareBones{
	Hashtable<Integer, Variable> variables = new Hashtable<Integer, Variable>();
	Hashtable<Integer, String> keyWords = new Hashtable<Integer,String>();
	public void setKeyWords(){
		String[] k = {"clear","incr","while","not","do","end","0"};
		for(int i=0;i<7;i++){
			keyWords.put(k[i].hashCode(),k[i]);
		}
	}	
	void addVariable(String identifier, int val){
		Variable newVar = new Variable(identifier,val);
		variables.put(identifier.hashCode(),newVar);
	}
	public String readInputFile(String fileName){
		String fileSource ;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try{
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			fileSource = sb.toString();
		}finally{
			br.close();
		}
		
		return fileSource;
	}
	int returnNextEndLinePositionForWhile(int whileLineNum,String[] source){
		int x = whileLineNum;
		int c = 0;
		while(source[x+1].contains("while")){
			x+=1;
		}
		c = x - whileLineNum;
		x+=1;
		do{
			if(source[x].contains("end")){
				c-=1;
			}
			x+=1;
		}while(c>0);
		
		return (x+1);
		
	}
	int findCurrentWhilePosInArray(int currentLineNum,int[][]arr, int length){
		for(int i=0;i<length;i++){
			if(arr[i][0]==currentLineNum){
				return i;
			}
		}
		return (-1);
	}
	int findPreviousWhileLineNumber(int endLineNum,int length,int[][]arr){
		for(int i=0;i<length;i++){
			if(arr[i][1]==endLineNum){
				return arr[i][0];
			}
		}
		return (-1);
	}
	public void printAllVariables(){
		Set<Integer> keys = variables.keySet();
		//Obtaining iterator over set entries
		Iterator<Integer> itr = keys.iterator();
		//Displaying Key and value pairs
		System.out.print("{ ");
		while (itr.hasNext()){ 
		   System.out.print(variables.get(itr.next()).identifier + " = " + variables.get(itr.next()).val+ ", " );
		} 
		System.out.print("}");
		
	}
	public void interpretSourceCode(String fileName){
		String fileSource = readInputFile(fileName);
		String sourceLines[] = fileSource.split(";");
		String nextInstruction; //while, clear, incr, decr
		//whileIndicator
		int whileIndicator[][] = new int[10][2]; //lineNumStart,lineNumEnd
		
		int whileCount = 0;
		String foundVariable;
		int whilePos;
		
		
		for(int i=0;i<sourceLines.length;i++){
			foundVariable = "";
			nextInstruction = "";
			String words[] = sourceLines[i].split(" ");
			
			//Individual words in line
			for(int j=0;j<words.length;j++){
				if(!keyWords.containsValue(words[j])){
					if(!variables.containsValue(words[j])){
						addVariable(words[j],0); //store found vaiable
					}
					foundVariable = words[j];
				}else if(words[j]=="clear" | words[j]=="incr" | words[j]=="decr" | words[j]=="ends" | words[j]=="while"){
					nextInstruction = words[j]; //update next instruction
				}
			}
			Variable foundVar = variables.get(foundVariable.hashCode());
			switch(nextInstruction){
					case "clear":
						foundVar.val = 0;
						break;
					case "decr":
						foundVar.val -=1;
						break;
					case "incr":
						foundVar.val +=1;
						break;
					case "while":
					
						whilePos = findCurrentWhilePosInArray(i,whileIndicator,whileCount+1);
						if(whilePos==-1){ //first time interpreter sees this while statement so it needs to include it in whileIndicator array
							//lineNumStart,lineNumEnd
							
							whileIndicator[whileCount][0] = i;
							whileIndicator[whileCount][1] = returnNextEndLinePositionForWhile(i,sourceLines);
							whileCount+=1;
						}
						
						if(foundVar.val==0){
							i =  returnNextEndLinePositionForWhile(i,sourceLines) + 1;  //branch statement
						}						
						break;
					case "ends":
						//branch to previous while
						i = findPreviousWhileLineNumber(i,whileCount,whileIndicator);
						break;
			}
			System.out.println("Finished executing line " + i );
			System.out.println("Current values of variables: ");
			printAllVariables();
			
			
			
			
			
		}
	
	}	
	public static void main(String[] args){
		InterpretBareBones myInterpreter = new InterpretBareBones();
		Toolbox myTB = new Toolbox();
		System.out.println("Save text file (source code) in the same directory as these files");
		System.out.println("Enter the file name of the text file (source code e.g. program.txt) ");
		String fileName = myTB.readStringFromCmd();
		myInterpreter.setKeyWords();
		myInterpreter.interpretSourceCode(fileName);
	}
		
		
	
	

}