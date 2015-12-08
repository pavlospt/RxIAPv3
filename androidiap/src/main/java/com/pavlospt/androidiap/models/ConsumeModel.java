package com.pavlospt.androidiap.models;

import android.support.annotation.Nullable;

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

    private int errorCode = Constants.ERROR_CODE_DEFAULT_VALUE;

    @Nullable
    private final PurchaseDataModel purchaseDataModel;

    @Nullable
    private final Throwable throwable;

    @Nullable
    private String errorMessage;

    public ConsumeModel(ConsumeModelBuilder builder) {
        throwable = builder.throwable;
        errorCode = builder.errorCode;
        errorMessage = builder.errorMessage;
        purchaseDataModel = builder.purchaseDataModel;
    }

    public boolean isSuccess() {
        return errorCode == Constants.ERROR_CODE_DEFAULT_VALUE && throwable == null;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    public PurchaseDataModel getPurchaseDataModel() {
        return purchaseDataModel;
    }

    public static class ConsumeModelBuilder {

        private PurchaseDataModel purchaseDataModel;
        private int errorCode;

        @Nullable
        private Throwable throwable;

        @Nullable
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
