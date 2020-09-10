package net.smackem.ylang.execution;

public enum BinaryOperator {
    ADD(new AddOperatorImpl()),
    SUB(new SubOperatorImpl());

    private final BinaryOperatorImpl impl;

    BinaryOperator(BinaryOperatorImpl impl) {
        this.impl = impl;
    }

    BinaryOperatorImpl implementation() {
        return this.impl;
    }
}
