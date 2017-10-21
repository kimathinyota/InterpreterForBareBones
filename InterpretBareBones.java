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
		String trimArr = new String();
		for(int i=start;i<end+1;i++){
			trimArr+=source[i];
		}
		return trimArr;
	}
			
	public int routineCall(ArrayList<Integer>arguments, String source[]){
		InterpretBareBones interpretRoutine = new InterpretBareBones();
		for(int i=0;i<parameters.size();i++){
			if(i<arguments.size()){
				parameters.get(i).val = arguments.get(i);
                        }else{
				parameters.get(i).val = 0;
			}
			interpretRoutine.addVariable(parameters.get(i));
		}
		return interpretRoutine.interpretSourceCode(trimSourceCode(source,startLineNum,endLineNum),startLineNum+1);
                
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
		String declareSub[] = line.split(" ");
		String id = declareSub[1];
		id = id.split("(")[0];
                id = id.split(" ")[0];
		return id;
		
	}	
	public Integer[] getRoutineParameters(String line){
		String splitBracket[] = line.split("(");
		String insideBracket = splitBracket[1];
		insideBracket = insideBracket.substring(0,insideBracket.length()-2);
		String param[] = insideBracket.split(",");
                Integer paramValues[] = new Integer[param.length];
                Variable foundVar;
                int count = 0;
                String expr = new String();
                for(int i=0;i<param.length;i++){
                    String currentExpression[] = param[i].split(" ");
                    for(int j=0;j<currentExpression.length;j++){
                        expr = "";
                        String currentToken[] = currentExpression[i].split(" ");
                        for(int k=0;k<currentToken.length;k++){
                            foundVar = variables.get(currentToken[k].hashCode());
                            if(foundVar!=null){
                                    expr+=String.valueOf(foundVar.val);
                            }else{
                                    expr+=currentToken[j]+" ";
                            }
                        }         
                        paramValues[count] = returnCalculationValue(expr);
                        count+=1;        
                    }
                }
		return paramValues;
	}	
	public String[] getDeclaredRoutineParameters(String line){
		String splitBracket[] = line.split("(");
		String insideBracket = splitBracket[1];
		insideBracket = insideBracket.substring(0,insideBracket.length()-2);
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
		String param[] = getDeclaredRoutineParameters(source[start]);
		ArrayList<Variable>parameters = new ArrayList<Variable>();
		for(int j=0;j<param.length;j++){
			Variable temp = new Variable(param[j].split(" ")[0],0);
			parameters.add(temp);
		}
		Subroutine newSub = new Subroutine(id,start,end,parameters);
		routines.put(id.hashCode(),newSub);
	}	
	public void addSubroutines(String source[]){
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
		interpretSourceCode(fileSource,0);
	}
	int returnOperatorPrecedence(String operator){
		String operators[] = {"*","/","+","-"};
		int precedence[] = {4,4,2,2};
		for(int i=0;i<operators.length;i++){
			if(operator==operators[i]){
				return precedence[i];
			}
		}
		return -1;
	}
	Boolean isOperand(String operator){
		if(returnOperatorPrecedence(operator)==-1 && operator!=" "){
			return true;
		}
		return false;
	}
	Boolean isParanthesis(String operator){
		if(operator=="(" | operator==")"){
			return true;
		}
		return false;
	}	
	String convertFromInfixToPostfix(String line){
		String postfix = new String();
		Stack<String>temp = new Stack<String>();
		String infix[] = line.split(" ");
		for(int i =0;i<infix.length;i++){
			if(isOperand(infix[i])){
				postfix+=(infix[i]+" ");
			}else if(isParanthesis(infix[i])==false){
				if(temp.empty() | (returnOperatorPrecedence(temp.peek())<returnOperatorPrecedence(infix[i]))  ){
					temp.push(infix[i]);
				}else{
					while(!temp.empty() && temp.peek()!="(" ){
						postfix+=(temp.pop()+" ");
					}
					temp.push(infix[i]);
					
				}
			}else if(infix[i]=="("){
				temp.push(infix[i]);		
			}else if(infix[i]==")"){
				while(!temp.empty() && temp.peek()!="("){
					postfix+=(temp.pop()+" ");
				}
				temp.pop();
			}
		}
		return postfix;	
	}	
	int calculatePostfixExpression(String line){
		Stack<Integer>temp = new Stack<Integer>();
		String expr[] = line.split(" ");
		int lastOp,secLastOp;
		Integer result;
		for(int i=0;i<expr.length;i++){
			if(isOperand(expr[i])){
				temp.push(Integer.parseInt(expr[i]));
			}else{
				lastOp = temp.pop();
				secLastOp = temp.pop();
				switch(expr[i]){
					case "*":
						result = lastOp*secLastOp;
						temp.push(result);
						break;
					case "+":
						result = lastOp+secLastOp;
						temp.push(result);
						break;
					case "-":
						result = lastOp-secLastOp;
						temp.push(result);
						break;
					case "/":
						result = lastOp/secLastOp;
						temp.push(result);
						break;
						
				}
                        }
		}
		return temp.pop();
	
	}
        public int returnCalculationValue(String line){
		return calculatePostfixExpression(convertFromInfixToPostfix(line));	
	}
	public String returnFormattedRightOfString(String input, String operator, String[] source){ //returns the right side of the input from the operator and replaces any function call or variable with the value
            //Check for function call: 
            String var = input.split(operator)[1];
            String rightExpr = new String();
            String rightOfEqual[] = var.split(" ");
            Variable foundVar;
            String functionCall;
            Subroutine foundSub;
            for(int l=0;l<rightOfEqual.length;l++){
                    var = rightOfEqual[l];
                    functionCall = getRoutineID(var);
                    foundSub = routines.get(functionCall.hashCode());
                    foundVar = variables.get(var.hashCode());
                    if(foundVar!=null){
                            rightExpr+=String.valueOf(foundVar.val);
                    }else if(foundSub!=null){
                            rightExpr+=var;
                    }else{
                         ArrayList<Integer>param = new ArrayList<Integer>(Arrays.asList(getRoutineParameters(var)));
                         rightExpr += foundSub.routineCall(param, source);
                    }
            } //code above will replace any variables with their actual number equivalent
            return rightExpr;
        }
	public int interpretSourceCode(String fileSource, int startLine){
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
						 | words[j].equals("end") | words[j].equals("while") | words[j].equals("sub") | words[j].equals("=") | words[j].equals("return")){
						nextInstruction = words[j]; //update next instruction
						
					}else if(words[j].charAt(0)=='/' && words[j].charAt(1)=='/'){
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
						String leftVariable = sourceLines[i].split("=")[0];
						leftVariable = leftVariable.split(" ")[0];
						foundVar = variables.get(leftVariable.hashCode());
						foundVar.val = returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"=",sourceLines));
						break;
                                        case "return":
                                            return returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"return",sourceLines));    
			}		
			redirect = i;
		}
		System.out.println("Last Line " + redirect + ": Finished stepping through program. ");
		System.out.print("Last Line " + redirect + ": FINAL VALUE OF VARIABLES: ");
		printAllVariables();
                return 0;
	
	}		
        public static void main(String[] args){
		InterpretBareBones myInterpreter = new InterpretBareBones();
		Toolbox myTB = new Toolbox();
		System.out.println("Save text file (source code) in the same directory as these files");
		System.out.println("Enter the name of the text file (e.g. for file program.txt enter program) ");
		String fileName = myTB.readStringFromCmd();
		myInterpreter.interpretFile(fileName+".txt");	
	}
}

	public static void main(String[] args){
		InterpretBareBones myInterpreter = new InterpretBareBones();
		Toolbox myTB = new Toolbox();
		System.out.println("Save text file (source code) in the same directory as these files");
		System.out.println("Enter the name of the text file (e.g. for file program.txt enter program) ");
		String fileName = myTB.readStringFromCmd();
		myInterpreter.interpretFile(fileName+".txt");	
	}
}
