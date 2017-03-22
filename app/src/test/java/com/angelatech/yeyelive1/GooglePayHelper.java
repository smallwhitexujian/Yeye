package com.angelatech.yeyelive1;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class GooglePayHelper {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";


    public static final int STATE_SUC = 0;//
    public static final int STATE_CANCELLED = 1;//


    private String mClientID;
    private String mClientSecret;
    private String mRedirecctURI;

    private final String OAUTH2_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private final String OAUTH2_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    private final String API_TPL = "https://www.googleapis.com/androidpublisher/v2/applications/{0}/purchases/products/{1}/tokens/{2}?access_token={3}";

    private final String SCOPE = "https://www.googleapis.com/auth/androidpublisher";


    private Map<String, String> loginMap = new HashMap<>();
    private Map<String, String> tokenMap = new HashMap<>();

    private Gson mGson = new Gson();
    private SaveInfo mSaveInfo = new SaveInfo();


    public GooglePayHelper(String client_id, String client_secret, String redirect_uri) {
        this.mClientID = client_id;
        this.mClientSecret = client_secret;
        this.mRedirecctURI = redirect_uri;
        setLoginParams(mClientID, mRedirecctURI);
    }


    private void setLoginParams(String client_id, String redirect_uri) {
        loginMap.put("scope", SCOPE);
        loginMap.put("response_type", "code");
        loginMap.put("access_type", "offline");
        loginMap.put("client_id", mClientID);
        loginMap.put("redirect_uri", mRedirecctURI);
        loginMap.put("approval_prompt", "force");//  # 加了这个参数，code才能兑换出refresh token。提示会增加一个离>线访问权限
    }

    private void setTokenParams(String client_id, String client_secret, String redirect_uri, String authorizationCode) {
        tokenMap.put("grant_type", "authorization_code");
        tokenMap.put("code", authorizationCode);
        tokenMap.put("client_id", client_id);
        tokenMap.put("client_secret", client_secret);
        tokenMap.put("redirect_uri", redirect_uri);
    }

    public String loginURL() {
        return restructureURL(OAUTH2_AUTH_URL, loginMap);
    }

    /**
     * @param authorizationCode 调用回调之后返回的授权code
     * @return
     */
    public void getRefreshToken(String authorizationCode) {
        setTokenParams(mClientID, mClientSecret, mRedirecctURI, authorizationCode);
        String result = https_request(METHOD_POST, OAUTH2_TOKEN_URL, tokenMap);
        System.out.println(result);
        AuthorizeToken token = mGson.fromJson(result, AuthorizeToken.class);
        if (token != null && token.getRefresh_token() != null) {
            mSaveInfo.save("refresh_token", token.getRefresh_token(), "refreshtoken");
        }
    }

    //
    public String getAccessToken(String refreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", mClientID);
        params.put("client_secret", mClientSecret);
        params.put("refresh_token", refreshToken);
        String jsonStr = https_request(METHOD_POST, OAUTH2_TOKEN_URL, params);
        System.out.println("=====" + refreshToken + "======" + jsonStr);
        AuthorizeToken authorizeToken = mGson.fromJson(jsonStr, AuthorizeToken.class);
        if (authorizeToken != null && authorizeToken.getAccess_token() != null) {
            mSaveInfo.save("expires_in", authorizeToken.getExpires_in() + "", "expires_in");
            mSaveInfo.save("create_time", String.valueOf((new Date().getTime()) / 1000), "create_time");
            return authorizeToken.getAccess_token();
        }
        return null;
    }


    public void verifyBill(String packagename, String productId, String purchaseToken) {
        String expiresIn = mSaveInfo.getValue("expires_in");
        String createTime = mSaveInfo.getValue("create_time");
        String refreshToken = mSaveInfo.getValue("refresh_token");
        String access_token = null;

//        Long expires_in = expiresIn == null ? 0 : Long.valueOf(expiresIn); // 有效时长
//        Long create_time = createTime == null ? 0 : Long.valueOf(createTime); // access_token的创建时间
//        Long now_time = (new Date().getTime()) / 1000;
//        if (now_time > (create_time + expires_in - 300)) { // 提前五分钟重新获取access_token
//            access_token = getAccessToken(refreshToken);
//        }
        access_token = getAccessToken(refreshToken);

        Map<String, String> params = new HashMap<>();
        String url = MessageFormat.format(API_TPL, packagename, productId, purchaseToken, access_token);
        String result = https_request(METHOD_GET, url, params);
        System.out.println("=====" + result);
        PurchaseEntity purchaseEntity = mGson.fromJson(result, PurchaseEntity.class);
        if (purchaseEntity.getPurchaseState() == STATE_SUC) {
            System.out.println("购买成功");
        } else {
            System.out.println("取消购买");
        }
    }


    //https request
    public String https_request(String method, String urlStr, Map<String, String> params) {
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        URL url;
        try {
            url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslsocketfactory);
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(encodeParameters(params));
            writer.close();
            InputStream inputstream = conn.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedreader.readLine()) != null) {
                System.out.println("Received " + str);
                buffer.append(str);
            }

            return new String(buffer);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String restructureURL(String url, Map<String, String> params) {
        url = url + "?" + encodeParameters(params);
        return url;
    }

    private String encodeParameters(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder encodedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            encodedParams.append(entry.getKey());
            encodedParams.append('=');
            encodedParams.append(entry.getValue());
            encodedParams.append('&');
        }
        String result = encodedParams.toString();
        return result.substring(0, result.length() - 1);
    }

}
