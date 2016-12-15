package com.pavlospt.androidiap.models;

import com.bluelinelabs.logansquare.LoganSquare;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class SkuDetailsJsonParsingTest {

    @Test
    public void shouldBeSameObjectAfterJsonParsing() throws IOException {
        // given
        SkuDetails originalSku = new SkuDetails();

        originalSku.setCurrency("EUR");
        originalSku.setDescription("description");
        originalSku.setPriceText("priceText");
        originalSku.setPriceValueMicros(BigDecimal.valueOf(1230000));
        originalSku.setProductId("productId");
        originalSku.setSubscription("sub");
        originalSku.setTitle("title");

        // when
        String json = LoganSquare.serialize(originalSku);
        SkuDetails outputSku = LoganSquare.parse(json, SkuDetails.class);

        // then
        assertThat(outputSku).isEqualTo(originalSku);
    }
}