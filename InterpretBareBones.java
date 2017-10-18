import java.util.*;
import java.io.*;
class Variable{ //this will store a variable in the program e.g. for variable x = 3 : identifier = X and val = 3 
	public String identifier;
	public int val;
	public Variable(String identifier, int val){
		this.identifier = identifier;
		this.val = val;
	}
}
class Subroutine{
	public String identifier;
	public int startLineNum,endLineNum;
	ArrayList<Variable> parameters = new ArrayList<Variable>();
	public String trimSourceCode(String source[],int start, int end){
		String trimArr;
		for(int i=start;i<end+1;i+++){
			trimArr+=source[i];
		}
		return trimArr;
	}
			
	public int routineCall(ArrayList<int>arguments, String source[]){
		InterpretBareBones interpretRoutine = new InterpretBareBones();
		for(int i=0;i<parameters.size();i++){
			if(i<arguments.size()){
				parameters[i].val = arguments[i];
			else{
				parameters[i].val = 0;
			}
			interpretRoutine.addVariable(parameters[i]);
		}
		interpretRoutine.interpretSourceCode(trimSourceCode(source,startLineNum,endLineNum),startLineNum+1);
	}
	
	public Subroutine(String id, int start, int end, ArrayList<Variable>param){
		identifier = id;
		startLineNum = start;
		endLineNum = end;
		parameters = param;
	}
	
	
	
}	
	
	
	
	
public class InterpretBareBones{
	//Hash tables have an average look up of O(1) so searching for an element in a hash table is much quicker than using an array
	Hashtable<Integer, Variable> variables = new Hashtable<Integer, Variable>(); /*as the number of variables increases, the look up
	time for each variable stays constant */
	Hashtable<Integer, String> keyWords = new Hashtable<Integer, String>(); //don't have to write a for loop to search array
	Hashtable<Integer, Subroutine> routines = new Hashtable<Integer, Subroutine>();
	
	public String getRoutineID(String line){
		String declareSub[] = source[start].split(" ");
		String id = declareSub[1];
		id = id.split("(")[0];
		return id;
		
	}
	
	public String[] getRoutineParameters(String line){
		String splitBracket[] = line.split("(");
		String insideBracket = splitBracket[1];
		insideBracket = insideBracket.substring(0,insideBracket.length-2);
		String param[] = insideBracket.split(",");
		return param;
	}
	
	public void addRoutine(String source[],int start){
		int i = start;
		while(!source[i+1].equals("endsub")){
			i+=1;
		}
		int end = i+1;
		//find identifier
		String id = getRoutineID(source[start]);
		//find parameters
		String param[] = getRoutineParameters(source[start]);
		ArrayList<Variable>parameters = new ArrayList<Variable>();
		for(int j=0;j<param.length;j++){
			Variable temp = new Variable(param[j].split(" ")[0],0);
			parameters.add(temp);
		}
		Subroutine newSub = new Subroutine(id,start,end,parameters);
		routines.put(id.hashCode(),newSub);
	}	
	public addSubroutines(String source[]){
		String routine;
		int start;
		for(int i=0;i<source.length;i++){
			if(source[i].contains("sub")){
				addRoutine(source,i);
			}
		}	
	}
		
		
		
	
	
	public void setKeyWords(){
		String[] k = {"clear","incr","while","not","do","end","0","decr"};
		for(int i=0;i<8;i++){
			keyWords.put(k[i].hashCode(),k[i]); //using hashCode method to determine unique key
		}
	}	
	void addVariable(String identifier, int val){ //adds variable to variables hash table
		Variable newVar = new Variable(identifier,val);
		variables.put(identifier.hashCode(),newVar);
	}
	void addVariable(Variable newVar){ //adds variable to variables hash table
		variables.put(newVar.identifier.hashCode(),newVar);
	}
	public String readInputFile(String fileName){ // returns contents of file if file exists or null if file doesn't exist
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
	public int returnEndLineForWhile(int whileLine,String[] source){  /*method will return the line number of the accompanying end
		to the input while (at line number whileLine) */
		int whileLines[][]= new int[10][2]; /*declares an array of {p,q}, where {p,q} is an array, where p refers to line number of while, 
		and q refers to line number of accompanying end */
		int len = 0;
		int i = whileLine+1;
		int newWhile;
		int temp;
		Boolean isFinish = false;
		while(!isFinish){
			newWhile = whileLine;
			//below while loop will set newWhile = p, and i = q, for a unique {p,q} (while, end line number pair)
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
			if(newWhile==whileLine){ /*this indicates that the unique {p,q} , where p = input while line number, has been found
				so the line number of the accompanying end to the input while has been found */
				return i;
			}else{
				//unique {p,q} found by above loop will be stored in whileLines 2D array
				whileLines[len][0] = newWhile;
				whileLines[len][1] = i;
				len+=1;
				i=0;
			}
		}
		return (-1);
	}
	int findPreviousWhileLineNumber(int endLineNum,int length,int[][]arr){ //find line number of accompanying while statement of input end statement
		for(int i=0;i<length;i++){
			if(arr[i][1]==endLineNum){
				return arr[i][0];
			}
		}
		return (-1);
	}
	public void printAllVariables(){ //print current values of all variables
		Set<Integer> keys = variables.keySet();
		System.out.print("{ ");
		for(Integer key: keys){
			System.out.print(variables.get(key).identifier + " = " + variables.get(key).val+ ", " );
		}
		System.out.print("}");
		System.out.println("");
	
	}
		
		
	public void interpretFile(String fileName){
		String fileSource = readInputFile(fileName);
		String sourceLines[] = fileSource.split(";");
		System.out.println("Interpreting: ");
		//Loop below will list source code that will be interpreted
		for(int i=0;i<sourceLines.length;i++){
			System.out.println("Line " + i + ": " + sourceLines[i]);
		}
		interpretSourceCode(fileSource);
	}
	int returnOperatorPrecedence(char operator){
		Char operators[] = {'(',')','*','/','+','-'}
		int precedence[] = {6,6,4,4,2,2};
		for(i=0;i<operators.length;i++){
			if(operator==operators[i]){
				return precedence[i];
			}
		}
		return -1;
	}
	bool isOperand(char operator){
		if(returnOperatorPrecedence(operator)==-1 && operator!=''){
			return true;
		}
		return false;
	}
	
	string convertFromInfixToPostfix(string line){
		String postfix,infix;
		Stack<Char>temp = new Stack<Char>();
		for(int i=0;i<line.length;i++){
			if(line[i]==''){
				infix+=line[i];
			}
		}
		
	}
	
	int calculatePostfixExpression(string line){
	
	}
	public int returnCalculationValue(string line){
		return calculatePostfixExpression(convertFromInfixToPostfix(line));	
	}
		
	public void interpretSourceCode(String fileSource, int startLine=0){
		setKeyWords();
		String sourceLines[] = fileSource.split(";"); //split source code into an array consisting of individual lines
		addSubroutines(sourceLines);
		String nextInstruction; //stores next instruction e.g. while, clear, incr, decr
		int whileIndicator[][] = new int[10][2]; /*declares an array of {p,q}, where {p,q} is an array, where p refers to line number of while, 
		and q refers to line number of accompanying end */
		int whileCount = 0;
		String foundVariable; 
		int whilePos,redirect;
		redirect = 0;
		String currentSub;
		for(int i=startLine;i<sourceLines.length;i++){ //iterate through array of individual lines of source code
			foundVariable = "";
			nextInstruction = "";
			String words[] = sourceLines[i].split(" "); //splits current line into an array of individual words
			System.out.println("");
			System.out.println("CURRENTLY EXECUTING LINE " + i);
			//Individual words in line
			for(int j=0;j<words.length;j++){ //iterate through array of individual words within current line
				if(!words[j].equals("")){ // <==> current word needs to not be empty
					if(keyWords.get(words[j].hashCode())==null){ //indicates that current word isn't a keyword so must be a variable
						if(variables.get(words[j].hashCode())==null){ //indicates that current variable hasn't been stored yet
							addVariable(words[j],0); //store found vaiable
						}
						foundVariable = words[j]; 
					}else if(words[j].equals("clear") | words[j].equals("incr") | words[j].equals("decr") 
						 | words[j].equals("end") | words[j].equals("while") | words[j].equals("sub") | words[j].equals("=")){
						nextInstruction = words[j]; //update next instruction
						
					}else if(words[j].contains("//"){
						break;
					}
					

						
						
					
				}
			}
			Variable foundVar = variables.get(foundVariable.hashCode()); //foundVar references variable found in the current line
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
						//CHECK WHILE CONDITIONS
						if(foundVar.val==0){ //assumes while condition hasn't been met for found variable
							redirect = returnEndLineForWhile(i,sourceLines);
							System.out.println("Line " + i + ": While condition hasn't been met for " + foundVariable + ": Need to exit while loop and redirect to line " + redirect);
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
							i =  redirect ;  //interpreter will branch to the next line out of the while loop
						}else{ //assumes while condition has been met for found variable
							System.out.println("Line " + i + ": While condition has been met for " + foundVariable );
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
						}
						break;
					case "end":
						System.out.println("Line " + i + ": Finished executing line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						redirect = findPreviousWhileLineNumber(i,whileCount,whileIndicator) -1; // interpreter will branch to previous while
						System.out.println("Line " + i + ": Reached end of while loop - need to redirect to start of while loop at line " + (redirect+1));
						i = redirect;
						break;
					case "sub":
						currentSub = getRoutineID(sourceLines[i]);
						redirect = routines.get(currentSub.hashCode()).endLineNum;
						i = redirect;
						break;
					case "=":
						//find variable to left of =
						String var = sourceLines[i].split("=")[0];
						var = var.split(" ")[0];
						foundVar = variables.get(var.hashCode());
						foundVar.val = returnCalculationValue(sourceLines[i].split("=")[1]);
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
		myInterpreter.interpretSourceCode(fileName+".txt");	
	}
}
