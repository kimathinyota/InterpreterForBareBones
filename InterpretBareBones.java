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
		String trimArr = new String();
		for(int i=start;i<end+1;i++){
			trimArr+=(source[i]+";");
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
                System.out.println();
		return interpretRoutine.interpretSourceCode(trimSourceCode(source,0,endLineNum),startLineNum+1);           
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
		String declareSub[] = line.trim().split("\\s+");
                String id;
                if(declareSub[0].equals("sub")){
                    id = declareSub[1];
                }else{
                    id = declareSub[0];
                }
                id = id.split("\\(")[0].trim();
		return id;
	}	
	public Integer[] getRoutineParameters(String line){ 
		String splitBracket[] = line.trim().split("\\(");
		String insideBracket = splitBracket[splitBracket.length-1].trim();
		insideBracket = insideBracket.substring(0,insideBracket.length()-1);
		String param[] = insideBracket.split(",");
                Integer paramValues[] = new Integer[param.length];
                Variable foundVar;
                int count = 0;
                for(int i=0;i<param.length;i++){
                    foundVar = variables.get(param[i].hashCode());
                    if(foundVar!=null){
                        paramValues[count] = foundVar.val;
                    }else{
                        paramValues[count] = Integer.valueOf(param[i]);
                    }
                    count+=1; 
                }
		return paramValues;
	}	
	public String[] getDeclaredRoutineParameters(String line){
		String splitBracket[] = line.trim().split("\\(");
		String insideBracket = splitBracket[1].trim();
		insideBracket = insideBracket.substring(0,insideBracket.length()-1);
		String param[] = insideBracket.split(",");
		return param;
	}
        public Boolean isParameter(String word){
            if(word.contains("(") && word.contains(")")){
                return true;
            }
            return false;
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
			Variable temp = new Variable(param[j].trim(),0);
			parameters.add(temp);
		}
		Subroutine newSub = new Subroutine(id,start,end,parameters);
		routines.put(id.hashCode(),newSub);
	}	
	public void addSubroutines(String source[]){
		for(int i=0;i<source.length;i++){
			if(source[i].contains("sub") && !source[i].contains("endsub")){
				addRoutine(source,i);
                                
			}
		}	
	}
	public void setKeyWords(){
		String[] k = {"clear","incr","while","not","do","endwhile","0","decr","sub","endsub","0","=","return","+","-","*","/","(",")","//","if","endif","<",">","==","!="};
		for(int i=0;i<k.length;i++){
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
        int findCurrentXPosInArray(int currentLineNum,int[][]arr){ 
		for(int i=0;i<arr.length;i++){
			if(arr[i][0]==currentLineNum){
				return i;
			}
		}
		return (-1);
	}
        public int returnEndLineForX(int xLine,String[] source,String xOperator){  /*method will return the line number of the accompanying end
		to the input while (at line number whileLine) */
		int xLines[][]= new int[10][2]; /*declares an array of {p,q}, where {p,q} is an array, where p refers to line number of while, 
		and q refers to line number of accompanying end */
		int len = 0;
		int i = xLine+1;
		int newX;
		int temp;
		Boolean isFinish = false;
		while(!isFinish){
			newX = xLine;
			//below while loop will set newWhile = p, and i = q, for a unique {p,q} (while, end line number pair)
			while(!source[i].contains("end"+xOperator)){
				if(source[i].contains(xOperator)){
					temp = findCurrentXPosInArray(i,xLines);
					if(temp!=-1){
						i = xLines[temp][1];
					}else{
						newX = i;
					}
				}
				i+=1;
			}			
			if(newX==xLine){ /*this indicates that the unique {p,q} , where p = input while line number, has been found
				so the line number of the accompanying end to the input while has been found */
				return i;
			}else{
				//unique {p,q} found by above loop will be stored in whileLines 2D array
				xLines[len][0] = newX;
				xLines[len][1] = i;
				len+=1;
				i=0;
			}
		}
		return (-1);
	}      
        int findPreviousXLineNumber(int endXLineNum, int[][]arr){ //find line number of accompanying while statement of input end statement
		for(int i=0;i<arr.length;i++){
			if(arr[i][1]==endXLineNum){
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
                if(operator!=null){
                    String operators[] = {"*","/","+","-"};
                    int precedence[] = {4,4,2,2};
                    for(int i=0;i<operators.length;i++){
                            if(operator.equals(operators[i])){
                                    return precedence[i];
                            }
                    }
                }
		
		return -1;
	}
	Boolean isOperand(String operator){
		if(returnOperatorPrecedence(operator)==-1 && operator!=" " && isParanthesis(operator)==false){
			return true;
		}
		return false;
	}
	Boolean isParanthesis(String operator){
		if(operator.equals("(") | operator.equals(")")){
			return true;
		}
		return false;
	}	
        String convertFromInfixToPostfix(String line){
		String postfix = new String();
		Stack<String>temp = new Stack<String>();
		String infix[] = line.trim().split("\\s+");
		for(int i =0;i<infix.length;i++){
			if(isOperand(infix[i])){
				postfix+=(infix[i]+" ");
			}else if(isParanthesis(infix[i])==false){
				if(temp.empty()){
					temp.push(infix[i]);
				}else if(returnOperatorPrecedence(temp.peek())<returnOperatorPrecedence(infix[i])  ){
                                        temp.push(infix[i]);
                                }else if(returnOperatorPrecedence(temp.peek())>returnOperatorPrecedence(infix[i])){
					while(!temp.empty() && !temp.peek().equals("(")){
						postfix+=(temp.pop()+" ");
					}
                                        if(!temp.empty()) temp.pop();
					temp.push(infix[i]);
				}
			}else if(infix[i].equals("(")){
				temp.push(infix[i]);
			}else if(infix[i].equals(")")){
				while(!temp.empty() && !temp.peek().equals("(")){
					postfix+=(temp.pop()+" ");
				}
				if(!temp.empty()) temp.pop();
			}
		}
                while(!temp.empty() && !temp.peek().equals("(")){
                    postfix+=(temp.pop()+" ");
		}
                if(!temp.empty()) temp.pop();
                postfix = postfix.substring(0,postfix.length()-1);	
		return postfix;	
	}	
	int calculatePostfixExpression(String line){
		Stack<Integer>temp = new Stack<Integer>();
		String expr[] = line.trim().split("\\s+");
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
						result = secLastOp*lastOp;
						temp.push(result);
						break;
					case "+":
						result = secLastOp+lastOp;
						temp.push(result);
						break;
					case "-":
						result = secLastOp-lastOp;
						temp.push(result);
						break;
					case "/":
						result = secLastOp/lastOp;
						temp.push(result);
						break;
				}
                        }
		}
		return temp.pop();
	
	}
        public Integer returnCalculationValue(String line){
		return calculatePostfixExpression(convertFromInfixToPostfix(line));	
	}
        String returnComparativeOperator(String line){
            String compOp[] = {"<",">","==","!=",">=","<=","not"};
            for(int i=0;i<compOp.length;i++){
                if(line.contains(compOp[i])){
                    return compOp[i];
                }
            }
            return null;
            
        }
        Boolean hasConditionBeenPassed(String condition, String source[]){
            String operator = returnComparativeOperator(condition);
            Variable foundVar;
            String leftVariable = condition;
            leftVariable = leftVariable.trim().split(operator)[0].trim();
            foundVar = variables.get(leftVariable.hashCode());
            Integer value = returnCalculationValue(returnFormattedRightOfString(condition,operator,source));
            switch(operator){
                case "<":
                    if(foundVar.val < value){
                        return true;
                    }
                    break;
                case ">":
                    if(foundVar.val > value){
                        return true;
                    }
                    break;
                case "==":
                    if(foundVar.val == value){
                        return true;
                    }
                    break;
                case "!=":
                    if(foundVar.val != value){
                        return true;
                    }
                    break;
                case "<=":
                    if(foundVar.val <= value){
                        return true;
                    }
                    break;
                case ">=":
                    if(foundVar.val >= value){
                        return true;
                    }
                    break;
            }
                return false;     
        }
	public Boolean isNumeric(String s) { 
            return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
        }  
    
        public String returnFormattedRightOfString(String input, String operator, String[] source){ //returns the right side of the input from the operator and replaces any function call or variable with the value
            //Check for function call: 
            String var = input.trim().split(operator)[1];
            String rightExpr = new String();
            String rightOfEqual[] = var.trim().split(" ");
            Variable foundVar;
            String functionCall = null;
            Subroutine foundSub = null;
            for(int l=0;l<rightOfEqual.length;l++){
                    foundSub = null;
                    functionCall = null;
                    if(rightOfEqual[l].length()>1){
                        functionCall = getRoutineID(rightOfEqual[l]);
                        foundSub = routines.get(functionCall.hashCode());
                    }
                    foundVar = variables.get(rightOfEqual[l].hashCode());
                    if(!rightOfEqual[l].equals(" ")){
                        if(foundVar!=null){
                            rightExpr+=foundVar.val;
                        }else if(foundSub==null){
                            
                            if(!isOperand(rightOfEqual[l])){
                                rightExpr+=rightOfEqual[l];
                            }else{
                                rightExpr+=Integer.valueOf(rightOfEqual[l]);
                            }
                        }else{
                            ArrayList<Integer>param = new ArrayList<Integer>(Arrays.asList(getRoutineParameters(rightOfEqual[l])));
                            rightExpr += foundSub.routineCall(param, source);
                        }
                    }
                    rightExpr += " ";
            } //code above will replace any variables with their actual number equivalent
            rightExpr = rightExpr.substring(0,rightExpr.length()-1);
            return rightExpr;
        }
       
	public int interpretSourceCode(String fileSource, int startLine){
		setKeyWords();
		String sourceLines[] = fileSource.split(";"); //split source code into an array consisting of individual lines
		addSubroutines(sourceLines);
		String nextInstruction; //stores next instruction e.g. while, clear, incr, decr
		int whileIndicator[][] = new int[10][2]; /*declares an array of {p,q}, where {p,q} is an array, where p refers to line number of while, 
		and q refers to line number of accompanying end */
                int ifIndicator[][] = new int[10][2]; 
                int ifCount = 0;
                int whileCount = 0;
		String foundVariable; 
		int whilePos,redirect,ifPos,value;
                String condition,currentSub,postfix;
		redirect = 0;
		for(int i=startLine;i<sourceLines.length;i++){ //iterate through array of individual lines of source code
			foundVariable = "";
			nextInstruction = "";
			String words[] = sourceLines[i].trim().split(" "); //splits current line into an array of individual words
			System.out.println("");
			System.out.println("CURRENTLY EXECUTING LINE " + i);
			//Individual words in line
			for(int j=0;j<words.length;j++){ //iterate through array of individual words within current line
				if(!words[j].equals("")){ // <==> current word needs to not be empty
					if(keyWords.get(words[j].hashCode())==null && isParameter(words[j])==false && isNumeric(words[j])==false ){ //indicates that current word isn't a keyword so must be a variable
                                                if(variables.get(words[j].hashCode())==null){ //indicates that current variable hasn't been stored yet
							addVariable(words[j],0); //store found vaiable
						}
						foundVariable = words[j]; 
					}else if(words[j].equals("clear") | words[j].equals("incr") | words[j].equals("decr") 
						 | words[j].equals("endwhile") | words[j].equals("while") | words[j].equals("sub")
                                                | words[j].equals("=") | words[j].equals("return") | words[j].equals("if") | words[j].equals("endif")
                                                ){
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
                                                whilePos = findCurrentXPosInArray(i,whileIndicator);
						if(whilePos==-1){ //first time interpreter sees this while statement so it needs to include it in whileIndicator array
							whileIndicator[whileCount][0] = i; 
							whileIndicator[whileCount][1] = returnEndLineForX(i,sourceLines,"while");
							whileCount+=1;
						}
						//CHECK WHILE CONDITIONS
                                                condition = sourceLines[i].trim().split("while")[1].trim();
						if(hasConditionBeenPassed(condition,sourceLines)==false){ //assumes if condition hasn't been met for found variable
							redirect = returnEndLineForX(i,sourceLines,"while");
							System.out.println("Line " + i + ": While condition hasn't been met so program will exit while loop and redirect to line " + redirect);
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
							i =  redirect ;  //interpreter will branch to the next line out of the while loop
						}else{ //assumes while condition has been met for found variable
							System.out.println("Line " + i + ": The while condition has been met ");
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
						}
						
						break;
					case "endwhile":
                                            
						System.out.println("Line " + i + ": Finished executing line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
						redirect = findPreviousXLineNumber(i,whileIndicator) -1; // interpreter will branch to previous while
						System.out.println("Line " + i + ": Reached end of while loop - need to redirect to start of while loop at line " + (redirect+1));
						i = redirect;
						break;
                                        case "if":
                                                System.out.println("Line " + i + ": Going to execute if at line " + i);
                                                ifPos = findCurrentXPosInArray(i,ifIndicator);
						if(ifPos==-1){ //first time interpreter sees this while statement so it needs to include it in whileIndicator array
							ifIndicator[ifCount][0] = i; 
							ifIndicator[ifCount][1] = returnEndLineForX(i,sourceLines,"if");
							ifCount+=1;
						}
						//CHECK WHILE CONDITIONS
                                                condition = sourceLines[i].trim().split("if")[1].trim();
						if(hasConditionBeenPassed(condition,sourceLines)==false){ //assumes if condition hasn't been met for found variable
							redirect = returnEndLineForX(i,sourceLines,"if");
							System.out.println("Line " + i + ": If condition hasn't been met so program will exit if statement and redirect to line " + redirect);
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
							i =  redirect ;  //interpreter will branch to the next line out of the while loop
						}else{ //assumes while condition has been met for found variable
							System.out.println("Line " + i + ": The if condition has been met ");
							System.out.print("Line " + i + ": Current values of variables: ");
							printAllVariables();
						}
						
                                                break;
                                        case "endif":
                                                System.out.println("Line " + i + ": Finished executing line " + i );
						System.out.print("Line " + i + ": Current values of variables: ");
						printAllVariables();
                                                break;
					case "sub":
                                                System.out.println("Line " + i + ": Found subroutine declaration: ");
						currentSub = getRoutineID(sourceLines[i]);
						redirect = routines.get(currentSub.hashCode()).endLineNum;
                                                System.out.println("Line " + i + ": Going to redirect to end of subroutine at line "+redirect);
						i = redirect;
						break;
                                        case "return":
                                            Integer returnValue = returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"return",sourceLines));
                                            System.out.println("Line " + i + ": Going to return value ("+returnValue+") for current routine: ");
                                            return returnValue;
					case "=":
                                                System.out.println("Line " + i +": Need to process " + sourceLines[i].trim() + " statement ");
						//find variable to left of =
						String leftVariable = sourceLines[i]; //.split("\\s+")[0];
						leftVariable = leftVariable.trim().split("=")[0].trim();
						foundVar = variables.get(leftVariable.hashCode());
                                                
                                                
                                                value = returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"=",sourceLines));
                                                System.out.println("Line " + i +": Finished " + leftVariable + " = " + returnFormattedRightOfString(sourceLines[i],"=",sourceLines) + " = " + value);
						foundVar.val = value;
						break;
                                        
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
