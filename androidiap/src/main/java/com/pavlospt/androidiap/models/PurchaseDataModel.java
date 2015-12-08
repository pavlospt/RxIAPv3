package com.pavlospt.androidiap.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.pavlospt.androidiap.utils.JsonProperties;

import java.io.Serializable;
import java.util.Date;

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
@JsonObject
public class PurchaseDataModel {

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_PRODUCT_ID)
    private String productId;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_DEVELOPER_PAYLOAD)
    private String developerPayload;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_ORDER_ID)
    private String orderId;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_PACKAGE_NAME)
    private String packageName;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_PURCHASE_STATE)
    private int purchaseState;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_PURCHASE_TOKEN)
    private String purchaseToken;

    @JsonField(name = JsonProperties.PURCHASE_DATA_MODEL_PURCHASE_TIME)
    private long purchaseTimeMillis;

    private String purchaseSignature;
    private String rawResponse;

    public PurchaseDataModel() {
    }

    public PurchaseDataModel(String productId, String developerPayload) {
        this.productId = productId;
        this.developerPayload = developerPayload;
    }

    public Date getPurchaseTime() {
        return new Date(this.purchaseTimeMillis);
    }

    public String getPurchaseSignature() {
        return purchaseSignature;
    }

    public void setPurchaseSignature(String purchaseSignature) {
        this.purchaseSignature = purchaseSignature;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public long getPurchaseTimeMillis() {
        return purchaseTimeMillis;
    }

    public void setPurchaseTimeMillis(long purchaseTimeMillis) {
        this.purchaseTimeMillis = purchaseTimeMillis;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    @Override
    public String toString() {
        return "PurchaseDataModel{" +
                "productId='" + productId + '\'' +
                ", developerPayload='" + developerPayload + '\'' +
                ", orderId='" + orderId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", purchaseState=" + purchaseState +
                ", purchaseToken='" + purchaseToken + '\'' +
                ", purchaseTimeMillis=" + purchaseTimeMillis +
                ", purchaseSignature='" + purchaseSignature + '\'' +
                '}';
    }
}
