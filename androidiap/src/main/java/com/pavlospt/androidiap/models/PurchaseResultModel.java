package com.pavlospt.androidiap.models;

import com.pavlospt.androidiap.utils.Constants;

/**
 * Created by PavlosPT13 on 02/12/15.
 */
public class PurchaseResultModel {

    private int errorCode = Constants.ERROR_CODE_DEFAULT_VALUE;
    private final TransactionDetails transactionDetails;
    private final String errorMessage, productId;

    public PurchaseResultModel(PurchaseResultModelBuilder builder) {
        errorCode = builder.errorCode;
        errorMessage = builder.errorMessage;
        transactionDetails = builder.transactionDetails;
        productId = builder.productId;
    }

    public boolean isSuccess() {
        return errorCode == Constants.ERROR_CODE_DEFAULT_VALUE ;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public String getProductId() {
        return productId;
    }

    public static class PurchaseResultModelBuilder {

        private TransactionDetails transactionDetails;
        private String productId;
        private Throwable throwable;
        private int errorCode;
        private String errorMessage;

        public PurchaseResultModelBuilder() {

        }

        public PurchaseResultModelBuilder setTransactionDetails(TransactionDetails transactionDetails) {
            this.transactionDetails = transactionDetails;
            return this;
        }

        public PurchaseResultModelBuilder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public PurchaseResultModelBuilder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public PurchaseResultModelBuilder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

    }

}
