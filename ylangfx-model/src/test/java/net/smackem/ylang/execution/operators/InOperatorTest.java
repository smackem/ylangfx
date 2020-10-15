package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InOperatorTest {
    @Test
    public void pointInRect() throws MissingOverloadException {
        final RectVal rect = new RectVal(0, 0, 20, 20);
        assertThat(BinaryOperator.IN.invoke(new PointVal(10f, 10f), rect))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new PointVal(19, 19), rect))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new PointVal(20, 19), rect))
                .isEqualTo(BoolVal.FALSE);
        assertThat(BinaryOperator.IN.invoke(new PointVal(19, 20), rect))
                .isEqualTo(BoolVal.FALSE);
    }

    @Test
    public void anyInList() throws MissingOverloadException {
        final ListVal list = new ListVal(List.of(
                new NumberVal(100),
                new RectVal(1, 1, 10, 10),
                BoolVal.TRUE));
        assertThat(BinaryOperator.IN.invoke(new NumberVal(100), list))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new RectVal(1, 1, 10, 10), list))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(BoolVal.TRUE, list))
                .isEqualTo(BoolVal.TRUE);

        assertThat(BinaryOperator.IN.invoke(BoolVal.FALSE, list))
                .isEqualTo(BoolVal.FALSE);
        assertThat(BinaryOperator.IN.invoke(new NumberVal(1244), list))
                .isEqualTo(BoolVal.FALSE);
    }

    @Test
    public void numberInKernel() throws MissingOverloadException {
        final KernelVal kernel = new KernelVal(List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(-2),
                new NumberVal(-1)));
        assertThat(BinaryOperator.IN.invoke(new NumberVal(1), kernel))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new NumberVal(-1), kernel))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new NumberVal(2), kernel))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(new NumberVal(-2), kernel))
                .isEqualTo(BoolVal.TRUE);

        assertThat(BinaryOperator.IN.invoke(new NumberVal(123), kernel))
                .isEqualTo(BoolVal.FALSE);
    }
}
