* C-Minus Compilation to TM Code
* File: TMSimulator/1
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
* Processing local var declaration: x
* Processing local var declaration: y
* Processing local var declaration: z
* -> op
* Looking up: z
13: LDA 0,-4(5)	load id
14: ST 0,-5(5)	op: push tmp left
* -> cosntant: 0
15: LDC 0,0(0)	load constant
* <- constant
16: LD 1,-5(5)	op: load left
17: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: z
18: LDA 0,-4(5)	load id
19: ST 0,-5(5)	op: push tmp left
* -> op
* Looking up: x
20: LD 0,-2(5)	load id
21: ST 0,-6(5)	op: push tmp left
* -> op
* Looking up: y
22: LD 0,-3(5)	load id
23: ST 0,-7(5)	op: push tmp left
* Looking up: y
24: LD 0,-3(5)	load id
25: LD 1,-7(5)	op: load left
26: MUL 0,1,0	op *
* <- op
27: LD 1,-6(5)	op: load left
28: ADD 0,1,0	op +
* <- op
29: LD 1,-5(5)	op: load left
30: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: z
31: LDA 0,-4(5)	load id
32: ST 0,-5(5)	op: push tmp left
* -> op
* -> op
* Looking up: x
33: LD 0,-2(5)	load id
34: ST 0,-6(5)	op: push tmp left
* Looking up: y
35: LD 0,-3(5)	load id
36: LD 1,-6(5)	op: load left
37: MUL 0,1,0	op *
* <- op
38: ST 0,-6(5)	op: push tmp left
* Looking up: y
39: LD 0,-3(5)	load id
40: LD 1,-6(5)	op: load left
41: ADD 0,1,0	op +
* <- op
42: LD 1,-5(5)	op: load left
43: ST 0,0(1)	assign: store value
* <- op
* Looking up: z
44: LD 0,-4(5)	load id
45: ST 0,-7(5)	store arg val
* -> call of function: output
46: ST 5,-5(5)	push ofp
47: LDA 5,-5(5)	push frame
48: LDA 0,1(7)	load ac with ret ptr
49: LDA 7,-43(7)	Jump to function location
50: LD 5,0(5)	pop frame
* <- call
51: LD 7,-1(5)	return to caller
11: LDA 7,40(7)	Jump around function: main
* End function: main
52: ST 5,0(5)	push ofp
53: LDA 5,0(5)	push frame
54: LDA 0,1(7)	load ac with ret ptr
55: LDA 7,-44(7)	jump to main location
56: LD 5,0(5)	pop frame
57: HALT 0,0,0	End
