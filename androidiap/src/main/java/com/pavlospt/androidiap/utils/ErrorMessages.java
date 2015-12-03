package com.pavlospt.androidiap.utils;

/**
 * Created by PavlosPT13 on 03/12/15.
 */
public class ErrorMessages {

    public static final String PRODUCT_ID_IS_EMPTY
            = "The provided Product ID is empty.";
    public static final String BILLING_PROCESSOR_IS_NOT_INITIALIZED
            = "Billing processor is not initialized.";
    public static final String PRODUCT_FOR_CONSUME_WAS_NOT_FOUND
            = "The Product ID you provided, was not found. As a result it can not be consumed.";
    public static final String BILLING_SERVICE_IS_NULL
            = "Billing service is null. Seems like a problem with Google Play Services.";
    public static final String PRODUCTS_LIST_IS_NULL
            = "The provided list with Product IDs is null.";
    public static final String PRODUCTS_LIST_IS_EMPTY
            = "The provided list with Product IDs is empty.";
    public static final String PURCHASE_TYPE_IS_EMPTY
            = "The provided Product Type is empty.";
    public static final String BILLING_ERROR_INVALID_MERCHANT_ID
            = "Invalid Merchant ID; Request might be tampered.";
    public static final String BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED
            = "You already own this item.";
    public static final String BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE
            = "Something went wrong and the purchase was not initialized correctly.";
    public static final String BUNDLE_NULL_FROM_GOOGLE
            = "The data bundle received from Google for this purchase, was null.";
}
