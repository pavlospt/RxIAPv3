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
package com.pavlospt.androidiap.billing;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.pavlospt.androidiap.models.PurchaseDataModel;
import com.pavlospt.androidiap.models.TransactionDetails;

import java.util.ArrayList;
import java.util.List;


public class BillingCache extends BillingBase {

    private static final String TAG = BillingBase.class.getSimpleName();

    private ArrayList<String> productIds;

	public BillingCache(Context context) {
		super(context);
        productIds = new ArrayList<>();
	}

	public boolean includesProduct(String productId) {
        return Hawk.contains(productId) && productIds.contains(productId);
	}

	public PurchaseDataModel getDetails(String productId) {
        Log.e(TAG,"Requested: " + productId + " from cache.");
        return Hawk.get(productId);
	}

	public void put(PurchaseDataModel purchaseDataModel) {
        Log.e(TAG,"Just put: " + purchaseDataModel.getProductId() + " into cache.");
        Hawk.put(purchaseDataModel.getProductId(), purchaseDataModel);
        if(!productIds.contains(purchaseDataModel.getProductId()))
            productIds.add(purchaseDataModel.getProductId());
	}

	public void remove(String productId) {
        if(Hawk.contains(productId))
            Hawk.remove(productId);
        if(productIds.contains(productId))
            productIds.remove(productId);
	}

	public void clear() {
        Hawk.clear();
        productIds = new ArrayList<>();
	}

    public List<PurchaseDataModel> getProducts() {
        List<PurchaseDataModel> toReturn = new ArrayList<>();
        for(String id : productIds){
            toReturn.add((PurchaseDataModel) Hawk.get(id));
        }
        return toReturn;
    }

	public List<String> getProductIds() {
		return productIds;
	}

	@Override
	public String toString() {
		return TextUtils.join(", ", productIds);
	}
}
