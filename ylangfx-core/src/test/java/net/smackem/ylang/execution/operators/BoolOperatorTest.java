package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.StringVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoolOperatorTest {
    @Test
    public void ofBool() throws MissingOverloadException {
        assertThat(UnaryOperator.BOOL.invoke(BoolVal.TRUE))
                .isEqualTo(BoolVal.TRUE);
        assertThat(UnaryOperator.BOOL.invoke(BoolVal.FALSE))
                .isEqualTo(BoolVal.FALSE);
    }

    @Test
    public void ofNumber() throws MissingOverloadException {
        assertThat(UnaryOperator.BOOL.invoke(new NumberVal(0)))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.BOOL.invoke(new NumberVal(1)))
                .isEqualTo(BoolVal.TRUE);
        assertThat(UnaryOperator.BOOL.invoke(new NumberVal(-1)))
                .isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void ofString() throws MissingOverloadException {
        assertThat(UnaryOperator.BOOL.invoke(new StringVal("")))
                .isEqualTo(BoolVal.FALSE);
        assertThat(UnaryOperator.BOOL.invoke(new StringVal("abc")))
                .isEqualTo(BoolVal.TRUE);
    }
}
