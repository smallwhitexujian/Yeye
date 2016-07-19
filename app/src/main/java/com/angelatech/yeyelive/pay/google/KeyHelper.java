package com.angelatech.yeyelive.pay.google;

import android.content.Context;

import com.android.vending.billing.security.SecurityUtil;
import com.android.vending.billing.util.Base64;
import com.android.vending.billing.util.Base64DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jjfly on 15-12-15.
 */
public class KeyHelper {

    private Context mContext;
    private final String KEY = "will.com";

    public KeyHelper(Context context){
        this.mContext = context;
    }


    //从key.propties中获取key,base64解密后，再DES解密
    public String getPublicStrKey() {
        String publicKey = null;
        try {
            Properties prop = new Properties();
            InputStream in = mContext.getResources().getAssets().open("key.properties");
            prop.load(in);
            String encryptStr = prop.getProperty("key");
            byte[] encrypData = Base64.decode(encryptStr);
            publicKey = SecurityUtil.decryptDES(encrypData, KEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Base64DecoderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
