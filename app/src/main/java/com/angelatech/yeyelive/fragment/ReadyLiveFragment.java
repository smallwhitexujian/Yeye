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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.Ticket;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.QqShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.SinaShare;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.LoadBitmap;
import com.angelatech.yeyelive.util.LocationMap.GpsTracker;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.datasource.DataSource;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment准备直播(预览)页面
 */
public class ReadyLiveFragment extends BaseFragment {
    private final int START_LIVE_CODE = 1;
    private final int LIVE_USER = 2; //直播者
    private View controlView;
    private RelativeLayout ly_body;
    private ImageView btn_sign_on_location, img_location_bg;
    private ImageView btn_facebook, btn_webchatmoments, btn_wechat, btn_weibo;//facebook
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
    private List<String> spinnnerList = new ArrayList<>();
    private String uid, token;
    private ArrayAdapter<String> spinnnerAdapter;
    private Spinner spinnner;
    private LinearLayout layout_ticket;

    public interface OnCallEvents {
        //开始直播
        void onBeginLive();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_ready_live, container, false);
        uid = ChatRoomActivity.userModel.userid;
        token = ChatRoomActivity.userModel.token;
        initView();
        findView();
        goAnimation();
        return controlView;
    }

    private void initView() {
        chatRoom = new ChatRoom(getActivity());
        spinnner = (Spinner) controlView.findViewById(R.id.spinner);
        txt_title = (EditText) controlView.findViewById(R.id.txt_title);
        btn_start = (Button) controlView.findViewById(R.id.btn_start);
        ly_body = (RelativeLayout) controlView.findViewById(R.id.ly_body);
        btn_sign_on_location = (ImageView) controlView.findViewById(R.id.btn_sign_on_location);
        btn_facebook = (ImageView) controlView.findViewById(R.id.btn_facebook);
        img_location_bg = (ImageView) controlView.findViewById(R.id.img_location_bg);
        mLocationInfo = (TextView) controlView.findViewById(R.id.location_info);
        btn_webchatmoments = (ImageView) controlView.findViewById(R.id.btn_webchatmoments);
        btn_wechat = (ImageView) controlView.findViewById(R.id.btn_wechat);
        btn_weibo = (ImageView) controlView.findViewById(R.id.btn_weibo);
        layout_ticket = (LinearLayout) controlView.findViewById(R.id.layout_ticket);
        if (ChatRoomActivity.userModel.isticket.equals("1")) {
            layout_ticket.setVisibility(View.VISIBLE);
            initTickets();
        }else{
            layout_ticket.setVisibility(View.GONE);
        }
    }

    private void initTickets() {
        chatRoom.getPayTicketsList(uid, token, callback);
        spinnnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_gift_pop_item) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.spinner_item_layout, parent, false);
                TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
                label.setText(spinnnerList.get(position));
                return view;
            }
        };

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    straddres = gpsTracker.getCity();
                }
            }).start();
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
        if (!ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            ly_body.setVisibility(View.VISIBLE);
            dialogTitle = getString(R.string.share_title);
            text = txt_title.getText().toString();
            fragmentHandler.sendEmptyMessage(LIVE_USER);
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
        LiveVideoBroadcast(txt_title.getText().toString(), straddres, App.price);
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            straddres = gpsTracker.getCity();
                        }
                    }).start();
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
        switch (msg.what) {
            case START_LIVE_CODE:
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
                        ChatRoomActivity.roomModel.setLiveid(jsonData.getString("liveid"));
                        ChatRoomActivity.roomModel.setName(txt_title.getText().toString());
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
                break;
            case LIVE_USER:
                new Timer().schedule(
                        new TimerTask() {
                            public void run() {
                                txt_title.setFocusable(true);
                                txt_title.setFocusableInTouchMode(true);
                                txt_title.requestFocus();
                                Utility.openKeybord(txt_title, getActivity());
                            }
                        },
                        200);
                break;
        }

    }

    // 初始化直播数据
    private void LiveVideoBroadcast(String title, String area, String price) {
        LoadingDialog.showSysLoadingDialog(getActivity(), getString(R.string.go_in));
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if (HttpFunction.isSuc((String) map.get("code"))) {
                    fragmentHandler.obtainMessage(START_LIVE_CODE, response).sendToTarget();
                } else {
                    onBusinessFaild((String) map.get("code"));
                }
            }
        };
        chatRoom.LiveVideoBroadcast(CommonUrlConfig.LiveVideoBroadcast, CacheDataManager.getInstance().loadUser(), title, area, price, callback);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    /**
     * 支付门票回调
     */
    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            CommonListResult<Ticket> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<Ticket>>() {
            }.getType());
            if (results != null) {
                for (int i = 0; i < results.data.size(); i++) {
                    spinnnerList.add(results.data.get(i).price);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinnner.setAdapter(spinnnerAdapter);
                        spinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                App.price = String.valueOf(spinnnerList.get(position));
                                parent.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        spinnnerAdapter.clear();
                        spinnnerAdapter.addAll(spinnnerList);
                        spinnnerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            super.onFailure(errorMap);
        }
    };

    /**
     * 更新票价
     */
    private HttpBusinessCallback callback2 = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            DebugLogs.d("更新票价" + response);
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            super.onFailure(errorMap);
        }
    };


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