package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CmpOperatorTest {
    @Test
    public void compareNumberToNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.CMP.invoke(Context.EMPTY,
                new NumberVal(-100), new NumberVal(100)))
                .isInstanceOf(NumberVal.class)
                .matches(v -> ((NumberVal) v).value() < 0f);
        assertThat(BinaryOperator.CMP.invoke(Context.EMPTY,
                new NumberVal(100), new NumberVal(100)))
                .isInstanceOf(NumberVal.class)
                .matches(v -> ((NumberVal) v).value() == 0f);
        assertThat(BinaryOperator.CMP.invoke(Context.EMPTY,
                new NumberVal(100), new NumberVal(-100)))
                .isInstanceOf(NumberVal.class)
                .matches(v -> ((NumberVal) v).value() > 0f);
    }
}