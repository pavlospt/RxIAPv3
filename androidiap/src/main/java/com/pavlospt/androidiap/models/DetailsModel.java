package com.pavlospt.androidiap.models;

import com.pavlospt.androidiap.utils.Constants;

import java.util.List;

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
public class DetailsModel {

    private int errorCode = Constants.ERROR_CODE_DEFAULT_VALUE;
    private String errorMessage;
    private SkuDetails skuDetail;
    private List<SkuDetails> skuDetailsList;

    public DetailsModel(DetailsModelBuilder builder) {
        skuDetailsList = builder.skuDetailsList;
        skuDetail = builder.skuDetail;
        errorCode = builder.errorCode;
        errorMessage = builder.errorMessage;
    }

    public boolean isSuccess() {
        return errorCode == Constants.ERROR_CODE_DEFAULT_VALUE;
    }

    public boolean hasError() {
        return errorCode != -1 || errorMessage != null;
    }

    public boolean hasMany() {
        return this.skuDetailsList != null && this.skuDetailsList.size() > 0;
    }

    public boolean hasOne() {
        return this.skuDetail != null && this.skuDetailsList == null;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SkuDetails getSkuDetail() {
        return skuDetail;
    }

    public List<SkuDetails> getSkuDetailsList() {
        return skuDetailsList;
    }

    public static class DetailsModelBuilder {

        private int errorCode;
        private String errorMessage;
        private SkuDetails skuDetail;
        private List<SkuDetails> skuDetailsList;

        public DetailsModelBuilder() {

        }

        public DetailsModelBuilder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public DetailsModelBuilder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public DetailsModelBuilder setSkuDetail(SkuDetails skuDetail){
            this.skuDetail = skuDetail;
            return this;
        }

        public DetailsModelBuilder setSkuDetailsList(List<SkuDetails> skuDetailsList){
            this.skuDetailsList = skuDetailsList;
            return this;
        }

    }

}
