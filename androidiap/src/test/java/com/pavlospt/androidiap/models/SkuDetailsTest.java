package com.pavlospt.androidiap.models;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class SkuDetailsTest {

    @Test
    public void shouldGetPriceValue() {
        // given
        SkuDetails skuDetails = new SkuDetails() {{
            setPriceValueMicros(BigDecimal.valueOf(1234000));
        }};

        // when
        BigDecimal priceValue = skuDetails.getPriceValue();

        // then
        assertThat(priceValue).isEqualTo(new BigDecimal("1.234"));
    }

}