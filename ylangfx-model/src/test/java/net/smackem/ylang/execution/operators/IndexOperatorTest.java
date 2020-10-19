package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IndexOperatorTest {
    @Test
    public void listAtNumber() throws MissingOverloadException {
        final ListVal list = new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(200),
                new NumberVal(300)));
        assertThat(BinaryOperator.INDEX.invoke(list, new NumberVal(0)))
                .isEqualTo(new NumberVal(100));
        assertThat(BinaryOperator.INDEX.invoke(list, new NumberVal(0.5f)))
                .isEqualTo(new NumberVal(100));
        assertThat(BinaryOperator.INDEX.invoke(list, new NumberVal(1.3f)))
                .isEqualTo(new NumberVal(200));
        assertThat(BinaryOperator.INDEX.invoke(list, new NumberVal(2.3f)))
                .isEqualTo(new NumberVal(300));
        assertThatThrownBy(() -> BinaryOperator.INDEX.invoke(list, new NumberVal(3.3f)))
                .isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> BinaryOperator.INDEX.invoke(list, new NumberVal(-1.1f)))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void kernelAtNumber() throws MissingOverloadException {
        final KernelVal kernel = new KernelVal(List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(-2),
                new NumberVal(-1)));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new NumberVal(0)))
                .isEqualTo(new NumberVal(1));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new NumberVal(1)))
                .isEqualTo(new NumberVal(2));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new NumberVal(2)))
                .isEqualTo(new NumberVal(-2));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new NumberVal(3)))
                .isEqualTo(new NumberVal(-1));
        assertThatThrownBy(() -> BinaryOperator.INDEX.invoke(kernel, new NumberVal(5.3f)))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void kernelAtPoint() throws MissingOverloadException {
        final KernelVal kernel = new KernelVal(List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(-2),
                new NumberVal(-1)));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new PointVal(0, 0)))
                .isEqualTo(new NumberVal(1));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new PointVal(1, 0)))
                .isEqualTo(new NumberVal(2));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new PointVal(0, 1)))
                .isEqualTo(new NumberVal(-2));
        assertThat(BinaryOperator.INDEX.invoke(kernel, new PointVal(1, 1)))
                .isEqualTo(new NumberVal(-1));
        assertThatThrownBy(() -> BinaryOperator.INDEX.invoke(kernel, new PointVal(2, 0)))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void listAtRange() throws MissingOverloadException {
        final ListVal list = new ListVal(List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(3),
                new NumberVal(4),
                new NumberVal(5),
                new NumberVal(6)));
        assertThat(BinaryOperator.INDEX.invoke(list, new RangeVal(0, 1, 1)))
                .isEqualTo(new ListVal(List.of(
                        new NumberVal(1))));
        assertThat(BinaryOperator.INDEX.invoke(list, new RangeVal(0, 3, 1)))
                .isEqualTo(new ListVal(List.of(
                        new NumberVal(1),
                        new NumberVal(2),
                        new NumberVal(3))));
        assertThat(BinaryOperator.INDEX.invoke(list, new RangeVal(0, 6, 2)))
                .isEqualTo(new ListVal(List.of(
                        new NumberVal(1),
                        new NumberVal(3),
                        new NumberVal(5))));
        assertThat(BinaryOperator.INDEX.invoke(list, new RangeVal(5, 0, -2)))
                .isEqualTo(new ListVal(List.of(
                        new NumberVal(6),
                        new NumberVal(4),
                        new NumberVal(2))));
    }
}