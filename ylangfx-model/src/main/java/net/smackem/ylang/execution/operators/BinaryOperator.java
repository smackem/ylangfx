package net.smackem.ylang.execution.operators;

public enum BinaryOperator {
    ADD(new AddOperatorImpl()),
    SUB(new SubOperatorImpl()),
    MUL(new MulOperatorImpl()),
    CMP(new CmpOperatorImpl());

    private final BinaryOperatorImpl impl;

    BinaryOperator(BinaryOperatorImpl impl) {
        this.impl = impl;
    }

    BinaryOperatorImpl implementation() {
        return this.impl;
    }
}
