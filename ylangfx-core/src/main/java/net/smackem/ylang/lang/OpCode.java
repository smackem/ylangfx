package net.smackem.ylang.lang;

public enum OpCode {
    LD_VAL,     // push valueArg
    LD_LOC,     // push local intArg
    LD_GLB,     // push global intArg
    LD_ENV,     // push environment[strArg] (e.g. input image)
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
    EQ,         // pop b, a, push a == b
    NEQ,        // pop b, a, push a != b
    GT,         // pop b, a, push a > b
    GE,         // pop b, a, push a >= b
    LT,         // pop b, a, push a < b
    LE,         // pop b, a, push a <= b
    BR,         // branch to intArg
    BR_ZERO,    // pop a, if not a then branch to intArg
    DUP,        // pop a, push a, push a
    NOT,        // pop a, push a != 0
    NEG,        // pop a, push -a
    IDX,        // pop b, pop a, push a[b]
    IN,         // pop b, pop a, push a in b
    CMP,        // pop b, pop a, push a compareTo b (< 0, 0, > 0)
    INVOKE,     // pop intArg arguments, call built-in function strArg with popped arguments
    POP,        // pop a, discard
    ITER,       // pop a, push a.iterator()
    BR_NEXT,    // pop a, push a.next if a.next not null, otherwise branch to intArg
    MK_MAP,     // pop intArg arguments, push map(arguments)
    MK_ENTRY,   // pop b, pop a, push mapEntry(a, b)
    MK_LIST,    // pop intArg arguments, push list(arguments)
    MK_POINT,   // pop b, pop a, push point(a, b)
    MK_RANGE,   // pop upper, pop step, pop lower, push range(lower, upper, step)
    LOG,        // pop intArg arguments, write to log
    CONCAT,     // pop b, pop a, push a :: b
    CALL,       // push pc, branch to intArg, no. of arguments: valueArg (NumberVal)
    RET;        // pop v, pop a, branch to a, push v

    public boolean isBranch() {
        return switch (this) {
            case BR, BR_ZERO, BR_NEXT, CALL, RET -> true;
            default -> false;
        };
    }
}
