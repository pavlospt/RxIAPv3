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
package com.pavlospt.androidiap.models;

public class TransactionDetails {

	private PurchaseDataModel purchaseDataModel;

    public TransactionDetails() {
    }

    public TransactionDetails(PurchaseDataModel dataModel) {
        purchaseDataModel = dataModel;

	}

    public TransactionDetails(String id, PurchaseDataModel dataModel) {
        purchaseDataModel = dataModel;
    }

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
