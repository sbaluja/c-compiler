* C-Minus Compilation to TM Code
* File: TMSimulator/9
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
* Function: printForward
12: ST 0,-1(5)	store return
* Processing local var[] declaration: arr
* Processing local var declaration: len
* Processing local var declaration: i
* -> op
* Looking up: i
13: LDA 0,-4(5)	load id
14: ST 0,-5(5)	op: push tmp left
* -> cosntant: 0
15: LDC 0,0(0)	load constant
* <- constant
16: LD 1,-5(5)	op: load left
17: ST 0,0(1)	assign: store value
* <- op
* -> While
* -> op
* Looking up: i
18: LD 0,-4(5)	load id
19: ST 0,-5(5)	op: push tmp left
* Looking up: len
20: LD 0,-3(5)	load id
21: LD 1,-5(5)	op: load left
22: SUB 0,1,0	op <
* <- op
23: JLT 0,2(7)	br if true
24: LDC 0,0(0)	false case
25: LDA 7,1(7)	unconditional jump
26: LDC 0,1(0)	true case
* If jump location
* Looking up: arr
28: LD 0,-2(5)	load id
29: ST 0,-7(5)	op: push tmp left
* Looking up: i
30: LD 0,-4(5)	load id
31: JLT 0,1(7)	Halt if subscript < 0
32: LDA 7,1(7)	Jump over if not
33: HALT 0,0,0	End (rip)
34: LD 1,-7(5)	load array base addr
35: SUB 0,1,0	base is at top of array
36: LD 0,0(0)	load value at array index
37: ST 0,-7(5)	store arg val
* -> call of function: output
38: ST 5,-5(5)	push ofp
39: LDA 5,-5(5)	push frame
40: LDA 0,1(7)	load ac with ret ptr
41: LDA 7,-35(7)	Jump to function location
42: LD 5,0(5)	pop frame
* <- call
* -> op
* Looking up: i
43: LDA 0,-4(5)	load id
44: ST 0,-5(5)	op: push tmp left
* -> op
* Looking up: i
45: LD 0,-4(5)	load id
46: ST 0,-6(5)	op: push tmp left
* -> cosntant: 1
47: LDC 0,1(0)	load constant
* <- constant
48: LD 1,-6(5)	op: load left
49: ADD 0,1,0	op +
* <- op
50: LD 1,-5(5)	op: load left
51: ST 0,0(1)	assign: store value
* <- op
52: LDA 7,-35(7)	Jump back to test condition
27: JEQ 0,25(7)	Jump over body
* <- While
53: LD 7,-1(5)	return to caller
11: LDA 7,42(7)	Jump around function: printForward
* End function: printForward
* Function: printBackward
55: ST 0,-1(5)	store return
* Processing local var[] declaration: arr
* Processing local var declaration: len
* Processing local var declaration: i
* -> op
* Looking up: i
56: LDA 0,-4(5)	load id
57: ST 0,-5(5)	op: push tmp left
* -> op
* Looking up: len
58: LD 0,-3(5)	load id
59: ST 0,-6(5)	op: push tmp left
* -> cosntant: 1
60: LDC 0,1(0)	load constant
* <- constant
61: LD 1,-6(5)	op: load left
62: SUB 0,1,0	op -
* <- op
63: LD 1,-5(5)	op: load left
64: ST 0,0(1)	assign: store value
* <- op
* -> While
* -> op
* Looking up: i
65: LD 0,-4(5)	load id
66: ST 0,-5(5)	op: push tmp left
* -> cosntant: 0
67: LDC 0,0(0)	load constant
* <- constant
68: LD 1,-5(5)	op: load left
69: SUB 0,1,0	op >=
* <- op
70: JGE 0,2(7)	br if true
71: LDC 0,0(0)	false case
72: LDA 7,1(7)	unconditional jump
73: LDC 0,1(0)	true case
* If jump location
* Looking up: arr
75: LD 0,-2(5)	load id
76: ST 0,-7(5)	op: push tmp left
* Looking up: i
77: LD 0,-4(5)	load id
78: JLT 0,1(7)	Halt if subscript < 0
79: LDA 7,1(7)	Jump over if not
80: HALT 0,0,0	End (rip)
81: LD 1,-7(5)	load array base addr
82: SUB 0,1,0	base is at top of array
83: LD 0,0(0)	load value at array index
84: ST 0,-7(5)	store arg val
* -> call of function: output
85: ST 5,-5(5)	push ofp
86: LDA 5,-5(5)	push frame
87: LDA 0,1(7)	load ac with ret ptr
88: LDA 7,-82(7)	Jump to function location
89: LD 5,0(5)	pop frame
* <- call
* -> op
* Looking up: i
90: LDA 0,-4(5)	load id
91: ST 0,-5(5)	op: push tmp left
* -> op
* Looking up: i
92: LD 0,-4(5)	load id
93: ST 0,-6(5)	op: push tmp left
* -> cosntant: 1
94: LDC 0,1(0)	load constant
* <- constant
95: LD 1,-6(5)	op: load left
96: SUB 0,1,0	op -
* <- op
97: LD 1,-5(5)	op: load left
98: ST 0,0(1)	assign: store value
* <- op
99: LDA 7,-35(7)	Jump back to test condition
74: JEQ 0,25(7)	Jump over body
* <- While
100: LD 7,-1(5)	return to caller
54: LDA 7,46(7)	Jump around function: printBackward
* End function: printBackward
* Function: main
102: ST 0,-1(5)	store return
* Processing local var[] declaration: x
* -> op
* Looking up: x
103: LDA 0,-2(5)	load id
104: ST 0,-12(5)	op: push tmp left
* -> cosntant: 0
105: LDC 0,0(0)	load constant
* <- constant
106: JLT 0,1(7)	Halt if subscript < 0
107: LDA 7,1(7)	Jump over if not
108: HALT 0,0,0	End (rip)
109: LD 1,-12(5)	load array base addr
110: SUB 0,1,0	base is at top of array
111: ST 0,-12(5)	op: push tmp left
* -> cosntant: 0
112: LDC 0,0(0)	load constant
* <- constant
113: LD 1,-12(5)	op: load left
114: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
115: LDA 0,-2(5)	load id
116: ST 0,-12(5)	op: push tmp left
* -> cosntant: 1
117: LDC 0,1(0)	load constant
* <- constant
118: JLT 0,1(7)	Halt if subscript < 0
119: LDA 7,1(7)	Jump over if not
120: HALT 0,0,0	End (rip)
121: LD 1,-12(5)	load array base addr
122: SUB 0,1,0	base is at top of array
123: ST 0,-12(5)	op: push tmp left
* -> cosntant: 1
124: LDC 0,1(0)	load constant
* <- constant
125: LD 1,-12(5)	op: load left
126: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
127: LDA 0,-2(5)	load id
128: ST 0,-12(5)	op: push tmp left
* -> cosntant: 2
129: LDC 0,2(0)	load constant
* <- constant
130: JLT 0,1(7)	Halt if subscript < 0
131: LDA 7,1(7)	Jump over if not
132: HALT 0,0,0	End (rip)
133: LD 1,-12(5)	load array base addr
134: SUB 0,1,0	base is at top of array
135: ST 0,-12(5)	op: push tmp left
* -> cosntant: 2
136: LDC 0,2(0)	load constant
* <- constant
137: LD 1,-12(5)	op: load left
138: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
139: LDA 0,-2(5)	load id
140: ST 0,-12(5)	op: push tmp left
* -> cosntant: 3
141: LDC 0,3(0)	load constant
* <- constant
142: JLT 0,1(7)	Halt if subscript < 0
143: LDA 7,1(7)	Jump over if not
144: HALT 0,0,0	End (rip)
145: LD 1,-12(5)	load array base addr
146: SUB 0,1,0	base is at top of array
147: ST 0,-12(5)	op: push tmp left
* -> cosntant: 3
148: LDC 0,3(0)	load constant
* <- constant
149: LD 1,-12(5)	op: load left
150: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
151: LDA 0,-2(5)	load id
152: ST 0,-12(5)	op: push tmp left
* -> cosntant: 4
153: LDC 0,4(0)	load constant
* <- constant
154: JLT 0,1(7)	Halt if subscript < 0
155: LDA 7,1(7)	Jump over if not
156: HALT 0,0,0	End (rip)
157: LD 1,-12(5)	load array base addr
158: SUB 0,1,0	base is at top of array
159: ST 0,-12(5)	op: push tmp left
* -> cosntant: 4
160: LDC 0,4(0)	load constant
* <- constant
161: LD 1,-12(5)	op: load left
162: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
163: LDA 0,-2(5)	load id
164: ST 0,-12(5)	op: push tmp left
* -> cosntant: 5
165: LDC 0,5(0)	load constant
* <- constant
166: JLT 0,1(7)	Halt if subscript < 0
167: LDA 7,1(7)	Jump over if not
168: HALT 0,0,0	End (rip)
169: LD 1,-12(5)	load array base addr
170: SUB 0,1,0	base is at top of array
171: ST 0,-12(5)	op: push tmp left
* -> cosntant: 5
172: LDC 0,5(0)	load constant
* <- constant
173: LD 1,-12(5)	op: load left
174: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
175: LDA 0,-2(5)	load id
176: ST 0,-12(5)	op: push tmp left
* -> cosntant: 6
177: LDC 0,6(0)	load constant
* <- constant
178: JLT 0,1(7)	Halt if subscript < 0
179: LDA 7,1(7)	Jump over if not
180: HALT 0,0,0	End (rip)
181: LD 1,-12(5)	load array base addr
182: SUB 0,1,0	base is at top of array
183: ST 0,-12(5)	op: push tmp left
* -> cosntant: 6
184: LDC 0,6(0)	load constant
* <- constant
185: LD 1,-12(5)	op: load left
186: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
187: LDA 0,-2(5)	load id
188: ST 0,-12(5)	op: push tmp left
* -> cosntant: 7
189: LDC 0,7(0)	load constant
* <- constant
190: JLT 0,1(7)	Halt if subscript < 0
191: LDA 7,1(7)	Jump over if not
192: HALT 0,0,0	End (rip)
193: LD 1,-12(5)	load array base addr
194: SUB 0,1,0	base is at top of array
195: ST 0,-12(5)	op: push tmp left
* -> cosntant: 7
196: LDC 0,7(0)	load constant
* <- constant
197: LD 1,-12(5)	op: load left
198: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
199: LDA 0,-2(5)	load id
200: ST 0,-12(5)	op: push tmp left
* -> cosntant: 8
201: LDC 0,8(0)	load constant
* <- constant
202: JLT 0,1(7)	Halt if subscript < 0
203: LDA 7,1(7)	Jump over if not
204: HALT 0,0,0	End (rip)
205: LD 1,-12(5)	load array base addr
206: SUB 0,1,0	base is at top of array
207: ST 0,-12(5)	op: push tmp left
* -> cosntant: 8
208: LDC 0,8(0)	load constant
* <- constant
209: LD 1,-12(5)	op: load left
210: ST 0,0(1)	assign: store value
* <- op
* -> op
* Looking up: x
211: LDA 0,-2(5)	load id
212: ST 0,-12(5)	op: push tmp left
* -> cosntant: 9
213: LDC 0,9(0)	load constant
* <- constant
214: JLT 0,1(7)	Halt if subscript < 0
215: LDA 7,1(7)	Jump over if not
216: HALT 0,0,0	End (rip)
217: LD 1,-12(5)	load array base addr
218: SUB 0,1,0	base is at top of array
219: ST 0,-12(5)	op: push tmp left
* -> cosntant: 9
220: LDC 0,9(0)	load constant
* <- constant
221: LD 1,-12(5)	op: load left
222: ST 0,0(1)	assign: store value
* <- op
* Looking up: x
223: LDA 0,-2(5)	load id
224: ST 0,-14(5)	store arg val
* -> cosntant: 10
225: LDC 0,10(0)	load constant
* <- constant
226: ST 0,-15(5)	store arg val
* -> call of function: printForward
227: ST 5,-12(5)	push ofp
228: LDA 5,-12(5)	push frame
229: LDA 0,1(7)	load ac with ret ptr
230: LDA 7,-219(7)	Jump to function location
231: LD 5,0(5)	pop frame
* <- call
* Looking up: x
232: LDA 0,-2(5)	load id
233: ST 0,-14(5)	store arg val
* -> cosntant: 10
234: LDC 0,10(0)	load constant
* <- constant
235: ST 0,-15(5)	store arg val
* -> call of function: printBackward
236: ST 5,-12(5)	push ofp
237: LDA 5,-12(5)	push frame
238: LDA 0,1(7)	load ac with ret ptr
239: LDA 7,-185(7)	Jump to function location
240: LD 5,0(5)	pop frame
* <- call
241: LD 7,-1(5)	return to caller
101: LDA 7,140(7)	Jump around function: main
* End function: main
242: ST 5,0(5)	push ofp
243: LDA 5,0(5)	push frame
244: LDA 0,1(7)	load ac with ret ptr
245: LDA 7,-144(7)	jump to main location
246: LD 5,0(5)	pop frame
247: HALT 0,0,0	End
