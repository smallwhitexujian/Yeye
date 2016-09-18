package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.view.CommDialog;


/**
 * Created by xujian on 16/3/14.
 * 房间错误状态提示管理
 */
public class roomToastError {
    private static roomToastError instance = null;

    public static roomToastError getInstance(){
        if (instance == null){
            instance = new roomToastError();
        }
        return instance;
    }

    private roomToastError(){
    }
    //显示没有取消按钮提示信息弹窗
    private void showDialog(Context context ,int content){
        new CommDialog().CommDialog(context, context.getString(content), false);
    }

    public void ShowRoomError(Context context ,String codeError){
        int code = Integer.valueOf(codeError);
        switch (code){
            case GlobalDef.SUCCESS_0:
                break;
            case GlobalDef.UNKNOWN_ERROR_1:
                showDialog(context, R.string.microom_code_1);
                break;
            case GlobalDef.UNPACK_ERROR_1001:
                showDialog(context,R.string.microom_code_1001);
                break;
            case GlobalDef.NOTENTER_SUCCESS_1002:
                showDialog(context,R.string.microom_code_1002);
                break;
            case GlobalDef.USER_NOTFOUND_1003:
                showDialog(context,R.string.microom_code_1003);
                break;
            case GlobalDef.BAR_NOTFOUND_1004:
                showDialog(context,R.string.microom_code_1004);
                break;
            case GlobalDef.WITHOUT_MIC_1005:
                showDialog(context,R.string.microom_code_1005);
                break;
            case GlobalDef.ONLINE_THAN_LIMIT_1006:
                showDialog(context,R.string.microom_code_1006);
                break;
            case GlobalDef.NOT_NORMAL_LOGOUT_1007:
                showDialog(context,R.string.microom_code_1007);
                break;
            case GlobalDef.SUPPRESSED_1008:
                showDialog(context,R.string.microom_code_1008);
                break;
            case GlobalDef.NO_PERMISSION_OPE_1009:
                showDialog(context,R.string.microom_code_1009);
                break;
            case GlobalDef.NOT_SUFFICIENT_FUNDS_1010:
                showDialog(context,R.string.microom_code_1010);
                break;
            case GlobalDef.KICKED_OUT_1011:
                showDialog(context,R.string.microom_code_1011);
                break;
            case GlobalDef.NOT_SUFFICIENT_COIN_1012:
                showDialog(context,R.string.microom_code_1012);
                break;
            case GlobalDef.GIFT_NONEXISTENT_1013:
                showDialog(context,R.string.microom_code_1013);
                break;
            case GlobalDef.SIGNATURE_VERIFY_FAILE_1014:
                showDialog(context,R.string.microom_code_1014);
                break;
            case GlobalDef.BAR_LOCKING_1015:
                showDialog(context,R.string.microom_code_1015);
                break;
            case GlobalDef.HAVE_MIC_1016:
                showDialog(context,R.string.microom_code_1016);
                break;
            case GlobalDef.UNUSE_MIC_1017:
                showDialog(context,R.string.microom_code_1017);
                break;
            case GlobalDef.JOIN_BAR_1018:
                showDialog(context,R.string.microom_code_1018);
                break;
            case GlobalDef.OTHER_ADMIN_PROCESS_1019:
                showDialog(context,R.string.microom_code_1019);
                break;
            case GlobalDef.TARGET_USER_NOT_MEMBER_1020:
                showDialog(context,R.string.microom_code_1020);
                break;
            case GlobalDef.TARGET_USER_NOT_ADMIN_1021:
                showDialog(context,R.string.microom_code_1021);
                break;
        }
    }
}
