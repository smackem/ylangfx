package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModOperatorTest {
    @Test
    public void numberModNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.MOD.invoke(new NumberVal(17), new NumberVal(10)))
                .isEqualTo(new NumberVal(7));
    }
}