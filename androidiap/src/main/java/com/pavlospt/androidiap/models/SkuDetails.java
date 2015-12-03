/**
 * Copyright 2015 Pavlos-Petros Tournaris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pavlospt.androidiap.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.pavlospt.androidiap.utils.JsonProperties;

@JsonObject
public class SkuDetails {

    @JsonField(name = JsonProperties.SKU_DETAILS_PRODUCT_ID)
	private String productId;

    @JsonField(name = JsonProperties.SKU_DETAILS_TITLE)
    private String title;

    @JsonField(name = JsonProperties.SKU_DETAILS_DESCRIPTION)
    private String description;

    @JsonField(name = JsonProperties.SKU_DETAILS_SUBSCRIPTION)
    private String subscription;

    @JsonField(name = JsonProperties.SKU_DETAILS_PRICE_CURRENCY_CODE)
    private String currency;

    @JsonField(name = JsonProperties.SKU_DETAILS_PRICE_AMOUNT_MICROS)
    private Double priceValue;

    @JsonField(name = JsonProperties.SKU_DETAILS_PRICE)
    private String priceText;

	public SkuDetails() {

	}

    public boolean isSubscription() {
        return this.subscription.equalsIgnoreCase(JsonProperties.SKU_DETAILS_SUBSCRIPTION);
    }

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubscription() {
        return subscription;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getPriceValue() {
        return priceValue / 1000000;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPriceValue(Double priceValue) {
        this.priceValue = priceValue;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }

    @Override
	public String toString() {
		return String.format("%s: %s(%s) %f in %s (%s)", productId, title, description, priceValue, currency, priceText);
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkuDetails that = (SkuDetails) o;

        return isSubscription() == that.isSubscription()
                && !(productId != null ? !productId.equals(that.productId) : that.productId != null);

    }

	@Override
	public int hashCode() {
		int result = productId != null ? productId.hashCode() : 0;
		result = 31 * result + (isSubscription() ? 1 : 0);
		return result;
	}
}
