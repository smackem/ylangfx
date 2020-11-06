package net.smackem.ylang.runtime;

import java.util.Objects;

public class IntRect {
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public IntRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int left() {
        return this.left;
    }

    public int top() {
        return this.top;
    }

    public int right() {
        return this.right;
    }

    public int bottom() {
        return this.bottom;
    }

    public boolean contains(int x, int y) {
        return this.left <= x && x < this.right
                && this.top <= y && y < this.bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IntRect intRect = (IntRect) o;
        return left == intRect.left &&
               top == intRect.top &&
               right == intRect.right &&
               bottom == intRect.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, top, right, bottom);
    }

    @Override
    public String toString() {
        return "IntRect{" +
               "left=" + left +
               ", top=" + top +
               ", right=" + right +
               ", bottom=" + bottom +
               '}';
    }
}
