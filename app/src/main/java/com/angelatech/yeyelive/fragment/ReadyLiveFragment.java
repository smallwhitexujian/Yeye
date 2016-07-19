package com.angelatech.yeyelive.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.view.CommDialog;
import com.facebook.datasource.DataSource;
import com.will.common.log.DebugLogs;
import com.will.common.tool.network.NetWorkUtil;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.QqShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.SinaShare;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.LoadBitmap;
import com.angelatech.yeyelive.util.LocationMap.GpsTracker;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive .R;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment准备直播(预览)页面
 */
public class ReadyLiveFragment extends BaseFragment {
    private final int START_LIVE_CODE = 1;
    private View controlView;
    private LinearLayout ly_body;
    private ImageView btn_sign_on_location, img_location_bg;
    private ImageView btn_facebook,btn_webchatmoments,btn_wechat,btn_weibo;//facebook
    private EditText txt_title;
    private GpsTracker gpsTracker;
    private Button btn_start;
    private ChatRoom chatRoom;
    private OnCallEvents callEvents;
    private String straddres = "";
    private boolean isLocation = true;
    private String dialogTitle = "", text, imageUrl;
    private Bitmap img = null;
    private Animation rotateAnimation;
    private TextView mLocationInfo;

    public interface OnCallEvents {
        //开始直播
        void onBeginLive();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_ready_live, container, false);
        initView();
        findView();
        goAnimation();
        return controlView;
    }

    private void initView() {
        txt_title = (EditText) controlView.findViewById(R.id.txt_title);
        btn_start = (Button) controlView.findViewById(R.id.btn_start);
        ly_body = (LinearLayout) controlView.findViewById(R.id.ly_body);
        btn_sign_on_location = (ImageView) controlView.findViewById(R.id.btn_sign_on_location);
        btn_facebook = (ImageView) controlView.findViewById(R.id.btn_facebook);
        img_location_bg = (ImageView) controlView.findViewById(R.id.img_location_bg);
        mLocationInfo = (TextView) controlView.findViewById(R.id.location_info);
        btn_webchatmoments = (ImageView) controlView.findViewById(R.id.btn_webchatmoments);
        btn_wechat = (ImageView) controlView.findViewById(R.id.btn_wechat);
        btn_weibo = (ImageView) controlView.findViewById(R.id.btn_weibo);
    }

    private void goAnimation() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.breathinglamp);
                img_location_bg.setVisibility(View.VISIBLE);
                img_location_bg.startAnimation(rotateAnimation);
            }
        });

        if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
            if (gpsTracker == null) {
                gpsTracker = new GpsTracker(getActivity());
            }
            straddres = gpsTracker.getCity();
            mLocationInfo.setText(straddres);
        }
    }

    private void findView() {
        btn_start.setOnClickListener(this);
        btn_sign_on_location.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_webchatmoments.setOnClickListener(this);
        btn_wechat.setOnClickListener(this);
        btn_weibo.setOnClickListener(this);
        txt_title.setText(String.format(getString(R.string.formatted_2), CacheDataManager.getInstance().loadUser().nickname));
        txt_title.selectAll();
        chatRoom = new ChatRoom(getActivity());
        if (!ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            ly_body.setVisibility(View.VISIBLE);
            Message msg = new Message();
            msg.what = 2;
            dialogTitle = getString(R.string.share_title);
            text = txt_title.getText().toString();
            fragmentHandler.sendMessage(msg);
        }
        imageUrl = ChatRoomActivity.userModel.headurl;
        Uri uri = Uri.parse(imageUrl);
        LoadBitmap.loadBitmap(getActivity(), uri, new LoadBitmap.LoadBitmapCallback() {
            @Override
            public void onLoadSuc(Bitmap bitmap) {
                img = bitmap;
            }

            @Override
            public void onLoadFaild(DataSource dataSource) {
                img = null;
            }
        });
    }

    private void startLive() {
        img_location_bg.clearAnimation();
        rotateAnimation.cancel();
        LiveVideoBroadcast(txt_title.getText().toString(), straddres);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                img_location_bg.setVisibility(View.GONE);
                if (NetWorkUtil.getActiveNetWorkType(getActivity()) == NetWorkUtil.TYPE_MOBILE) {
                    CommDialog commDialog = new CommDialog();
                    CommDialog.Callback callback = new CommDialog.Callback() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onOK() {
                            startLive();
                        }
                    };
                    commDialog.CommDialog(getActivity(), getString(R.string.traffic_alert), true, callback);
                } else {
                    startLive();
                }

                break;
            case R.id.btn_sign_on_location:
                isLocation = !isLocation;
                if (isLocation) {
                    btn_sign_on_location.setImageResource(R.drawable.btn_sign_on_location_n);
                    img_location_bg.setVisibility(View.VISIBLE);
                    img_location_bg.startAnimation(rotateAnimation);
                    if (gpsTracker == null) {
                        gpsTracker = new GpsTracker(getActivity());
                    }
                    straddres = gpsTracker.getCity();
                } else {
                    btn_sign_on_location.setImageResource(R.drawable.btn_sign_on_location_s);
                    img_location_bg.setVisibility(View.GONE);
                    img_location_bg.clearAnimation();
                    straddres = "";
                }
                mLocationInfo.setText(straddres);
                break;

            case R.id.btn_facebook:
                closekeybord();
                FbShare fbShare = new FbShare(getActivity(), shareListener);
                fbShare.postStatusUpdate(dialogTitle, text,
                        CommonUrlConfig.shareURL,
                        imageUrl);
                // + "?id=" + ChatRoomActivity.userModel.idx + "&type=0"
                break;
            case R.id.btn_webchatmoments:
                closekeybord();
                WxShare webchatmoment = new WxShare(getActivity(), shareListener);
                webchatmoment.SceneWebPage(dialogTitle, text, CommonUrlConfig.shareURL + "?uid=" + ChatRoomActivity.userModel.idx,
                        img, 1);
                break;
            case R.id.btn_wechat:
                closekeybord();
                WxShare wxShare = new WxShare(getActivity(), shareListener);
                wxShare.SceneWebPage(dialogTitle, text, CommonUrlConfig.shareURL + "?uid=" + ChatRoomActivity.userModel.idx, img, 0);
                break;
            case R.id.btn_weibo:
                closekeybord();
                SinaShare sinaShare = new SinaShare(getActivity(), dialogTitle, text,
                        CommonUrlConfig.shareURL + "?uid=" + ChatRoomActivity.userModel.idx, img);
                sinaShare.registerCallback(shareListener);
                sinaShare.share(true, true, true, false, false, false);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        img_location_bg.clearAnimation();
        closekeybord();
    }

    public void closekeybord() {
        Utility.closeKeybord(txt_title, getActivity());
    }

    @Override
    public void doHandler(Message msg) {
        if (msg.what == START_LIVE_CODE) {
            JSONObject json;
            try {
                json = new JSONObject((String) msg.obj);
                if (json.getInt("code") == 1000) {
                    JSONObject jsonData = json.getJSONObject("data");
                    ChatRoomActivity.roomModel.setId(jsonData.getInt("roomid"));
                    ChatRoomActivity.roomModel.setRtmpip(jsonData.getString("rtmpaddress"));
                    ChatRoomActivity.roomModel.setRtmpwatchaddress(jsonData.getString("rtmpwatchaddress"));
                    ChatRoomActivity.roomModel.setIp(jsonData.getString("roomserverip").split(":")[0]);
                    ChatRoomActivity.roomModel.setPort(Integer.parseInt(jsonData.getString("roomserverip").split(":")[1]));
                } else {
                    ToastUtils.showToast(getActivity(), getString(R.string.data_get_fail));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatRoomActivity.roomModel.setName(txt_title.getText().toString());
            LoadingDialog.cancelLoadingDialog();
            Utility.closeKeybord(txt_title, getActivity());
            ly_body.setVisibility(View.GONE);
            //进入直播
            callEvents.onBeginLive();
        } else {
            new Timer().schedule(new TimerTask() {
                                     public void run() {
                                         txt_title.setFocusable(true);
                                         txt_title.setFocusableInTouchMode(true);
                                         txt_title.requestFocus();
                                         Utility.openKeybord(txt_title, getActivity());
                                     }
                                 },
                    200);
        }
    }

    // 初始化直播数据
    private void LiveVideoBroadcast(String title, String area) {
        LoadingDialog.showSysLoadingDialog(getActivity(), getString(R.string.go_in));
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("=========获得直播数据失败了=====");
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("response--------" + response);
                Message msg = new Message();
                msg.what = START_LIVE_CODE;
                msg.obj = response;
                fragmentHandler.sendMessage(msg);
            }
        };
        chatRoom.LiveVideoBroadcast(CommonUrlConfig.LiveVideoBroadcast, CacheDataManager.getInstance().loadUser(), title, area, callback);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    /**
     * 分享 回调
     */
    private ShareListener shareListener = new ShareListener() {
        @Override
        public void callBackSuccess(int shareType) {
            switch (shareType) {
                case FbShare.SHARE_TYPE_FACEBOOK:
                    ToastUtils.showToast(getActivity(), getString(R.string.success));
                    break;
                case QqShare.SHARE_TYPE_QQ:
                    break;
                case WxShare.SHARE_TYPE_WX:
                    break;
                case SinaShare.SHARE_TYPE_SINA:
                    break;
            }
        }

        @Override
        public void callbackError(int shareType) {
            switch (shareType) {
                case FbShare.SHARE_TYPE_FACEBOOK:
                    ToastUtils.showToast(getActivity(), getString(R.string.error));
                    break;
                case QqShare.SHARE_TYPE_QQ:
                    break;
                case WxShare.SHARE_TYPE_WX:
                    break;
                case SinaShare.SHARE_TYPE_SINA:
                    break;
            }
        }

        @Override
        public void callbackCancel(int shareType) {
            switch (shareType) {
                case FbShare.SHARE_TYPE_FACEBOOK:
                    ToastUtils.showToast(getActivity(), getString(R.string.cancel));
                    break;
                case QqShare.SHARE_TYPE_QQ:
                    break;
                case WxShare.SHARE_TYPE_WX:
                    break;
                case SinaShare.SHARE_TYPE_SINA:
                    break;
            }
        }
    };
}