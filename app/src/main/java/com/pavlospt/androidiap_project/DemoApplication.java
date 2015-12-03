package com.pavlospt.androidiap_project;

import android.app.Application;

import com.pavlospt.androidiap.billing.BillingProcessor;

/**
 * Created by PavlosPT13 on 03/12/15.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BillingProcessor.init(this);
    }
}
