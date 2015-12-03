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
