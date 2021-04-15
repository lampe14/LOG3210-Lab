// unsigned int fib(unsigned int n){
//    unsigned int i = n - 1, a = 1, b = 0, c = 0, d = 1, t;
//    if (n <= 0)
//      return 0;
//    while (i > 0){
//      if (i % 2 == 1){
//        t = d*(b + a) + c*b;
//        a = d*b + c*a;
//        b = t;
//      }
//      t = d*(2*c + d);
//      c = c*c + d*d;
//      d = t;
//      i = i / 2;
//    }
//    return a + b;
//  }

PRINT "Please enter the number of the fibonacci suite to compute:"
INPUT n

//    if (n <= 0)
//      return 0;
LD R0, n
BGTZ R0, validInput
PRINT #0
BR end

validInput:
//    unsigned int i = n - 1, a = 1, b = 0, c = 0, d = 1, t;
DEC R0
ST i, R0
ST a, #1
ST b, #0
ST c, #0
ST d, #1

//    while (i > 0){
beginWhile:
LD R0, i
BLETZ R0, printResult

//      if (i % 2 == 1){
MOD R0, R0, #2
DEC R0
BNETZ R0, afterIf

CLEAR

//        t = d*(b + a) + c*b;
//        a = d*b + c*a;
//        b = t;

// TODO:: PUT THE BLOCK 1 HERE !
// Step 0
LD R0, b
LD R1, a
ADD R2, R0, R1
// Life_IN  : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5]
// Next_OUT : a:[6], b:[5], c:[6], d:[5], t0:[1]

// Step 1
LD R3, d
MUL R4, R3, t0
// Life_IN  : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5], t0:[1]
// Next_OUT : a:[6], b:[5], c:[6], d:[5], t1:[3]

// Step 2
LD R5, c
MUL R6, R5, b
// Life_IN  : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5], t1:[3]
// Next_OUT : a:[6], b:[5], c:[6], d:[5], t1:[3], t2:[3]

// Step 3
ADD R7, t1, t2
// Life_IN  : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t3, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5], t1:[3], t2:[3]
// Next_OUT : a:[6], b:[5], c:[6], d:[5], t3:[4]

// Step 4
LD #0, #0
null R8, #0, t3
// Life_IN  : [a, b, c, d, i, t, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5], t3:[4]
// Next_OUT : a:[6], b:[5], c:[6], d:[5], t:[9]

// Step 5
MUL R9, d, b
// Life_IN  : [a, b, c, d, i, t, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t4, t5, t6]
// Next_IN  : a:[6], b:[5], c:[6], d:[5], t:[9]
// Next_OUT : a:[6], c:[6], t:[9], t4:[7]

// Step 6
MUL R10, c, a
// Life_IN  : [a, b, c, d, i, t, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t4, t5, t6]
// Next_IN  : a:[6], c:[6], t:[9], t4:[7]
// Next_OUT : t:[9], t4:[7], t5:[7]

// Step 7
ADD R11, t4, t5
// Life_IN  : [a, b, c, d, i, t, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t6]
// Next_IN  : t:[9], t4:[7], t5:[7]
// Next_OUT : t:[9], t6:[8]

// Step 8
LD #0, #0
null R1, #0, t6
// Life_IN  : [a, b, c, d, i, t, t6]
// Life_OUT : [a, b, c, d, i, t]
// Next_IN  : t:[9], t6:[8]
// Next_OUT : t:[9]

// Step 9
LD #0, #0
null R0, #0, t
// Life_IN  : [a, b, c, d, i, t]
// Life_OUT : [a, b, c, d, i]
// Next_IN  : t:[9]
// Next_OUT :


// TODO:: END THE BLOCK 1 HERE ABOVE !

CLEAR

afterIf:
CLEAR

//      t = d*(2*c + d);
//      c = c*c + d*d;
//      d = t;
//      i = i / 2;

// TODO:: PUT THE BLOCK 2 HERE !
// Step 0
LD #2, #2
LD R0, c
MUL R1, #2, R0
// Life_IN  : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Next_IN  : c:[4], d:[5], i:[9]
// Next_OUT : c:[4], d:[5], i:[9], t0:[1]

// Step 1
LD R2, d
ADD R3, t0, R2
// Life_IN  : [a, b, c, d, i, t, t0, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Next_IN  : c:[4], d:[5], i:[9], t0:[1]
// Next_OUT : c:[4], d:[5], i:[9], t1:[2]

// Step 2
MUL R4, d, t1
// Life_IN  : [a, b, c, d, i, t, t1, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t2, t3, t4, t5, t6]
// Next_IN  : c:[4], d:[5], i:[9], t1:[2]
// Next_OUT : c:[4], d:[5], i:[9], t2:[3]

// Step 3
LD #0, #0
null R5, #0, t2
// Life_IN  : [a, b, c, d, i, t, t2, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t3, t4, t5, t6]
// Next_IN  : c:[4], d:[5], i:[9], t2:[3]
// Next_OUT : c:[4], d:[5], i:[9], t:[8]

// Step 4
MUL R6, c, c
// Life_IN  : [a, b, c, d, i, t, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t3, t4, t5, t6]
// Next_IN  : c:[4], d:[5], i:[9], t:[8]
// Next_OUT : d:[5], i:[9], t:[8], t3:[6]

// Step 5
MUL R7, d, d
// Life_IN  : [a, b, c, d, i, t, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t3, t4, t5, t6]
// Next_IN  : d:[5], i:[9], t:[8], t3:[6]
// Next_OUT : i:[9], t:[8], t3:[6], t4:[6]

// Step 6
ADD R8, t3, t4
// Life_IN  : [a, b, c, d, i, t, t3, t4, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t5, t6]
// Next_IN  : i:[9], t:[8], t3:[6], t4:[6]
// Next_OUT : i:[9], t:[8], t5:[7]

// Step 7
LD #0, #0
null R0, #0, t5
// Life_IN  : [a, b, c, d, i, t, t5, t6]
// Life_OUT : [a, b, c, d, i, t, t6]
// Next_IN  : i:[9], t:[8], t5:[7]
// Next_OUT : i:[9], t:[8]

// Step 8
LD #0, #0
null R2, #0, t
// Life_IN  : [a, b, c, d, i, t, t6]
// Life_OUT : [a, b, c, d, i, t6]
// Next_IN  : i:[9], t:[8]
// Next_OUT : i:[9]

// Step 9
LD R9, i
LD #2, #2
DIV R10, R9, #2
// Life_IN  : [a, b, c, d, i, t6]
// Life_OUT : [a, b, c, d, i, t6]
// Next_IN  : i:[9]
// Next_OUT : t6:[10]

// Step 10
LD #0, #0
null R9, #0, t6
// Life_IN  : [a, b, c, d, i, t6]
// Life_OUT : [a, b, c, d, i]
// Next_IN  : t6:[10]
// Next_OUT :


// TODO:: END THE BLOCK 2 HERE ABOVE!




// TODO:: This instruction is just a placeholder to let the code end, remove the code below!
//LD R0, i
//DEC R0
//ST i, R0
// TODO:: Remove the placeholder above of this line!

CLEAR
BR beginWhile

//    return a + b;
printResult:
LD R0, a
LD R1, b
ADD R0, R0, R1
PRINT R0

end:
PRINT "END"