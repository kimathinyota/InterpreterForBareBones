/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barebones;

import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

/**
 *
 * @author kimat
 */

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
        public String output;
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
                Integer val = interpretRoutine.interpretSourceCode(trimSourceCode(source,0,endLineNum),startLineNum+1);
                output = interpretRoutine.output;
                return val;
                
	}
        public void clear(){
            identifier = null;
            
            
        }
	public Subroutine(String id, int start, int end, ArrayList<Variable>param){
		identifier = id;
		startLineNum = start;
		endLineNum = end;
		parameters = param;
	}
}		
class InterpretBareBones{
	//Hash tables have an average look up of O(1) so searching for an element in a hash table is much quicker than using an array
	Hashtable<Integer, Variable> variables = new Hashtable<Integer, Variable>(); /*as the number of variables increases, the look up
	time for each variable stays constant */
	Hashtable<Integer, String> keyWords = new Hashtable<Integer, String>(); //don't have to write a for loop to search array
	Hashtable<Integer, Subroutine> routines = new Hashtable<Integer, Subroutine>();	
        public String output;
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
	public String printAllVariables(){ //print current values of all variables
                String output = new String();
		Set<Integer> keys = variables.keySet();
		output +=(";{ ");
		for(Integer key: keys){
			output +=(variables.get(key).identifier + " = " + variables.get(key).val+ ", " );
		}
                output = output.substring(0,output.length()-1);
		output +=("}");
		output +=(";");
                return output;
	}
        public String printFinalVariables(){ //print current values of all variables
                String output = new String();
		Set<Integer> keys = variables.keySet();
		for(Integer key: keys){
			output +=(variables.get(key).identifier + " = " + variables.get(key).val+ "; " );
		}
                output = output.substring(0,output.length()-1);
                return output;
	}
	public String interpretFile(String fileName){
		String fileSource = readInputFile(fileName);
                String out= new String();
		interpretSourceCode(fileSource,0);
                return output;
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
                            output += foundSub.output;
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
			output +=(";");
			output +=("CURRENTLY EXECUTING LINE " + i+";");
			//Individual words in line
			for(int j=0;j<words.length;j++){ //iterate through array of individual words within current line
				if(!words[j].equals("")){ // <==> current word needs to not be empty
					if(words[j].contains("//")){
                                            output +=("Line " + i + ": Line is a comment so it will be ignored ; ");
                                            break;
					}else if(keyWords.get(words[j].hashCode())==null && isParameter(words[j])==false && isNumeric(words[j])==false ){ //indicates that current word isn't a keyword so must be a variable
                                            if(variables.get(words[j].hashCode())==null){ //indicates that current variable hasn't been stored yet
                                                    addVariable(words[j],0); //store found vaiable
                                            }
                                            foundVariable = words[j]; 
					}else if(words[j].equals("clear") | words[j].equals("incr") | words[j].equals("decr") 
                                            | words[j].equals("endwhile") | words[j].equals("while") | words[j].equals("sub")
                                           | words[j].equals("=") | words[j].equals("return") | words[j].equals("if") | words[j].equals("endif")
                                           ){
                                            nextInstruction = words[j]; //update next instruction
						
					}
				}
			}
                        
			Variable foundVar = variables.get(foundVariable.hashCode()); //foundVar references variable found in the current line
			switch(nextInstruction){ 
				 	case "clear":
						foundVar.val = 0; 
						output +=("Line " + i + ": Finished executing clear instruction on line " + i + ";" );
						output +=("Line " + i + ": Current values of variables: ");
						output+=printAllVariables();
						break;
					case "decr":
						foundVar.val -=1;
						output +=("Line " + i + ": Finished executing decr instruction on line " + i + ";");
						output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
						break;
					case "incr":
						foundVar.val +=1;
						output +=("Line " + i + ": Finished executing incr instruction on line " + i +";");
						output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
						break;
					case "while":
						output +=("Line " + i + ": Going to execute while at line " + i+";");
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
							output +=("Line " + i + ": While condition hasn't been met so program will exit while loop and redirect to line " + redirect+";");
							output +=("Line " + i + ": Current values of variables: ");
							output +=printAllVariables();
							i =  redirect ;  //interpreter will branch to the next line out of the while loop
						}else{ //assumes while condition has been met for found variable
							output +=("Line " + i + ": The while condition has been met ;");
							output +=("Line " + i + ": Current values of variables: ");
							output +=printAllVariables();
						}
						
						break;
					case "endwhile":
                                            
						output +=("Line " + i + ": Finished executing line " + i + ";");
						output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
						redirect = findPreviousXLineNumber(i,whileIndicator) -1; // interpreter will branch to previous while
						output +=("Line " + i + ": Reached end of while loop - need to redirect to start of while loop at line " + (redirect+1)+";");
						i = redirect;
						break;
                                        case "if":
                                                output +=("Line " + i + ": Going to execute if at line " + i+";");
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
							output +=("Line " + i + ": If condition hasn't been met so program will exit if statement and redirect to line " + redirect+";");
							output +=("Line " + i + ": Current values of variables: ");
							output +=printAllVariables();
							i =  redirect ;  //interpreter will branch to the next line out of the while loop
						}else{ //assumes while condition has been met for found variable
							output +=("Line " + i + ": The if condition has been met ;");
							output +=("Line " + i + ": Current values of variables: ");
							output +=printAllVariables();
						}
						
                                                break;
                                        case "endif":
                                                output +=("Line " + i + ": Finished executing line " + i +";");
						output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
                                                break;
					case "sub":
                                                output +=("Line " + i + ": Found subroutine declaration: ;");
						currentSub = getRoutineID(sourceLines[i]);
						redirect = routines.get(currentSub.hashCode()).endLineNum;
                                                output +=("Line " + i + ": Going to redirect to end of subroutine at line "+redirect+";");
						i = redirect;
                                                
						break;
                                        case "return":
                                            Integer returnValue = returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"return",sourceLines));
                                            output +=("Line " + i + ": Going to return value ("+returnValue+") for current routine: ;");
                                            output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
                                           
                                            return returnValue;
					case "=":
                                                output +=("Line " + i +": Need to process " + sourceLines[i].trim() + " statement ;");
						//find variable to left of =
						String leftVariable = sourceLines[i]; //.split("\\s+")[0];
						leftVariable = leftVariable.trim().split("=")[0].trim();
						foundVar = variables.get(leftVariable.hashCode());
                                                
                                                
                                                value = returnCalculationValue(returnFormattedRightOfString(sourceLines[i],"=",sourceLines));
                                                output +=("Line " + i +": Finished " + leftVariable + " = " + returnFormattedRightOfString(sourceLines[i],"=",sourceLines) + " = " + value+";");
						foundVar.val = value;
                                                output +=("Line " + i + ": Current values of variables: ");
						output +=printAllVariables();
						break;
                                        
			}		
			redirect = i;
		}
		output +=("Last Line " + redirect + ": Finished stepping through program. ;");
		output +=("Last Line " + redirect + ": FINAL VALUE OF VARIABLES: ");
		output +=printAllVariables();
             
                return 0;
	
	}		  
}

public class InterpreterGUI extends javax.swing.JFrame {
    public InterpretBareBones myInterpreter; 
    String output;
    /**
     * Creates new form InterpreterGUI
     */
    public InterpreterGUI() {
        initComponents();
        finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
        stepThroughCode.setText("               SOURCE CODE HAS NOT BEEN INTERPRETED YET");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        currentFile = new javax.swing.JTextField();
        interpretButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stepThroughCode = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        finalVariables = new javax.swing.JTextArea();
        importFile = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        inputCode = new javax.swing.JTextArea();
        inputFile = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1387, 780));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SOURCE CODE");

        currentFile.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        currentFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentFileActionPerformed(evt);
            }
        });

        interpretButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        interpretButton.setText("INTERPRET CODE");
        interpretButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interpretButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("STEP THROUGH CODE");
        jLabel3.setToolTipText("");

        stepThroughCode.setColumns(20);
        stepThroughCode.setFont(new java.awt.Font("Courier New", 0, 16)); // NOI18N
        stepThroughCode.setRows(5);
        stepThroughCode.setText("        SOURCE CODE HAS NOT BEEN INTERPRETED YET");
        stepThroughCode.setWrapStyleWord(true);
        jScrollPane1.setViewportView(stepThroughCode);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("FINAL VARIABLE VALUES");
        jLabel4.setToolTipText("FINAL VARIABLE VALUES");

        finalVariables.setColumns(20);
        finalVariables.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        finalVariables.setRows(5);
        finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
        finalVariables.setToolTipText("");
        jScrollPane2.setViewportView(finalVariables);

        importFile.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        importFile.setText("IMPORT FILE");
        importFile.setToolTipText("");
        importFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFileActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 48)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("KEBB INTERPRETER");

        inputCode.setColumns(20);
        inputCode.setFont(new java.awt.Font("Courier New", 0, 16)); // NOI18N
        inputCode.setRows(5);
        inputCode.setToolTipText("");
        inputCode.setWrapStyleWord(true);
        jScrollPane3.setViewportView(inputCode);

        inputFile.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputFileActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("CURRENT FILE NAME");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("ENTER FILE NAME");

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(1287, Short.MAX_VALUE)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(interpretButton, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane3)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                    .addComponent(currentFile, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 713, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(inputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(importFile, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(importFile, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentFile, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(interpretButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>                        

    
    
    private void importFileActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
        myInterpreter = new InterpretBareBones();
        String space = "                           ";
   
        finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
        stepThroughCode.setText("               SOURCE CODE HAS NOT BEEN INTERPRETED YET");
        inputCode.setText("");
        String fileName = inputFile.getText();
        
        String fileSource = myInterpreter.readInputFile(fileName+".txt");        
        if(fileSource==null){
            JOptionPane.showMessageDialog(null, "File couldn't been found");
        }else{
            String code[] = fileSource.split(";");
            currentFile.setText(fileName);
            for(int i=0;i<code.length;i++){
                inputCode.append(code[i]+";\n");
            }
        } 
    }                                          

    private void interpretButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
        finalVariables.setText("");
        stepThroughCode.setText("");
        myInterpreter = new InterpretBareBones();
        String currentFileName = currentFile.getText();
        String sourceCode = inputCode.getText();
       if(currentFileName.equals("") && sourceCode.equals("")){ //file hasn't been opened
           JOptionPane.showMessageDialog(null, "You need to import a file or type BareBones code into source code editor");
           finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
           stepThroughCode.setText("               SOURCE CODE HAS NOT BEEN INTERPRETED YET");
       }else if(sourceCode.equals("")){ //file is empty
           JOptionPane.showMessageDialog(null, "The current file imported is empty");
           finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
           stepThroughCode.setText("               SOURCE CODE HAS NOT BEEN INTERPRETED YET");
       }else if(currentFileName.equals("")){
           JOptionPane.showMessageDialog(null, "If you wish to create a new file you must give it a name using the CURRENT FILE NAME input box");
           finalVariables.setText("          SOURCE CODE HAS NOT BEEN INTERPRETED YET");
           stepThroughCode.setText("               SOURCE CODE HAS NOT BEEN INTERPRETED YET");
       }else{
           try{
                    finalVariables.setText("");
                    stepThroughCode.setText("");
                    String code[] = sourceCode.split(";");
                    PrintWriter writer = new PrintWriter(currentFileName+".txt", "UTF-8");
                    for(int i=0;i<code.length;i++){
                        writer.println(code[i]+";");
                    }
                    writer.close();
                    try{
                        String output = myInterpreter.interpretFile(currentFileName+".txt");
                        String response[] = output.split(";");
                        for(int i=0;i<response.length;i++){
                            stepThroughCode.append(response[i]+"\n");
                        }
                       output = myInterpreter.printFinalVariables();
                       String resp[] = output.split(";");
                       
                       for(int i=0;i<resp.length;i++){
                           finalVariables.append(resp[i].trim()+"\n");
                        }
                    }catch (Exception f){
                        JOptionPane.showMessageDialog(null, "There is something wrong with your source code ("+currentFileName+".txt)");
                    }
                    
            }catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Error when trying to save "+currentFileName);
            }
       }
    }                                               

    private void currentFileActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void inputFileActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    /**
     * @param args the command line arguments
     */
    
    

    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterpreterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterpreterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterpreterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterpreterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InterpreterGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JTextField currentFile;
    private javax.swing.JTextArea finalVariables;
    private javax.swing.JButton importFile;
    private javax.swing.JTextArea inputCode;
    private javax.swing.JTextField inputFile;
    private javax.swing.JButton interpretButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea stepThroughCode;
    // End of variables declaration                   
}
