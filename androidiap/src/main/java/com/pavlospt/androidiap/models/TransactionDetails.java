/**
 * Copyright 2014 AnjLab
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

import java.util.Date;

//@JsonObject
public class TransactionDetails {

//    @JsonField(name = JsonProperties.RESPONSE_DATA_ORDER_ID)
//    private String productId;
//
//    @JsonField(name = JsonProperties.RESPONSE_DATA_ORDER_ID)
//    private String orderId;
//
//    @JsonField(name = JsonProperties.RESPONSE_DATA_PURCHASE_TOKEN)
//	private String purchaseToken;
//
//    @JsonField(name = JsonProperties.RESPONSE_DATA_PURCHASE_TIME)
//	private long purchaseTime;

	private PurchaseDataModel purchaseDataModel;

    public TransactionDetails() {
    }

    public TransactionDetails(PurchaseDataModel dataModel) {
        purchaseDataModel = dataModel;

	}

    public TransactionDetails(String id, PurchaseDataModel dataModel) {
        purchaseDataModel = dataModel;
    }

//    public Date getPurchaseDate() {
//        return new Date(purchaseTime);
//    }
//
//    public String getProductId() {
//        return productId;
//    }
//
//    public void setProductId(String productId) {
//        this.productId = productId;
//    }
//
//    public String getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }
//
//    public String getPurchaseToken() {
//        return purchaseToken;
//    }
//
//    public void setPurchaseToken(String purchaseToken) {
//        this.purchaseToken = purchaseToken;
//    }
//
//    public long getPurchaseTime() {
//        return purchaseTime;
//    }
//
//    public void setPurchaseTime(long purchaseTime) {
//        this.purchaseTime = purchaseTime;
//    }

    public PurchaseDataModel getPurchaseDataModel() {
        return purchaseDataModel;
    }

    public void setPurchaseDataModel(PurchaseDataModel purchaseDataModel) {
        this.purchaseDataModel = purchaseDataModel;
    }

    @Override
	public String toString() {
		return String.format("%s purchased at %s(%s). Token: %s, Signature: %s",
                purchaseDataModel.getProductId(),
                purchaseDataModel.getPurchaseTime(),
                purchaseDataModel.getOrderId(),
                purchaseDataModel.getPurchaseToken(),
                purchaseDataModel.getPurchaseSignature());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TransactionDetails details = (TransactionDetails) o;

		return !(purchaseDataModel.getOrderId() != null ?
                !purchaseDataModel.getOrderId().equals(details.purchaseDataModel.getOrderId()) :
                details.purchaseDataModel.getOrderId() != null);

	}

	@Override
	public int hashCode() {
		return purchaseDataModel.getOrderId() != null ? purchaseDataModel.getOrderId().hashCode() : 0;
	}
}
