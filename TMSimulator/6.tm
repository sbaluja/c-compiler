* C-Minus Compilation to TM Code
* File: TMSimulator/6
* Standard prelude:
0: LD 6,0(0)	load gp with maxaddress
1: LDA 5,0(6)	copy to gp to fp
2: ST 0,0(0)	clear location 0
* Jump around i/o routines here
* Function input
4: ST 0,-1(5)	store return
5: IN 0,0,0	Input
6: LD 7,-1(5)	Return to caller
* code for output routine
7: ST 0,-1(5)	store return
8: LD 0,-2(5)	load emitput value
9: OUT 0,0,0	output
10: LD 7,-1(5)	return to caller
3: LDA 7,7(7)	Jump around I/O code
* End of standard prelude.
* Function: main
12: ST 0,-1(5)	store return
* Processing local var declaration: m
* Processing local var[] declaration: array
* -> if
* -> test
* -> op
* Looking up: array
13: LDA 0,-3(5)	load id
14: ST 0,-5(5)	op: push tmp left
* Looking up: m
15: LD 0,-2(5)	load id
16: JLT 0,1(7)	Halt if subscript < 0
17: LDA 7,1(7)	Jump over if not
18: HALT 0,0,0	End (rip)
19: LD 1,-5(5)	load array base addr
20: SUB 0,1,0	base is at top of array
21: LD 0,0(0)	load value at array index
22: ST 0,-5(5)	op: push tmp left
* -> cosntant: 0
23: LDC 0,0(0)	load constant
* <- constant
24: LD 1,-5(5)	op: load left
25: SUB 0,1,0	op >
* <- op
26: JGT 0,2(7)	br if true
27: LDC 0,0(0)	false case
28: LDA 7,1(7)	unconditional jump
29: LDC 0,1(0)	true case
* If jump location
* <- test
* -> return
* Looking up: m
31: LD 0,-2(5)	load id
32: LD 7,-1(5)	return to caller
* <- return
* Jump to end of if block
30: JEQ 0,3(7)	Jump over then block
33: LDA 7,0(7)	Leave then block
* <- if
* -> return
* Looking up: m
34: LD 0,-2(5)	load id
35: LD 7,-1(5)	return to caller
* <- return
36: LD 7,-1(5)	return to caller
11: LDA 7,25(7)	Jump around function: main
* End function: main
37: ST 5,0(5)	push ofp
38: LDA 5,0(5)	push frame
39: LDA 0,1(7)	load ac with ret ptr
40: LDA 7,-29(7)	jump to main location
41: LD 5,0(5)	pop frame
42: HALT 0,0,0	End
