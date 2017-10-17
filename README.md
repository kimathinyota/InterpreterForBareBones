# InterpreterForBareBones by Kimathi Nyota

Bare Bones Language Syntax:
Bare Bones is the simple language that Brookshear uses in his book, 'Computer Science: an Overview', to illustrate the power of Turing complete machines and investigate the halting problem.
Bare Bones has three simple commands for manipulating a variable:
clear name; incr name; decr name;
...which respectively sets variable name to zero, increments it by one and decrements it by one.
The language also contains one control sequence, a simple loop:
while name not 0 do; ... end;
... where name is a variable. Note that variables need not be declared before they are used and must be non-negative integers. Statements are delimited by the ; character.

Challenge specifications:
This weeks challenge is to implement a Bare Bones interpreter. The program should take a text file containing a bare bones program as input and execute each statement in turn. After each statement has been executed it should output the state of all the variables in the system to form a record of execution.
