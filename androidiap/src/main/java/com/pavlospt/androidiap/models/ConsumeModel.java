package com.pavlospt.androidiap.models;

import com.pavlospt.androidiap.utils.Constants;

/**
 * Created by PavlosPT13 on 02/12/15.
 */
public class ConsumeModel {

    private final TransactionDetails transactionDetails;
    private final PurchaseDataModel purchaseDataModel;
    private final String productId;
    private final Throwable throwable;
    private int errorCode = Constants.ERROR_CODE_DEFAULT_VALUE;
    private String errorMessage;

    public ConsumeModel(ConsumeModelBuilder builder) {
        transactionDetails = builder.transactionDetails;
        productId = builder.productId;
        throwable = builder.throwable;
        errorCode = builder.errorCode;
        errorMessage = builder.errorMessage;
        purchaseDataModel = builder.purchaseDataModel;
    }

    public boolean isSuccess() {
        return errorCode == Constants.ERROR_CODE_DEFAULT_VALUE && throwable == null;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public String getProductId() {
        return productId;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class ConsumeModelBuilder {

        private TransactionDetails transactionDetails;
        private PurchaseDataModel purchaseDataModel;
        private String productId;
        private Throwable throwable;
        private int errorCode;
        private String errorMessage;

        public ConsumeModelBuilder() {

        }

        public ConsumeModelBuilder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public ConsumeModelBuilder setPurchaseDataModel(PurchaseDataModel purchaseDataModel) {
            this.purchaseDataModel = purchaseDataModel;
            return this;
        }

        public ConsumeModelBuilder setTransactionDetails(TransactionDetails transactionDetails) {
            this.transactionDetails = transactionDetails;
            return this;
        }

        public ConsumeModelBuilder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public ConsumeModelBuilder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ConsumeModelBuilder setErrorCode(int errorCode, Throwable throwable) {
            this.errorCode = errorCode;
            this.throwable = throwable;
            return this;
        }

    }

}
