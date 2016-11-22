package com.angelatech.yeyelive.pay.MimoPay;

import android.content.Context;

import com.angelatech.yeyelive.application.App;
import com.mimopay.Mimopay;
import com.mimopay.MimopayInterface;
import com.mimopay.merchant.Merchant;
import com.will.view.ToastUtils;

import java.util.ArrayList;

/**
 *
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 *
 *
 * 作者: Created by: xujian on Date: 2016/11/2.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.pay.MimoPay
 */

public class MimoPayLib {
    public static int MPOINT = 10;//Maxis
    public static int DPOINT = 11;//Digi
    public static int CELCOM = 12;//Celcom
    private boolean mbGateway = true;//设置正式环境和测试环境
    private Mimopay mMimopay = null;//初始化mimopay支付
    private CallBack callback;

    public void setcallBack(CallBack back){
        this.callback = back;
    }

    public interface CallBack{
        void cuccess();

        void error();

        void fatalerror();
    }

    public void initMimopay(Context context,MimopayModel mimopayModel) {
        String emailOrUserId = mimopayModel.UserId;             // 用户id
        String merchantCode = "ID-0218";                        // 唯一标识(不变)
        String productName = mimopayModel.productName;          // 商品名称
        String transactionId = mimopayModel.transactionId;      // (订单号)这应该是独特的在每个事务。如果你把它空,SDK将产生独特的数字基于unix时间戳
        String secretKeyStaging = null;
        String secretKeyGateway = null;

        // 你可能发起secretKeyStaging secretKeyGateway硬编码在程序的源代码,但是如果这是不合适的,你可以使用我们的
        // 加密secretKey避免它。每个注册商人都应该收到我们,两个文件的jar文件和txt文件。Txt文件包含secretKey加密密钥,
        // 而jar文件包含secretKey加密值。所有您需要做的就是选择并复制加密密钥从txt文件,粘贴到Merchant.get()的参数,
        // 它将返回你真正的secretKey。
        try {
            secretKeyStaging = Merchant.get(true, "5dIVdFT2Qj99xUe1vgdyZw==");
            secretKeyGateway = Merchant.get(false, "QXerDSTtqiQZ4yrkzIyRPQ==");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String currency = mimopayModel.currency;//默认币种
        if (secretKeyStaging == null || secretKeyGateway == null) {//判断是否填写签名key
            ToastUtils.showToast(context,"secretKey problem!");
            return;
        }
        //初始化参数
        mMimopay = new Mimopay(context,
                emailOrUserId,
                merchantCode,
                productName,
                transactionId,
                secretKeyStaging,
                secretKeyGateway,
                currency,
                new MimopayInterface() {//回调
                    public void onReturn(String info, ArrayList<String> params) {
                        String s, toastmsg = "";
                        System.out.println("onReturn: " + info);
                        if (params != null) {
                            switch (info) {
                                case "SUCCESS":
                                    if (callback!=null){
                                        callback.cuccess();
                                    }
                                    break;
                                case "ERROR":
                                    if (callback!=null){
                                        callback.error();
                                    }
                                    break;
                                case "FATALERROR":
                                    if (callback!=null){
                                        callback.fatalerror();
                                    }
                                    break;
                            }
                            toastmsg += (info + "\n\n");
                            int i, j = params.size();
                            for (i = 0; i < j; i++) {
                                s = params.get(i);
                                toastmsg += (s + "\n");
                                System.out.println(String.format("onReturn: " + "[%d] %s", i, s));
                            }
                        }
                    }
                }
        );

        //设置生产环境和测试环境
        mMimopay.enableGateway(!App.isDebug);
        // enableLog Mimopay SDK的内部日志打印。如果设置为启用,所有日志打印出来在你的应用程序的日志。这是非常有用的在开发阶段
        // 请注意:“enableGateway(真正的)将禁用日志曾称,如果你仍然想看看日志在生产时,那么你需要去重新调用enableLog(真正的)
        mMimopay.enableLog(true);
        switch (mimopayModel.paymentid) {
            case 10: // mpoint
                paymentMPoint(mimopayModel.currency, mimopayModel.coins);
                break;
            case 11: // dpoint
                paymentDPoint(mimopayModel.currency, mimopayModel.coins);
                break;
            case 12: // celcom
                paymentCelcom(mimopayModel.currency, mimopayModel.coins);
                break;
        }
    }


    //点击支付掉支付过程
    //currency MYR
    //coins 10
    //maxis
    private void paymentMPoint(String currency, String coins) {
        if (mMimopay == null) return;
        mMimopay.setCurrency(currency);
        mMimopay.executeMPointAirtime(coins);

    }
    //digi
    private void paymentDPoint(String currency, String coins) {
        if (mMimopay == null) return;
        mMimopay.setCurrency(currency);
        mMimopay.executeDPointAirtime(coins);
    }

    //celcom
    private void paymentCelcom(String currency, String coins) {
        if (mMimopay == null) return;
        mMimopay.setCurrency(currency);
        mMimopay.executeCelcomAirtime(coins);
    }
}
