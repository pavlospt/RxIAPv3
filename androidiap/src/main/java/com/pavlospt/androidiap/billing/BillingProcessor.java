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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


import com.android.vending.billing.IInAppBillingService;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.pavlospt.androidiap.models.ConsumeModel;
import com.pavlospt.androidiap.models.DetailsModel;
import com.pavlospt.androidiap.models.PurchaseDataModel;
import com.pavlospt.androidiap.models.PurchaseInfo;
import com.pavlospt.androidiap.models.PurchaseModel;
import com.pavlospt.androidiap.models.SkuDetails;
import com.pavlospt.androidiap.models.TransactionDetails;
import com.pavlospt.androidiap.utils.Constants;
import com.pavlospt.androidiap.utils.ErrorMessages;
import com.pavlospt.androidiap.utils.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;

public class BillingProcessor extends BillingBase implements IBillingProcessor{

	/**
	 * Callback methods where billing events are reported.
	 * Apps must implement one of these to construct a BillingProcessor.
	 */
	public interface BillingProcessorListener {

		void onProductPurchased(String productId, PurchaseDataModel purchaseDataModel);

		void onPurchaseHistoryRestored();

		void onBillingError(int errorCode, Throwable error);
	}

	private static final Date dateMerchantLimit1 = new Date(2012, 12, 5); //5th December 2012
	private static final Date dateMerchantLimit2 = new Date(2015, 7, 20); //21st July 2015

	private static final int PURCHASE_FLOW_REQUEST_CODE = 2061984;
	private static final String LOG_TAG = "rxiapv3";
	private static final String SETTINGS_VERSION = ".v1";
	private static final String RESTORE_KEY = ".products.restored" + SETTINGS_VERSION;
	private static final String MANAGED_PRODUCTS_CACHE_KEY = ".products.cache" + SETTINGS_VERSION;
	private static final String SUBSCRIPTIONS_CACHE_KEY = ".subscriptions.cache" + SETTINGS_VERSION;
	private static final String PURCHASE_PAYLOAD_CACHE_KEY = ".purchase.last" + SETTINGS_VERSION;

	private IInAppBillingService billingService;
	private String contextPackageName;
	private String signatureBase64;
	private BillingCache cachedProducts;
	private BillingCache cachedSubscriptions;
	private BillingProcessorListener billingProcessorListener;
	private String developerMerchantId;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			billingService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			billingService = IInAppBillingService.Stub.asInterface(service);
			if (!isPurchaseHistoryRestored() && loadOwnedPurchasesFromGoogle()) {
				setPurchaseHistoryRestored();
				if (billingProcessorListener != null)
                    billingProcessorListener.onPurchaseHistoryRestored();
			}
		}
	};

    public static void init(Context context) {
        Hawk.init(context)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.FULL)
                .setCallback(new HawkBuilder.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG," Hawk initiated.");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.e(LOG_TAG,"Hawk failure to initiate:" + e.getMessage());
                    }
                })
                .build();
    }

	public BillingProcessor(Context context, String licenseKey, BillingProcessorListener listener) {
		this(context, licenseKey, null, listener);
	}

	public BillingProcessor(Context context, String licenseKey, String merchantId, BillingProcessorListener listener) {
		super(context);
        Hawk.init(context)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.FULL)
                .setCallback(new HawkBuilder.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG," Hawk initiated.");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.e(LOG_TAG,"Hawk failure to initiate:" + e.getMessage());
                    }
                })
                .build();
		this.signatureBase64 = licenseKey;
        this.contextPackageName = context.getApplicationContext().getPackageName();
        this.cachedProducts = new BillingCache(context);
        this.cachedSubscriptions = new BillingCache(context);
        this.developerMerchantId = merchantId;
        this.billingProcessorListener = listener;
		bindPlayServices();
        loadOwnedPurchasesFromGoogle();
	}

	private void bindPlayServices() {
		try {
			Intent iapIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
			iapIntent.setPackage("com.android.vending");
			getContext().bindService(iapIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}
	}

	@Override
	public void release() {
		if (serviceConnection != null && getContext() != null) {
			try {
				getContext().unbindService(serviceConnection);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
			billingService = null;
		}
		cachedProducts.release();
        cachedSubscriptions.release();
		super.release();
	}

    @Override
	public boolean isInitialized() {
		return billingService != null;
	}

    @Override
    public Observable<Boolean> isInitializedObservable() {
        return Observable.fromCallable(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return billingService != null;
            }
        });
    }

    @Override
    public boolean isPurchased(String productId) {
		return cachedProducts.includesProduct(productId);
	}

    @Override
    public Observable<Boolean> isPurchasedObservable(final String productId) {
        return Observable.fromCallable(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return cachedProducts.includesProduct(productId);
            }
        });
    }

    @Override
	public boolean isSubscribed(String productId) {
		return cachedSubscriptions.includesProduct(productId);
	}

    @Override
    public Observable<Boolean> isSubscribedObservable(final String productId) {
        return Observable.fromCallable(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return cachedSubscriptions.includesProduct(productId);
            }
        });
    }

    @Override
	public List<String> listOwnedProducts() {
		return cachedProducts.getProductIds();
	}

    @Override
    public Observable<List<String>> listOwnedProductsObservable() {
        return Observable.fromCallable(new Func0<List<String>>() {
            @Override
            public List<String> call() {
                return cachedProducts.getProductIds();
            }
        });
    }

    @Override
	public List<String> listOwnedSubscriptions() {
		return cachedSubscriptions.getProductIds();
	}

    @Override
    public Observable<List<String>> listOwnedSubscriptionsObservable() {
        return Observable.fromCallable(new Func0<List<String>>() {
            @Override
            public List<String> call() {
                return cachedSubscriptions.getProductIds();
            }
        });
    }

    @Override
    public List<PurchaseDataModel> ownedProductsTransactionDetails() {
        return cachedProducts.getProducts();
    }

    @Override
    public Observable<List<PurchaseDataModel>> ownedProductsTransactionDetailsObservable() {
        return Observable.fromCallable(new Func0<List<PurchaseDataModel>>() {
            @Override
            public List<PurchaseDataModel> call() {
                return cachedProducts.getProducts();
            }
        });
    }

    @Override
    public List<PurchaseDataModel> ownedSubscriptionsTransactionDetails() {
        return cachedSubscriptions.getProducts();
    }

    @Override
    public Observable<List<PurchaseDataModel>> ownedSubscriptionsTransactionDetailsObservable() {
        return Observable.fromCallable(new Func0<List<PurchaseDataModel>>() {
            @Override
            public List<PurchaseDataModel> call() {
                return cachedSubscriptions.getProducts();
            }
        });
    }

    @Override
	public Observable<PurchaseModel> purchaseObservable(Activity activity, String productId) {
		return purchaseObservable(activity, productId, Constants.PRODUCT_TYPE_MANAGED);
	}

    @Override
	public Observable<PurchaseModel> subscribeObservable(Activity activity, String productId) {
		return purchaseObservable(activity, productId, Constants.PRODUCT_TYPE_SUBSCRIPTION);
	}

    @Override
    public boolean purchase(Activity activity, String productId) {
        return purchase(activity, productId, Constants.PRODUCT_TYPE_MANAGED);
    }

    @Override
    public boolean subscribe(Activity activity, String productId) {
        return purchase(activity, productId, Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    @Override
    public SkuDetails getPurchaseListingDetails(String productId) {
        return getSkuDetails(productId, Constants.PRODUCT_TYPE_MANAGED);
    }

    @Override
    public SkuDetails getSubscriptionListingDetails(String productId) {
        return getSkuDetails(productId, Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    @Override
    public List<SkuDetails> getPurchaseListingDetails(ArrayList<String> productIdList) {
        return getSkuDetails(productIdList, Constants.PRODUCT_TYPE_MANAGED);
    }

    @Override
    public List<SkuDetails> getSubscriptionListingDetails(ArrayList<String> productIdList) {
        return getSkuDetails(productIdList, Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    @Override
    public TransactionDetails getPurchaseTransactionDetails(String productId) {
        return getPurchaseTransactionDetails(productId, cachedProducts);
    }

    @Override
    public TransactionDetails getSubscriptionTransactionDetails(String productId) {
        return getPurchaseTransactionDetails(productId, cachedSubscriptions);
    }

    /*
    * RX Methods
    * */
    @Override
    public Observable<SkuDetails> getPurchaseListingDetailsObservable(String productId) {
        return getSkuDetailsObservable(productId, Constants.PRODUCT_TYPE_MANAGED);
    }

    @Override
    public Observable<SkuDetails> getSubscriptionListingDetailsObservable(String productId) {
        return getSkuDetailsObservable(productId, Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    @Override
    public Observable<DetailsModel> getPurchaseListingDetailsObservable(ArrayList<String> productIdList) {
        return getSkuDetailsObservable(productIdList, Constants.PRODUCT_TYPE_MANAGED);
    }

    @Override
    public Observable<DetailsModel> getSubscriptionListingDetailsObservable(ArrayList<String> productIdList) {
        return getSkuDetailsObservable(productIdList, Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    @Override
    public Observable<TransactionDetails> getPurchaseTransactionDetailsObservable(String productId) {
        return getPurchaseTransactionDetailsObservable(productId, cachedProducts);
    }

    @Override
    public Observable<TransactionDetails> getSubscriptionTransactionDetailsObservable(String productId) {
        return getPurchaseTransactionDetailsObservable(productId, cachedSubscriptions);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PURCHASE_FLOW_REQUEST_CODE)
            return false;
        if (data == null) {
            Log.e(LOG_TAG, "handleActivityResult: data is null!");
            return false;
        }
        int responseCode = data.getIntExtra(Constants.RESPONSE_CODE, Constants.BILLING_RESPONSE_RESULT_OK);
        Log.d(LOG_TAG, String.format("resultCode = %d, responseCode = %d", resultCode, responseCode));
        String purchasePayload = getPurchasePayload();
        if (resultCode == Activity.RESULT_OK
                && responseCode == Constants.BILLING_RESPONSE_RESULT_OK
                && !TextUtils.isEmpty(purchasePayload)) {
            String purchaseData = data.getStringExtra(Constants.INAPP_PURCHASE_DATA);
            String dataSignature = data.getStringExtra(Constants.RESPONSE_INAPP_SIGNATURE);

            try {
                PurchaseDataModel purchaseDataModel = LoganSquare.parse(purchaseData, PurchaseDataModel.class);
                purchaseDataModel.setPurchaseSignature(dataSignature);
                purchaseDataModel.setRawResponse(purchaseData);
                String productId = purchaseDataModel.getProductId();
                String developerPayload = purchaseDataModel.getDeveloperPayload();
                if (developerPayload == null)
                    developerPayload = "";
                boolean purchasedSubscription = purchasePayload.startsWith(Constants.PRODUCT_TYPE_SUBSCRIPTION);
                if (purchasePayload.equals(developerPayload)) {
                    if (verifyPurchaseSignature(productId, purchaseData, dataSignature)) {
                        BillingCache cache = purchasedSubscription ? cachedSubscriptions : cachedProducts;
                        cache.put(purchaseDataModel);
                        if (billingProcessorListener != null)
                            billingProcessorListener.onProductPurchased(productId,
                                    purchaseDataModel);
                    } else {
                        Log.e(LOG_TAG, "Public key signature doesn't match!");
                        if (billingProcessorListener != null)
                            billingProcessorListener.onBillingError(Constants.BILLING_ERROR_INVALID_SIGNATURE, null);
                    }
                } else {
                    Log.e(LOG_TAG, String.format("Payload mismatch: %s != %s", purchasePayload, developerPayload));
                    if (billingProcessorListener != null)
                        billingProcessorListener.onBillingError(Constants.BILLING_ERROR_INVALID_SIGNATURE, null);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                if (billingProcessorListener != null)
                    billingProcessorListener.onBillingError(Constants.BILLING_ERROR_OTHER_ERROR, e);
            }
        } else {
            if (billingProcessorListener != null)
                billingProcessorListener.onBillingError(responseCode, null);
        }
        return true;
    }

    @Override
    public Observable<ConsumeModel> consumePurchaseObservable(final String productId) {
        return Observable.create(new Observable.OnSubscribe<ConsumeModel>() {
            @Override
            public void call(Subscriber<? super ConsumeModel> subscriber) {

                loadManagedProductsFromGoogle();

                ConsumeModel.ConsumeModelBuilder consumeModelBuilder =
                        new ConsumeModel.ConsumeModelBuilder();

                if(!isInitialized()){
                    consumeModelBuilder.setErrorCode(Constants.BILLING_PROCESSOR_NOT_INITIALIZED);
                    consumeModelBuilder.setErrorMessage(ErrorMessages.BILLING_PROCESSOR_IS_NOT_INITIALIZED);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new ConsumeModel(consumeModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                if(TextUtils.isEmpty(productId)){
                    consumeModelBuilder.setErrorCode(Constants.PRODUCT_ID_IS_EMPTY);
                    consumeModelBuilder.setErrorMessage(ErrorMessages.PRODUCT_ID_IS_EMPTY);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new ConsumeModel(consumeModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                try {

                    for(String s : cachedProducts.getProductIds()){
                        Log.e(LOG_TAG,"ProductId:" + s);
                    }

                    Log.e(LOG_TAG,"" + cachedProducts.includesProduct(productId));

                    PurchaseDataModel transactionDetails = cachedProducts.getDetails(productId);

                    Log.e(LOG_TAG,"Transaction details is:" + (transactionDetails == null ? "null" : "not null"));

                    if (transactionDetails != null &&
                            !TextUtils.isEmpty(transactionDetails.getPurchaseToken())) {

                        int response = billingService
                                .consumePurchase(
                                        Constants.GOOGLE_API_VERSION,
                                        contextPackageName,
                                        transactionDetails.getPurchaseToken());
                        Log.e(LOG_TAG,"Consume response code:" + response);
                        if (response == Constants.BILLING_RESPONSE_RESULT_OK) {
                            cachedProducts.remove(productId);
                            consumeModelBuilder.setPurchaseDataModel(transactionDetails);
                            consumeModelBuilder.setProductId(productId);
                            if(!subscriber.isUnsubscribed()){
                                subscriber.onNext(new ConsumeModel(consumeModelBuilder));
                                subscriber.onCompleted();
                            }
                            Log.e(LOG_TAG, "Successfully consumed " + productId + " purchase.");
                        } else {
                            consumeModelBuilder.setErrorCode(response);
                            if(!subscriber.isUnsubscribed()){
                                subscriber.onNext(new ConsumeModel(consumeModelBuilder));
                                subscriber.onCompleted();
                            }
                            Log.e(LOG_TAG, String.format("Failed to consume %s: error %d", productId, response));
                        }
                    }else{
                        if(transactionDetails != null && TextUtils.isEmpty(transactionDetails.getPurchaseToken())){
                            consumeModelBuilder.setErrorCode(Constants.PRODUCT_FOR_CONSUME_WAS_NOT_FOUND);
                            consumeModelBuilder.setErrorMessage(ErrorMessages.PRODUCT_FOR_CONSUME_WAS_NOT_FOUND);
                            if(!subscriber.isUnsubscribed()){
                                subscriber.onNext(new ConsumeModel(consumeModelBuilder));
                                subscriber.onCompleted();
                            }
                        }
                    }
                    if(!subscriber.isUnsubscribed())
                        subscriber.onCompleted();
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, e.toString());
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
	public boolean consumePurchase(String productId) {
		if (!isInitialized())
			return false;
		try {
			PurchaseDataModel transactionDetails = cachedProducts.getDetails(productId);
			if (transactionDetails != null && !TextUtils.isEmpty(transactionDetails.getPurchaseToken())) {
				int response = billingService
						.consumePurchase(
                                Constants.GOOGLE_API_VERSION,
                                contextPackageName,
                                transactionDetails.getPurchaseToken());
				if (response == Constants.BILLING_RESPONSE_RESULT_OK) {
					cachedProducts.remove(productId);
					Log.d(LOG_TAG, "Successfully consumed " + productId + " purchase.");
					return true;
				} else {
					if (billingProcessorListener != null)
                        billingProcessorListener.onBillingError(response, null);
					Log.e(LOG_TAG, String.format("Failed to consume %s: error %d", productId, response));
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
			if (billingProcessorListener != null)
                billingProcessorListener.onBillingError(Constants.BILLING_ERROR_CONSUME_FAILED, e);
		}
		return false;
	}

    @Override
    public boolean isValid(TransactionDetails transactionDetails) {
        boolean verified = verifyPurchaseSignature(transactionDetails.getPurchaseDataModel().getProductId(),
                transactionDetails.getPurchaseDataModel().getRawResponse(),
                transactionDetails.getPurchaseDataModel().getPurchaseSignature());
        boolean checked = checkMerchantTransactionDetails(transactionDetails);
        return verified && checked;
    }

    public boolean loadOwnedPurchasesFromGoogle() {
        return isInitialized() &&
                loadPurchasesByType(Constants.PRODUCT_TYPE_MANAGED) &&
                loadPurchasesByType(Constants.PRODUCT_TYPE_SUBSCRIPTION);
    }

    public void loadManagedProductsFromGoogle() {
        if(isInitialized()){
            loadPurchasesByType(Constants.PRODUCT_TYPE_MANAGED);
        }
    }

    public void loadSubscriptionsFromGoogle() {
        if(isInitialized()){
            loadPurchasesByType(Constants.PRODUCT_TYPE_SUBSCRIPTION);
        }
    }

    public void setPurchaseHistoryRestored() {
        saveBoolean(getPreferencesBaseKey() + RESTORE_KEY, true);
    }

    public static boolean isIabServiceAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        List<ResolveInfo> list = packageManager.queryIntentServices(intent, 0);
        return list.size() > 0;
    }

	private SkuDetails getSkuDetails(String productId, String purchaseType) {
        ArrayList<String> productIdList = new ArrayList<>();
        productIdList.add(productId);
        List<SkuDetails> skuDetailsList =
                getSkuDetails(productIdList, purchaseType);
        if (skuDetailsList != null && skuDetailsList.size() > 0)
            return skuDetailsList.get(0);
        return null;
    }

    private Observable<SkuDetails> getSkuDetailsObservable(String productId, String purchaseType) {
        ArrayList<String> productIdList = new ArrayList<>();
        productIdList.add(productId);
        return getSkuDetailsObservable(productIdList, purchaseType)
                .filter(new Func1<DetailsModel, Boolean>() {
                    @Override
                    public Boolean call(DetailsModel detailsModel) {
                        return detailsModel.isSuccess();
                    }
                })
                .flatMap(new Func1<DetailsModel, Observable<SkuDetails>>() {
                    @Override
                    public Observable<SkuDetails> call(DetailsModel detailsModel) {
                        return Observable.just(detailsModel.getSkuDetailsList().get(0));
                    }
                });
    }

    private Observable<DetailsModel> getSkuDetailsObservable(final ArrayList<String> productIdList, final String purchaseType){
        return Observable.create(new Observable.OnSubscribe<DetailsModel>() {
            @Override
            public void call(Subscriber<? super DetailsModel> subscriber) {

                DetailsModel.DetailsModelBuilder detailsModelBuilder
                        = new DetailsModel.DetailsModelBuilder();

                if(billingService == null){
                    detailsModelBuilder.setErrorCode(Constants.BILLING_SERVICE_IS_NULL);
                    detailsModelBuilder.setErrorMessage(ErrorMessages.BILLING_SERVICE_IS_NULL);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new DetailsModel(detailsModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                if(productIdList == null){
                    detailsModelBuilder.setErrorCode(Constants.PRODUCTS_LIST_IS_NULL);
                    detailsModelBuilder.setErrorMessage(ErrorMessages.PRODUCTS_LIST_IS_NULL);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new DetailsModel(detailsModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                if (productIdList != null && productIdList.size() == 0) {
                    detailsModelBuilder.setErrorCode(Constants.PRODUCTS_LIST_IS_EMPTY);
                    detailsModelBuilder.setErrorMessage(ErrorMessages.PRODUCTS_LIST_IS_EMPTY);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new DetailsModel(detailsModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                try {
                    Bundle products = new Bundle();
                    products.putStringArrayList(Constants.PRODUCTS_LIST, productIdList);
                    Bundle skuDetails =
                            billingService.getSkuDetails(Constants.GOOGLE_API_VERSION, contextPackageName, purchaseType, products);
                    int response = skuDetails.getInt(Constants.RESPONSE_CODE);

                    if (response == Constants.BILLING_RESPONSE_RESULT_OK) {
                        ArrayList<SkuDetails> productDetails = new ArrayList<>();
                        List<String> detailsList = skuDetails.getStringArrayList(Constants.DETAILS_LIST);
                        if (detailsList != null)
                            for (String responseLine : detailsList) {
                                SkuDetails product = LoganSquare.parse(responseLine, SkuDetails.class);
                                productDetails.add(product);
                            }

                        detailsModelBuilder.setSkuDetailsList(productDetails);
                    } else {
                        detailsModelBuilder.setErrorCode(response);
                    }
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new DetailsModel(detailsModelBuilder));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, String.format("Failed to call getSkuDetails %s", e.toString()));
                    if(subscriber.isUnsubscribed()){
                        subscriber.onError(e);
                    }
                }

            }
        });
    }

	private List<SkuDetails> getSkuDetails(ArrayList<String> productIdList, String purchaseType) {
		if (billingService != null && productIdList != null && productIdList.size() > 0) {
			try {
				Bundle products = new Bundle();
				products.putStringArrayList(Constants.PRODUCTS_LIST, productIdList);
				Bundle skuDetails =
                        billingService.getSkuDetails(Constants.GOOGLE_API_VERSION, contextPackageName, purchaseType, products);
				int response = skuDetails.getInt(Constants.RESPONSE_CODE);

				if (response == Constants.BILLING_RESPONSE_RESULT_OK) {
					ArrayList<SkuDetails> productDetails = new ArrayList<>();
					List<String> detailsList = skuDetails.getStringArrayList(Constants.DETAILS_LIST);
					if (detailsList != null)
						for (String responseLine : detailsList) {
							SkuDetails product = LoganSquare.parse(responseLine, SkuDetails.class);
							productDetails.add(product);
						}
					return productDetails;

				} else {
					if (billingProcessorListener != null)
                        billingProcessorListener.onBillingError(response, null);
					Log.e(LOG_TAG, String.format("Failed to retrieve info for %d products, %d", productIdList.size(), response));
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, String.format("Failed to call getSkuDetails %s", e.toString()));
				if (billingProcessorListener != null)
                    billingProcessorListener.onBillingError(Constants.BILLING_ERROR_SKUDETAILS_FAILED, e);
			}
		}
		return null;
	}

	private boolean verifyPurchaseSignature(String productId, String purchaseData, String dataSignature) {
		try {
			/*
             * Skip the signature check if the provided License Key is NULL and return true in order to
             * continue the purchase flow
             */
			return TextUtils.isEmpty(signatureBase64) || Security.verifyPurchase(productId, signatureBase64, purchaseData, dataSignature);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isPurchaseHistoryRestored() {
		return loadBoolean(getPreferencesBaseKey() + RESTORE_KEY, false);
	}

	private void savePurchasePayload(String value) {
		saveString(getPreferencesBaseKey() + PURCHASE_PAYLOAD_CACHE_KEY, value);
	}

	private String getPurchasePayload() {
		return loadString(getPreferencesBaseKey() + PURCHASE_PAYLOAD_CACHE_KEY, null);
	}

    private boolean purchase(Activity activity, String productId, String purchaseType) {
        if (!isInitialized() || TextUtils.isEmpty(productId) || TextUtils.isEmpty(purchaseType))
            return false;
        try {
            String purchasePayload = purchaseType + ":" + UUID.randomUUID().toString();
            savePurchasePayload(purchasePayload);
            Bundle bundle = billingService.getBuyIntent(Constants.GOOGLE_API_VERSION, contextPackageName, productId, purchaseType, purchasePayload);
            if (bundle != null) {
                int response = bundle.getInt(Constants.RESPONSE_CODE);
                if (response == Constants.BILLING_RESPONSE_RESULT_OK) {
                    PendingIntent pendingIntent = bundle.getParcelable(Constants.BUY_INTENT);
                    if (activity != null && pendingIntent != null)
                        activity.startIntentSenderForResult(pendingIntent.getIntentSender(), PURCHASE_FLOW_REQUEST_CODE, new Intent(), 0, 0, 0);
                    else if (billingProcessorListener != null)
                        billingProcessorListener.onBillingError(Constants.BILLING_ERROR_LOST_CONTEXT, null);
                } else if (response == Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    if (!isPurchased(productId) && !isSubscribed(productId))
                        loadOwnedPurchasesFromGoogle();
                    PurchaseDataModel details = cachedProducts.getDetails(productId);
                    if (!checkMerchant(details)) {
                        Log.i(LOG_TAG, "Invalid or tampered merchant id!");
                        if (billingProcessorListener != null)
                            billingProcessorListener.onBillingError(Constants.BILLING_ERROR_INVALID_MERCHANT_ID, null);
                        return false;
                    }
                    if (billingProcessorListener != null) {
                        if (details == null)
                            details = cachedSubscriptions.getDetails(productId);
                        billingProcessorListener.onProductPurchased(productId, details);
                    }
                } else if (billingProcessorListener != null)
                    billingProcessorListener.onBillingError(Constants.BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE, null);
            }
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            if (billingProcessorListener != null)
                billingProcessorListener.onBillingError(Constants.BILLING_ERROR_OTHER_ERROR, e);
        }
        return false;
    }

    private Observable<PurchaseModel> purchaseObservable(final Activity activity, final String productId, final String purchaseType){

        return Observable.create(new Observable.OnSubscribe<PurchaseModel>(){

            @Override
            public void call(Subscriber<? super PurchaseModel> subscriber) {

                PurchaseModel.PurchaseModelBuilder purchaseModelBuilder
                        = new PurchaseModel.PurchaseModelBuilder();

                if(!isInitialized() || TextUtils.isEmpty(productId) || TextUtils.isEmpty(purchaseType)){

                    if(!isInitialized()){
                        //Billing processor has not been initialized
                        purchaseModelBuilder.setErrorCode(Constants.BILLING_PROCESSOR_NOT_INITIALIZED);
                        purchaseModelBuilder.setErrorMessage(ErrorMessages.BILLING_PROCESSOR_IS_NOT_INITIALIZED);
                    }else if(TextUtils.isEmpty(productId)){
                        //Product Id provided is empty
                        purchaseModelBuilder.setErrorCode(Constants.PRODUCT_ID_IS_EMPTY);
                        purchaseModelBuilder.setErrorMessage(ErrorMessages.PRODUCT_ID_IS_EMPTY);
                    }else if(TextUtils.isEmpty(purchaseType)){
                        //PurchaseType provided is empty
                        purchaseModelBuilder.setErrorCode(Constants.PURCHASE_TYPE_IS_EMPTY);
                        purchaseModelBuilder.setErrorMessage(ErrorMessages.PURCHASE_TYPE_IS_EMPTY);
                    }

                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                        subscriber.onCompleted();
                    }
                }

                String purchasePayload = purchaseType + ":" + UUID.randomUUID();

                savePurchasePayload(purchasePayload);

                try {
                    Bundle bundle =
                            billingService.getBuyIntent(Constants.GOOGLE_API_VERSION, contextPackageName,
                                    productId, purchaseType, purchasePayload);

                    if (bundle != null) {

                        int response = bundle.getInt(Constants.RESPONSE_CODE);

                        if (response == Constants.BILLING_RESPONSE_RESULT_OK) {

                            PendingIntent pendingIntent = bundle.getParcelable(Constants.BUY_INTENT);

                            if (activity != null && pendingIntent != null)
                                activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                                        PURCHASE_FLOW_REQUEST_CODE, new Intent(), 0, 0, 0);
                            else if(!subscriber.isUnsubscribed()){
                                //We lost context so we inform the subscriber to complete with an error
                                purchaseModelBuilder.setErrorCode(Constants.BILLING_ERROR_LOST_CONTEXT);
                                subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                                subscriber.onCompleted();
                            }

                        } else if (response == Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {

                            if (!isPurchased(productId) && !isSubscribed(productId)){
                                if(purchaseType.equalsIgnoreCase(Constants.PRODUCT_TYPE_MANAGED))
                                    loadManagedProductsFromGoogle();
                                else
                                    loadSubscriptionsFromGoogle();
                            }

                            PurchaseDataModel details = cachedProducts.getDetails(productId);

                            if (!checkMerchant(details)) {
                                Log.e(LOG_TAG, "Invalid or tampered merchant id!");
                                if (!subscriber.isUnsubscribed()){

                                    //Purchase request might be tampered. We inform the subscriber about it.
                                    purchaseModelBuilder.setErrorCode(Constants.BILLING_ERROR_INVALID_MERCHANT_ID);
                                    purchaseModelBuilder.setErrorMessage(ErrorMessages.BILLING_ERROR_INVALID_MERCHANT_ID);
                                    subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                                    subscriber.onCompleted();
                                }
                            }
                            if (!subscriber.isUnsubscribed()) {

                                if (details == null)
                                    details = cachedSubscriptions.getDetails(productId);

                                //Item was purchase successfully. We return the transaction details
                                //and productId back to the subscriber
                                purchaseModelBuilder.setProductId(productId);
                                purchaseModelBuilder.setPurchaseDataModel(details);
                                purchaseModelBuilder.setErrorCode(Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED);
                                purchaseModelBuilder.setErrorMessage(ErrorMessages.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED);
                                subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                                subscriber.onCompleted();
                            }

                        } else if (!subscriber.isUnsubscribed()){

                            //We failed to initialize the purchase. We inform the subscriber about it.
                            purchaseModelBuilder.setErrorCode(Constants.BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE);
                            purchaseModelBuilder.setErrorMessage(ErrorMessages.BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE);
                            subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                            subscriber.onCompleted();
                        }

                    }else{
                        //Something went wrong when receiving purchase info from Google
                        purchaseModelBuilder.setErrorCode(Constants.BUNDLE_NULL_FROM_GOOGLE);
                        purchaseModelBuilder.setErrorMessage(ErrorMessages.BUNDLE_NULL_FROM_GOOGLE);
                        subscriber.onNext(new PurchaseModel(purchaseModelBuilder));
                        subscriber.onCompleted();
                    }
                }catch(RemoteException | IntentSender.SendIntentException e){
                    e.printStackTrace();
                    //Something got totally fcked up.
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    private boolean loadPurchasesByType(String type) {
        if (!isInitialized())
            return false;
        try {
            Bundle bundle = billingService.getPurchases(Constants.GOOGLE_API_VERSION, contextPackageName, type, null);
            if (bundle.getInt(Constants.RESPONSE_CODE) == Constants.BILLING_RESPONSE_RESULT_OK) {

                if(type.equalsIgnoreCase(Constants.PRODUCT_TYPE_MANAGED))
                    cachedProducts.clear();
                else
                    cachedSubscriptions.clear();

                ArrayList<String> purchaseList = bundle.getStringArrayList(Constants.INAPP_PURCHASE_DATA_LIST);
                ArrayList<String> signatureList = bundle.getStringArrayList(Constants.INAPP_DATA_SIGNATURE_LIST);

                if (purchaseList != null)
                    for (int i = 0; i < purchaseList.size(); i++) {
                        String jsonData = purchaseList.get(i);
                        PurchaseDataModel purchaseDataModel = LoganSquare.parse(jsonData, PurchaseDataModel.class);
                        String signature = signatureList != null && signatureList.size() > i ? signatureList.get(i) : null;
                        purchaseDataModel.setPurchaseSignature(signature);
                        purchaseDataModel.setRawResponse(jsonData);
                        if(type.equalsIgnoreCase(Constants.PRODUCT_TYPE_MANAGED)){
                            cachedProducts.put(purchaseDataModel);
                        } else {
                            cachedSubscriptions.put(purchaseDataModel);
                        }
                    }
            }
            return true;
        } catch (Exception e) {
            if (billingProcessorListener != null)
                billingProcessorListener.onBillingError(Constants.BILLING_ERROR_FAILED_LOAD_PURCHASES, e);
            Log.e(LOG_TAG, e.toString());
        }
        return false;
    }

    /**
     * Checks merchant's id validity. If purchase was generated by Freedom alike program it doesn't know
     * real merchant id, unless publisher GoogleId was hacked
     * If merchantId was not supplied function checks nothing
     *
     * @param details TransactionDetails
     * @return boolean
     */
    private boolean checkMerchant(PurchaseDataModel details) {
        if (developerMerchantId == null) //omit merchant id checking
            return true;
        if (details.getPurchaseTime().before(dateMerchantLimit1)) //new format [merchantId].[orderId] applied or not?
            return true;
        if (details.getPurchaseTime().after(dateMerchantLimit2)) //newest format applied
            return true;
        if (details.getOrderId() == null || details.getOrderId() .trim().length() == 0)
            return false;
        int index = details.getOrderId() .indexOf('.');
        if (index <= 0)
            return false; //protect on missing merchant id
        //extract merchant id
        String merchantId = details.getOrderId().substring(0, index);
        return merchantId.compareTo(developerMerchantId) == 0;
    }

    private boolean checkMerchantTransactionDetails(TransactionDetails details) {
        if (developerMerchantId == null) //omit merchant id checking
            return true;
        if (details.getPurchaseDataModel().getPurchaseTime().before(dateMerchantLimit1)) //new format [merchantId].[orderId] applied or not?
            return true;
        if (details.getPurchaseDataModel().getPurchaseTime().after(dateMerchantLimit2)) //newest format applied
            return true;
        if (details.getPurchaseDataModel().getOrderId() == null || details.getPurchaseDataModel().getOrderId() .trim().length() == 0)
            return false;
        int index = details.getPurchaseDataModel().getOrderId() .indexOf('.');
        if (index <= 0)
            return false; //protect on missing merchant id
        //extract merchant id
        String merchantId = details.getPurchaseDataModel().getOrderId().substring(0, index);
        return merchantId.compareTo(developerMerchantId) == 0;
    }

    private Observable<TransactionDetails> getPurchaseTransactionDetailsObservable(String productId, BillingCache cache) {
        PurchaseDataModel details = cache.getDetails(productId);
        if (details != null && !TextUtils.isEmpty(details.getRawResponse())) {
            return Observable.just(new TransactionDetails(details));
        }
        return null;
    }

    private TransactionDetails getPurchaseTransactionDetails(String productId, BillingCache cache) {
        PurchaseDataModel details = cache.getDetails(productId);
        if (details != null && !TextUtils.isEmpty(details.getRawResponse())) {
            return new TransactionDetails(details);
        }
        return null;
    }
}
