package net.smackem.ylang.lang;

public enum OpCode {
    LD_VAL,     // push value_arg
    LD_LOC,     // push local intArg
    LD_GLB,     // push global intArg
    ST_LOC,     // pop a, store a to local intArg
    ST_GLB,     // pop a, store a to global intArg
    ADD,        // pop b, pop a, push a + b
    SUB,        // pop b, pop a, push a - b
    MUL,        // pop b, pop a, push a * b
    DIV,        // pop b, pop a, push a / b
    MOD,        // pop b, pop a, push a % b
    OR,         // pop b, pop a, push a or b (anything but 0.0 is true)
    AND,        // pop b, pop a, push a and b
    LABEL,      // nop, label intArg
    EQ,         // pop b, a, push a = b
    NEQ,        // pop b, a, push a != b
    GT,         // pop b, a, push a > b
    GE,         // pop b, a, push a >= b
    LT,         // pop b, a, push a < b
    LE,         // pop b, a, push a <= b
    BR,         // branch to label intArg
    BR_ZERO,    // pop a, if 0.0 then branch to intArg
    DUP,        // pop a, push a, push a
    NOT,        // pop a push a != 0
    NEG,        // pop a push -a
    IDX,        // pop b, pop a, push a[b]
    IN,         // pop b, pop a, push a in b
    CMP,        // pop b, pop a, push a compareTo b (< 0, 0, > 0)
    INVOKE,     // pop intArg arguments, call built-in function strArg with popped arguments
}
