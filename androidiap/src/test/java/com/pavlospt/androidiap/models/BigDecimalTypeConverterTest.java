package com.pavlospt.androidiap.models;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalTypeConverterTest {

    private BigDecimalTypeConverter converter = new BigDecimalTypeConverter();

    @Test
    public void shouldCreateBigDecimalFromString() {
        // given
        String stringRepresentation = "123.45";

        // when
        BigDecimal bigDecimal = converter.getFromString(stringRepresentation);

        // then
        assertThat(bigDecimal.unscaledValue()).isEqualTo(BigInteger.valueOf(12345));
        assertThat(bigDecimal.scale()).isEqualTo(2);
    }

    @Test
    public void shouldConvertBigDecimalToString() {
        // given
        BigDecimal bigDecimal = new BigDecimal(BigInteger.valueOf(12345), 2);

        // when
        String stringRepresentation = converter.convertToString(bigDecimal);

        // then
        assertThat(stringRepresentation).isEqualTo("123.45");
    }

}