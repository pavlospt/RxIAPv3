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
package com.pavlospt.androidiap.utils;

public class Constants {
    public static final int GOOGLE_API_VERSION = 3;

	public static final String BINDING_INTENT_VALUE = "com.android.vending.billing.InAppBillingService.BIND";
	public static final String VENDING_INTENT_PACKAGE = "com.android.vending";

    public static final String PRODUCT_TYPE_MANAGED = "inapp";
    public static final String PRODUCT_TYPE_SUBSCRIPTION = "subs";

    public static final int ERROR_CODE_DEFAULT_VALUE = -1;

	public static final int BILLING_RESPONSE_RESULT_OK = 0; 				//Success
	public static final int BILLING_RESPONSE_RESULT_USER_CANCELED =	1; 		//User pressed back or canceled a dialog
	public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;//Billing API version is not supported for the type requested
	public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4; 	//Requested product is not available for purchase
	public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5; 	//Invalid arguments provided to the API. This error can also indicate that the application was not correctly signed or properly set up for In-app Billing in Google Play, or does not have the necessary permissions in its manifest
	public static final int BILLING_RESPONSE_RESULT_ERROR =	6; 				//Fatal error during the API action
	public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7; //Failure to purchase since item is already owned
	public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8; 	//Failure to consume since item is not owned

	public static final String RESPONSE_CODE = "RESPONSE_CODE";
	public static final String DETAILS_LIST = "DETAILS_LIST";
    public static final String PRODUCTS_LIST = "ITEM_ID_LIST";
	public static final String BUY_INTENT = "BUY_INTENT";
	public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
	public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
	public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";

	public static final int BILLING_ERROR_FAILED_LOAD_PURCHASES = 100;
	public static final int BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE = 101;
	public static final int BILLING_ERROR_INVALID_SIGNATURE = 102;
	public static final int BILLING_ERROR_LOST_CONTEXT = 103;
	public static final int BILLING_ERROR_INVALID_MERCHANT_ID = 104;
    public static final int BILLING_ERROR_PAYLOAD_MISMATCH = 105;
	public static final int BILLING_ERROR_OTHER_ERROR = 110;
	public static final int BILLING_ERROR_CONSUME_FAILED = 111;
	public static final int BILLING_ERROR_SKUDETAILS_FAILED = 112;

	public static final int BILLING_PROCESSOR_NOT_INITIALIZED = 400;
    public static final int PRODUCT_ID_IS_EMPTY = 401;
    public static final int PURCHASE_TYPE_IS_EMPTY = 402;
    public static final int BUNDLE_NULL_FROM_GOOGLE = 403;
    public static final int TRANSACTION_DETAILS_NOT_FOUND = 404;
    public static final int PURCHASE_TOKEN_EMPTY = 405;
    public static final int BILLING_SERVICE_IS_NULL = 406;
    public static final int PRODUCTS_LIST_IS_NULL = 407;
    public static final int PRODUCTS_LIST_IS_EMPTY = 408;
    public static final int NOT_BILLING_PROCESSOR_FLOW = 409;
    public static final int NULL_DATA_PASSED_FROM_INTENT = 410;
	public static final int PRODUCT_FOR_CONSUME_WAS_NOT_FOUND = 411;
}
