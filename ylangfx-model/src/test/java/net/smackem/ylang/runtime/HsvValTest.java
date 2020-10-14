package net.smackem.ylang.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HsvValTest {
    @Test
    public void convertToRgb() {
        // red
        assertThat(new HsvVal(0, 1, 1).toRgb()).isEqualTo(new RgbVal(255, 0, 0, 255));
        // black
        assertThat(new HsvVal(0, 0, 0).toRgb()).isEqualTo(new RgbVal(0, 0, 0, 255));
        // white
        assertThat(new HsvVal(0, 0, 1).toRgb()).isEqualTo(new RgbVal(255, 255, 255, 255));
        // green
        assertThat(new HsvVal(120, 1, 1).toRgb()).isEqualTo(new RgbVal(0, 255, 0, 255));
        // blue
        assertThat(new HsvVal(240, 1, 1).toRgb()).isEqualTo(new RgbVal(0, 0, 255, 255));
    }

    @Test
    public void convertFromRgb() {
        // red
        assertThat(HsvVal.fromRgb(new RgbVal(255, 0, 0, 255))).isEqualTo(new HsvVal(0, 1, 1));
        // black
        assertThat(HsvVal.fromRgb(new RgbVal(0, 0, 0, 255))).isEqualTo(new HsvVal(0, 0, 0));
        // white
        assertThat(HsvVal.fromRgb(new RgbVal(255, 255, 255, 255))).isEqualTo(new HsvVal(0, 0, 1));
        // green
        assertThat(HsvVal.fromRgb(new RgbVal(0, 255, 0, 255))).isEqualTo(new HsvVal(120, 1, 1));
        // blue
        assertThat(HsvVal.fromRgb(new RgbVal(0, 0, 255, 255))).isEqualTo(new HsvVal(240, 1, 1));
    }
}
