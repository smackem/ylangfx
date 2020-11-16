package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.PointVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubOperatorTest {
    @Test
    public void numberMinusNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.SUB.invoke(new NumberVal(100), NumberVal.ONE))
                .isEqualTo(new NumberVal(99));
    }

    @Test
    public void pointMinusNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.SUB.invoke(new PointVal(100, 99), NumberVal.ONE))
                .isEqualTo(new PointVal(99, 98));
    }

    @Test
    public void pointMinusPoint() throws MissingOverloadException {
        assertThat(BinaryOperator.SUB.invoke(new PointVal(100, 99), new PointVal(10, 9)))
                .isEqualTo(new PointVal(90, 90));
    }
}
