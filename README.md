Interpreter For Bare Bones by Kimathi Nyota
========================================

Key Information:
---------------
InterpretBareBones.java -> this refers to java source file for command line based interpreter. 

dist/ this folder contains source code and jar file for GUI interpreter.

Bare Bones Language Syntax:
---------------------------

Bare Bones is the simple language that Brookshear uses in his book, 'Computer Science: an Overview', to illustrate the power of Turing complete machines and investigate the halting problem.

Bare Bones has three simple commands for manipulating a variable: clear name; incr name; decr name; ...which respectively sets variable name to zero, increments it by one and decrements it by one.

The language also contains one control sequence, a simple loop:
while name not 0 do; ... endwhile; ... where name is a variable.

Note that variables need not be declared before they are used and must be non-negative integers. Statements are delimited by the ; character.

Added Features:
-----------------
while condition; code; endwhile;
if condition; code; endif;
EXAMPLES: condition could equal var > integer or var == integer

OPERATORS: == (check if two integers are equal), != (not equal), >=, <=, >, <, = (set var = some integer), -, +, *, /

sub routine(var1,var2,var3,..); code; endsub; ==> this is a subroutine

return value ==> use this within a subroutine to return a value

Example programs:
-----------------
Program 1: Adding 5 and 7 : 

sub add(x,y); 

     clear z; 
   
     z = x + y; 
   
     return z; 
   
endsub; 

clear x; 

clear y; 

x = 5;

y = 7;

clear z; 

z = add(x,y);


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

y = difference(2,3); 

clear h; 

h = difference(3,8); 

clear p; 

p = difference(4,4); 


Program 3: Returning square of input number : 

sub square(x); 

     return x * x; 

endsub; 

clear z; 

z = square(5);


Challenge specifications:
-------------------------
This weeks challenge is to implement a Bare Bones interpreter. The program should take a text file containing a bare bones program as input and execute each statement in turn. After each statement has been executed it should output the state of all the variables in the system to form a record of execution.
