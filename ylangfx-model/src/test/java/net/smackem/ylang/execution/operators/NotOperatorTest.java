package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.StringVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotOperatorTest {
    @Test
    public void notNumber() throws MissingOverloadException {
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, NumberVal.ONE))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, NumberVal.MINUS_ONE))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, NumberVal.ZERO))
                .isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void notBool() throws MissingOverloadException {
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, BoolVal.TRUE))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, BoolVal.FALSE))
                .isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void notString() throws MissingOverloadException {
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, new StringVal("abc")))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.NOT.invoke(Context.EMPTY, StringVal.EMPTY))
                .isEqualTo(BoolVal.TRUE);
    }
}
