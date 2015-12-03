package com.pavlospt.androidiap.models;

import com.pavlospt.androidiap.utils.Constants;

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
