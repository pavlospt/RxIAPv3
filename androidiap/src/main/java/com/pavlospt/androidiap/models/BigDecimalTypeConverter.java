package com.pavlospt.androidiap.models;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.math.BigDecimal;

class BigDecimalTypeConverter extends StringBasedTypeConverter<BigDecimal> {

    @Override
    public BigDecimal getFromString(String string) {
        return new BigDecimal(string);
    }

    @Override
    public String convertToString(BigDecimal bigDecimal) {
        return bigDecimal.toString();
    }
}
