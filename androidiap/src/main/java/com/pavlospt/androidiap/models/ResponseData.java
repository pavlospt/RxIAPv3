package com.pavlospt.androidiap.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.pavlospt.androidiap.utils.JsonProperties;

import java.util.Date;

/**
 * Created by PavlosPT13 on 02/12/15.
 */
@JsonObject
public class ResponseData {

    @JsonField(name = JsonProperties.RESPONSE_DATA_ORDER_ID)
    private String orderId;

    @JsonField(name = JsonProperties.RESPONSE_DATA_PACKAGE_NAME)
    private String packageName;

    @JsonField(name = JsonProperties.RESPONSE_DATA_PRODUCT_ID)
    private String productId;

    @JsonField(name = JsonProperties.RESPONSE_DATA_PURCHASE_TIME)
    private long purchaseTime;

    @JsonField(name = JsonProperties.RESPONSE_DATA_PURCHASE_STATE)
    private int responseDataPurchaseState;

    @JsonField(name = JsonProperties.RESPONSE_DATA_DEVELOPER_PAYLOAD)
    private String developerPayload;

    @JsonField(name = JsonProperties.RESPONSE_DATA_PURCHASE_TOKEN)
    private String purchaseToken;

    @JsonField(name = JsonProperties.RESPONSE_DATA_AUTO_RENEWING)
    private boolean autoRenewing;

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getPurchaseDate() {
        return new Date(purchaseTime);
    }

    public PurchaseInfo.PurchaseState getStateOfPurchase() {
        return PurchaseInfo.getPurchaseState(responseDataPurchaseState);
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public int getResponseDataPurchaseState() {
        return responseDataPurchaseState;
    }

    public void setResponseDataPurchaseState(int responseDataPurchaseState) {
        this.responseDataPurchaseState = responseDataPurchaseState;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public boolean isAutoRenewing() {
        return autoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
    }
}
