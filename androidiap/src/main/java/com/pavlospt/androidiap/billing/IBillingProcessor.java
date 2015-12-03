package com.pavlospt.androidiap.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.pavlospt.androidiap.models.ConsumeModel;
import com.pavlospt.androidiap.models.DetailsModel;
import com.pavlospt.androidiap.models.PurchaseDataModel;
import com.pavlospt.androidiap.models.PurchaseModel;
import com.pavlospt.androidiap.models.SkuDetails;
import com.pavlospt.androidiap.models.TransactionDetails;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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
public interface IBillingProcessor {

    boolean isInitialized();
    boolean isPurchased(String productId);
    boolean isSubscribed(String productId);
    boolean loadOwnedPurchasesFromGoogle();
    boolean purchase(Activity activity, String productId);
    boolean subscribe(Activity activity, String productId);
    boolean consumePurchase(String productId);
    boolean handleActivityResult(int requestCode, int resultCode, Intent data);
    boolean isValid(TransactionDetails transactionDetails);
    List<String> listOwnedProducts();
    List<String> listOwnedSubscriptions();
    List<SkuDetails> getPurchaseListingDetails(ArrayList<String> productIdList);
    List<SkuDetails> getSubscriptionListingDetails(ArrayList<String> productIdList);
    List<PurchaseDataModel> ownedProductsTransactionDetails();
    List<PurchaseDataModel> ownedSubscriptionsTransactionDetails();
    SkuDetails getPurchaseListingDetails(String productId);
    SkuDetails getSubscriptionListingDetails(String productId);
    TransactionDetails getPurchaseTransactionDetails(String productId);
    TransactionDetails getSubscriptionTransactionDetails(String productId);

    /*
    * Rx Methods
    * */
    Observable<Boolean> isInitializedObservable();
    Observable<Boolean> isPurchasedObservable(final String productId);
    Observable<Boolean> isSubscribedObservable(final String productId);
    Observable<List<String>> listOwnedProductsObservable();
    Observable<List<String>> listOwnedSubscriptionsObservable();
    Observable<PurchaseModel> purchaseObservable(Activity activity, String productId);
    Observable<PurchaseModel> subscribeObservable(Activity activity, String productId);
    Observable<ConsumeModel> consumePurchaseObservable(final String productId);
    Observable<SkuDetails> getPurchaseListingDetailsObservable(String productId);
    Observable<SkuDetails> getSubscriptionListingDetailsObservable(String productId);
    Observable<DetailsModel> getPurchaseListingDetailsObservable(ArrayList<String> productIdList);
    Observable<DetailsModel> getSubscriptionListingDetailsObservable(ArrayList<String> productIdList);
    Observable<TransactionDetails> getPurchaseTransactionDetailsObservable(String productId);
    Observable<TransactionDetails> getSubscriptionTransactionDetailsObservable(String productId);
    Observable<List<PurchaseDataModel>> ownedProductsTransactionDetailsObservable();
    Observable<List<PurchaseDataModel>> ownedSubscriptionsTransactionDetailsObservable();

}
