package com.pavlospt.androidiap.models;

import com.pavlospt.androidiap.utils.Constants;

/**
 * Created by PavlosPT13 on 02/12/15.
 */
public class PurchaseModel {

    private final TransactionDetails transactionDetails;
    private final PurchaseDataModel purchaseDataModel;
    private final String productId, errorMessage;
    private final Throwable throwable;
    private int errorCode = Constants.ERROR_CODE_DEFAULT_VALUE;

    public PurchaseModel(PurchaseModelBuilder builder) {
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

    public static class PurchaseModelBuilder {

        private TransactionDetails transactionDetails;
        private PurchaseDataModel purchaseDataModel;
        private String productId, errorMessage;
        private Throwable throwable;
        private int errorCode;

        public PurchaseModelBuilder() {

        }

        public PurchaseModelBuilder setPurchaseDataModel(PurchaseDataModel purchaseDataModel) {
            this.purchaseDataModel = purchaseDataModel;
            return this;
        }

        public PurchaseModelBuilder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public PurchaseModelBuilder setTransactionDetails(TransactionDetails transactionDetails) {
            this.transactionDetails = transactionDetails;
            return this;
        }

        public PurchaseModelBuilder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public PurchaseModelBuilder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public PurchaseModelBuilder setErrorCode(int errorCode, Throwable throwable) {
            this.errorCode = errorCode;
            this.throwable = throwable;
            return this;
        }

    }

}
