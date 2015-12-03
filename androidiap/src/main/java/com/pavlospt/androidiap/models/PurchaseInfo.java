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

import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.pavlospt.androidiap.utils.JsonProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * With this PurchaseInfo a developer is able verify
 * a purchase from the google play store on his own
 * server.
 */
public class PurchaseInfo {

    private static final String LOG_TAG = "iabv3.purchaseInfo";

    public enum PurchaseState {
        PurchasedSuccessfully, Canceled, Refunded, SubscriptionExpired;
    }

    public final String responseDataString;
    public final String signature;
    public final ResponseData responseData;

    public PurchaseInfo(String responseData, String signature) {
        this.responseDataString = responseData;
        this.signature = signature;
        this.responseData = parseResponseData(responseData);
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public String getSignature() {
        return signature;
    }

    public static PurchaseState getPurchaseState(int state) {
        switch (state) {
            case 0:
                return PurchaseState.PurchasedSuccessfully;
            case 1:
                return PurchaseState.Canceled;
            case 2:
                return PurchaseState.Refunded;
            case 3:
                return PurchaseState.SubscriptionExpired;
            default:
                return PurchaseState.Canceled;
        }
    }

    public ResponseData parseResponseData(String responseDataString) {
        try {
            return LoganSquare.parse(responseDataString,ResponseData.class);
        } catch (IOException e) {
            return null;
        }
    }
}
