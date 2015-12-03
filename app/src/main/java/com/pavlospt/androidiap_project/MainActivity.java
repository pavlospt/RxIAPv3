package com.pavlospt.androidiap_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pavlospt.androidiap.billing.BillingProcessor;
import com.pavlospt.androidiap.models.ConsumeModel;
import com.pavlospt.androidiap.models.PurchaseDataModel;
import com.pavlospt.androidiap.models.PurchaseModel;
import com.pavlospt.androidiap.models.PurchaseResultModel;
import com.pavlospt.androidiap.models.TransactionDetails;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements BillingProcessor.BillingProcessorListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private Subscription billingSubscription;
    private BillingProcessor billingProcessor;

    private final String SUB_PRODUCT_ID = "rxiapv3.testproduct.sub1";
    private final String CONSUMABLE_PRODUCT_ID = "rxiapv3.testproduct1";

    private Button purchaseButton, consumeButton;
    public static final String LICENCE_KEY = "your_license_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        purchaseButton = (Button) findViewById(R.id.btn_purchase_product);
        consumeButton = (Button) findViewById(R.id.btn_consume_product);
        billingProcessor = new BillingProcessor(this,LICENCE_KEY,this);

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseProduct();
            }
        });

        consumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumeProduct();
            }
        });

        for(PurchaseDataModel details : billingProcessor.ownedProductsTransactionDetails()){
            Log.e(TAG,details.toString());
        }
    }

    private void consumeProduct() {
        billingProcessor.consumePurchaseObservable(CONSUMABLE_PRODUCT_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConsumeModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Consume Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error on consume:" + e.getMessage(),e);
                    }

                    @Override
                    public void onNext(ConsumeModel consumeModel) {
                        if(consumeModel.isSuccess())
                            Log.e(TAG,"Product consume on next:" + consumeModel.getProductId());
                        else{
                            Log.e(TAG,"Product consume on next error code:" + consumeModel.getErrorCode());
                        }
                    }
                });
    }

    private void purchaseProduct() {
        billingProcessor.purchaseObservable(this,CONSUMABLE_PRODUCT_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PurchaseModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Purchase Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error on purchase:" + e.getMessage(),e);
                    }

                    @Override
                    public void onNext(PurchaseModel purchaseModel) {
                        if(purchaseModel.isSuccess())
                            Log.e(TAG,"Product purchase on next:" + purchaseModel.getProductId());
                        else
                            Log.e(TAG,"Product purchase on next error code:" + purchaseModel.getErrorCode());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    protected void onDestroy() {
        billingProcessor.release();
        super.onDestroy();
    }
}
