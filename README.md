KEBB Interpreter by Kimathi Nyota
========================================

Key Information:
-----------------
KEBB (Kimathi's Extended Bare Bones) : This is a simple integer-only programming language that is based upon Bare Bones.

InterpretBareBones.java -> this refers to java source file for command line based interpreter. 

dist/ this folder contains source code and jar file for GUI interpreter.

Bare Bones Language Syntax:
---------------------------

Bare Bones is the simple language that Brookshear uses in his book, 'Computer Science: an Overview', to illustrate the power of Turing complete machines and investigate the halting problem.

Bare Bones has three simple commands for manipulating a variable: clear name; incr name; decr name; ...which respectively sets variable name to zero, increments it by one and decrements it by one.

The language also contains one control sequence, a simple loop:
while name not 0 do; ... endwhile; ... where name is a variable.

Note that variables need not be declared before they are used and must be non-negative integers. Statements are delimited by the ; character.

KEBB Syntax:
-----------------
DECLARING VARIABLES
Syntax [clear variableName;] (declares an integer variable called variableName with a value of 0)

ADDING COMMENTS: 
Syntax [ // add comment ; ] - Use // to add a comment and DON'T FORGET TO ADD SEMICOLON AT THE END OF EACH COMMENT 

CONDITION STATEMENTS: statement that involves a comparison between two values. 
Syntax [ var operator val; ] 
var = variable, val = mathematical expression, 
Operator (comparitive) :  == (equal), != (not equal) , >= , <=, >, <, not 0 (special case where val = 0) 


Selection:  IF STATEMENTS
Syntax [if condition; ... ; endif; ]  

Iteration: WHILE LOOPS
Syntax [while condition; ... ; endwhile; ] 

Subroutines: 
DECLARATION SYNTAX [sub name(x,y,...); ... ; endsub; ] 
For a routine to be declared there can't be any space between routine name and parameter brackets. 
For function declarations you need to add a return statement 
Syntax [return val;], where val = mathematical expression.
For procedure declarations you shouldn't add a return statement.
CALLING FUNCTION SYNTAX [var = routineName(parameters)], where routineName is a function
CALLING PROCEDUCRE SYNTAX [routineName(parameters);], where routineName is a procedure


Mathematical expressions (val) : consists of variables, numbers, operators and brackets such that val = integer 
Syntax: There must be a space between each operator (*, -, +, /) , operand (variable or number), and bracket 
and each opening bracket must have an accompanying closing bracket.
Valid examples: val = 2 * x * y; , val = ( 2 * ( 4 * 5 * b ) + 4 ) / 3;


Example programs:
-----------------
Program 1: Adding 5 and 7 : 

    sub add(x,y); 
        return x + y;    
    endsub; 
    clear z; 
    z = add(3,4);
    //above code sets z = 3 + 4 = 7;
    
Program 2: Returning difference between 2 input numbers : 

    sub difference(x,y); 
        if y > x;  
            return ( y - x ); 
        endif; 
        if x > y; 
            return ( x - y ); 
        endif;  
        return 0; 
    endsub; 
    clear y;
    y = difference(2,3); //y = 1;
    clear h; 
    h = difference(3,8); // h = 5;
    clear p; 
    p = difference(4,4); //p = 0;


Program 3: Returning exponent given base and power : 

    sub exponent(x,power); // returns x ^ power;
        clear z;
        z = power;
        clear tot;
        tot = 1;
        while z > 0;
            tot = tot * x;
            z = z - 1;
        endwhile;
        return tot;
    endsub;
    clear z;
    z = exponent(2,3); //z = 2^ 3 = 8;
    
Program 4: Recursive approach to calculate factorial

    sub factorial(x);
        if x == 1; //terminating statement
            return 1;
            endif;
        clear z;
        z = x - 1;
        return x * factorial(z); // recursive call
    endsub;
    clear z;
    z = factorial(3);  //z = 3 * 2 * 1 = 6
   


Challenge specifications:
-------------------------
This weeks challenge is to implement a Bare Bones interpreter. The program should take a text file containing a bare bones program as input and execute each statement in turn. After each statement has been executed it should output the state of all the variables in the system to form a record of execution.
