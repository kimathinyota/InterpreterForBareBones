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
		String[] k = {"clear","incr","while","not","do","end","0","decr"};
		for(int i=0;i<8;i++){
			keyWords.put(k[i].hashCode(),k[i]);
		}
	}	
	void addVariable(String identifier, int val){
		Variable newVar = new Variable(identifier,val);
		variables.put(identifier.hashCode(),newVar);
	}
	public String readInputFile(String fileName){
		String fileSource;
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			fileSource = sb.toString();
			br.close();
		}catch (Exception e){
			System.err.format("Exception occurred trying to read '%s'.", fileName);
			e.printStackTrace();
			return null;
		}
		return fileSource;
	}
	int findCurrentWhilePosInArray(int currentLineNum,int[][]arr, int length){
		for(int i=0;i<length;i++){
			if(arr[i][0]==currentLineNum){
				return i;
			}
		}
		return (-1);
	}
	public int returnEndLineForWhile(int whileLine,String[] source){
		int whileLines[][]= new int[10][2];
		int len = 0;
		int i = whileLine+1;
		int newWhile;
		int temp;
		Boolean isFinish = false;
		while(!isFinish){
			newWhile = whileLine;
			while(!source[i].contains("end")){
				if(source[i].contains("while")){
					temp = findCurrentWhilePosInArray(i,whileLines,len);
					if(temp!=-1){
						i = whileLines[temp][1];
					}else{
						newWhile = i;
					}
				}
				i+=1;
			}			
			if(newWhile==whileLine){
				return i;
			}else{
				whileLines[len][0] = newWhile;
				whileLines[len][1] = i;
				len+=1;
				i=0;
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
		System.out.print("{ ");
		for(Integer key: keys){
			System.out.print(variables.get(key).identifier + " = " + variables.get(key).val+ ", " );
		}
		System.out.print("}");
		System.out.println("");
	
	}
	public void interpretSourceCode(String fileName){
		String fileSource = readInputFile(fileName);
		String sourceLines[] = fileSource.split(";");
		System.out.println("Interpreting: ");
		for(int i=0;i<sourceLines.length;i++){
			System.out.println("Line " + i + ": " + sourceLines[i]);
		}
		String nextInstruction; //while, clear, incr, decr
		int whileIndicator[][] = new int[10][2]; //lineNumStart,lineNumEnd
		int whileCount = 0;
		String foundVariable;
		int whilePos,redirect;
		redirect = 0;
		for(int i=0;i<sourceLines.length;i++){
			foundVariable = "";
			nextInstruction = "";
			String words[] = sourceLines[i].split(" ");
			System.out.println("");
			System.out.println("CURRENTLY EXECUTING LINE " + i);
			//Individual words in line
			for(int j=0;j<words.length;j++){
				if(!words[j].equals("")){
					if(keyWords.get(words[j].hashCode())==null){
						if(variables.get(words[j].hashCode())==null){
							addVariable(words[j],0); //store found vaiable
						}
					foundVariable = words[j];
					}else if(words[j].equals("clear") | words[j].equals("incr") | words[j].equals("decr") | words[j].equals("end") | words[j].equals("while")){
						nextInstruction = words[j]; //update next instruction
					}
				}
			}
			Variable foundVar = variables.get(foundVariable.hashCode());
			switch(nextInstruction){
					case "clear":
						foundVar.val = 0;
						System.out.println("Line " + i + ": Finished executing clear instruction on line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						break;
					case "decr":
						foundVar.val -=1;
						System.out.println("Line " + i + ": Finished executing decr instruction on line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						break;
					case "incr":
						foundVar.val +=1;
						System.out.println("Line " + i + ": Finished executing incr instruction on line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						break;
					case "while":
						System.out.println("Line " + i + ": Going to execute while at line " + i);
						whilePos = findCurrentWhilePosInArray(i,whileIndicator,whileCount+1);
						if(whilePos==-1){ //first time interpreter sees this while statement so it needs to include it in whileIndicator array
							whileIndicator[whileCount][0] = i;
							whileIndicator[whileCount][1] = returnEndLineForWhile(i,sourceLines);
							whileCount+=1;
						}	
						if(foundVar.val==0){
							redirect = returnEndLineForWhile(i,sourceLines);
							System.out.println("Line " + i + ": While condition hasn't been met for " + foundVariable + ": Need to exit while loop and redirect to line " + redirect);
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
							i =  redirect ;  //branch statement
						}else{
							System.out.println("Line " + i + ": While condition has been met for " + foundVariable );
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
						}
						break;
					case "end":
						System.out.println("Line " + i + ": Finished executing line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						//branch to previous while
						redirect = findPreviousWhileLineNumber(i,whileCount,whileIndicator) -1;
						System.out.println("Line " + i + ": Reached end of while loop - need to redirect to start of while loop at line " + (redirect+1));
						i = redirect;
						break;	
			}		
			redirect = i;
		}
		System.out.println("Last Line " + redirect + ": Finished stepping through program. ");
		System.out.print("Last Line " + redirect + ": FINAL VALUE OF VARIABLES: ");
		printAllVariables();
	
	}	
	public static void main(String[] args){
		InterpretBareBones myInterpreter = new InterpretBareBones();
		Toolbox myTB = new Toolbox();
		System.out.println("Save text file (source code) in the same directory as these files");
		System.out.println("Enter the name of the text file (e.g. for file program.txt enter program) ");
		String fileName = myTB.readStringFromCmd();
		myInterpreter.setKeyWords();
		myInterpreter.interpretSourceCode(fileName+".txt");	
	}
}