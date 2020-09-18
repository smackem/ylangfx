package net.smackem.ylang.execution;

import net.smackem.ylang.runtime.Value;

import java.util.Objects;

class Stack {
    private final Value[] array = new Value[256];
    private int count = 0;

    public int size() {
        return this.count;
    }

    public Value get(int index) {
        Objects.checkIndex(index, this.count);
        return this.array[index];
    }

    public void set(int index, Value v) {
        Objects.checkIndex(index, this.count);
        this.array[index] = v;
    }

    public void push(Value value) throws StackException {
        if (this.count >= this.array.length) {
            throw new StackException("Stack overflow");
        }
        this.array[this.count] = value;
        this.count++;
    }

    public Value pop() throws StackException {
        if (this.count <= 0) {
            throw new StackException("Stack underflow");
        }
        this.count--;
        return this.array[this.count];
    }
}
