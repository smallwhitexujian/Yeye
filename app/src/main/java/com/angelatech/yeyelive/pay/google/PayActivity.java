package com.angelatech.yeyelive.pay.google; /***
 * 支付activity
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.android.vending.billing.pay.PayManager;
import com.android.vending.billing.pay.PayManager.PayManagerCallback;
import com.android.vending.billing.util.Purchase;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;


public class PayActivity extends HeaderBaseActivity implements CommonDoHandler {
    protected int requestCode = 1000;

    private final int PREFIX = 0xAA;
    protected final int CANCEL_PURCHASE = PREFIX + 9;
    private final String KEY = "will.com";

    private CommonHandler<PayActivity> mHandler = new CommonHandler(this);


    private PayManager mPayManager = new PayManager(mHandler, new PayManagerCallback() {

        @Override
        public String getPublicKey() {
            //String base64EncodedPublicKey = new KeyHelper(PayActivity.this).getPublicStrKey();
            //return base64EncodedPublicKey;
            return PayManager.RSA_KEY;
        }

        @Override
        public boolean verifyDeveloperPayload(Purchase p) {
            // 验证商品
            String payload = p.getDeveloperPayload();
            /*
             * TODO: verify that the developer payload of the purchase
			 * is correct. It will be the same one that you sent when
			 * initiating the purchase.
			 * 
			 * WARNING: Locally generating a random string when starting
			 * a purchase and verifying it here might seem like a good
			 * approach, but this will fail in the case where the user
			 * purchases an item on one device and then uses your app on
			 * a different device, because on the other device you will
			 * not have access to the random string you originally
			 * generated.
			 * 
			 * So a good developer payload has these characteristics:
			 * 
			 * 1. If two different users purchase an item, the payload
			 * is different between them, so that one user's purchase
			 * can't be replayed to another user.
			 * 
			 * 2. The payload must be such that you can verify it even
			 * when the app wasn't the one who initiated the purchase
			 * flow (so that items purchased by the user on one device
			 * work on other devices owned by the user).
			 * 
			 * Using your own server to store and verify developer
			 * payloads across app installations is recommended.
			 */
            return true;
        }
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPayManager.onCreate(PayActivity.this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPayManager.onDestroy();
    }


    public void pay(String sku, int requestCode, String extraData) {
        mPayManager.pay(PayActivity.this, sku, requestCode, extraData);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPayManager.onActivityResult(requestCode, resultCode, data)) {
            //购买成功回调
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                mHandler.obtainMessage(CANCEL_PURCHASE).sendToTarget();
            }
        }
    }

    @Override
    public void doHandler(Message msg) {

    }
}
