package com.angelatech.yeyelive.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.LableModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.Ticket;
import com.angelatech.yeyelive.model.VoucherModel;
import com.angelatech.yeyelive.model.coverInfoModel;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.QqShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.SinaShare;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.LoadBitmap;
import com.angelatech.yeyelive.util.LocationMap.GpsTracker;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.datasource.DataSource;
import com.google.gson.reflect.TypeToken;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment准备直播(预览)页面
 */
public class ReadyLiveFragment extends BaseFragment {
    private final int START_LIVE_CODE = 1;
    private final int MSG_LOCATION_SUCCESS = 12;
    private final int LIVE_USER = 2; //直播者
    private final int SPINNNER_CHANGE = 20023;
    private View controlView;
    private RelativeLayout ly_body;
    private ImageView btn_sign_on_location, img_location_bg, img_start_play_pwd;
    private ImageView btn_facebook, btn_webchatmoments, btn_wechat, btn_weibo;//facebook
    private EditText txt_title;
    private GpsTracker gpsTracker;
    private Button btn_start;
    private ChatRoom chatRoom;
    private OnCallEvents callEvents;
    private String straddres = "";
    private boolean isLocation = true;
    private String text, imageUrl;
    private Bitmap img = null;
    private Animation rotateAnimation;
    private TextView mLocationInfo;
    private String typeKind = "";
    private String getTypeKind = "";
    private List<String> spinnnerList = new ArrayList<>();
    private ArrayAdapter<String> spinnnerAdapter;
    private Spinner spinnner;
    private BasicUserInfoDBModel liveUserModel, loginUserModel;
    private RoomModel roomModel;
    private ImageView buttonCamera;
    private FrescoRoundView Front_cover;
    private ArrayList<LableModel> typeList;
    private CommonAdapter<LableModel> commonAdapter;

    public interface OnCallEvents {
        //开始直播
        void onBeginLive();

        void onCameraSwitch();

        void onCamera();
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
        App.roompwd = "";
        chatRoom = new ChatRoom(getActivity());
        typeList = new ArrayList<>();
        RelativeLayout ready_layout = (RelativeLayout) controlView.findViewById(R.id.ready_layout);
        spinnner = (Spinner) controlView.findViewById(R.id.spinner);
        txt_title = (EditText) controlView.findViewById(R.id.txt_title);
        btn_start = (Button) controlView.findViewById(R.id.btn_start);
        ly_body = (RelativeLayout) controlView.findViewById(R.id.ly_body);
        btn_sign_on_location = (ImageView) controlView.findViewById(R.id.btn_sign_on_location);
        btn_facebook = (ImageView) controlView.findViewById(R.id.btn_facebook);
        img_location_bg = (ImageView) controlView.findViewById(R.id.img_location_bg);
        mLocationInfo = (TextView) controlView.findViewById(R.id.location_info);
        btn_webchatmoments = (ImageView) controlView.findViewById(R.id.btn_webchatmoments);
        buttonCamera = (ImageView) controlView.findViewById(R.id.button_call_switch_camera);
        btn_wechat = (ImageView) controlView.findViewById(R.id.btn_wechat);
        btn_weibo = (ImageView) controlView.findViewById(R.id.btn_weibo);
        GridView gridView = (GridView) controlView.findViewById(R.id.gridView);
        Front_cover = (FrescoRoundView) controlView.findViewById(R.id.Front_cover);
        LinearLayout layout_ticket = (LinearLayout) controlView.findViewById(R.id.layout_ticket);
        LinearLayout layout_lock = (LinearLayout) controlView.findViewById(R.id.layout_lock);
        img_start_play_pwd = (ImageView) controlView.findViewById(R.id.img_start_play_pwd);
        layout_lock.setOnClickListener(this);
        loginUserModel = CacheDataManager.getInstance().loadUser();
        if (ChatRoomActivity.roomModel.getUserInfoDBModel() != null) {
            roomModel = ChatRoomActivity.roomModel;
            liveUserModel = roomModel.getUserInfoDBModel();
        } else {
            liveUserModel = loginUserModel;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarHeight = ScreenUtils.getStatusHeight(getActivity());
            ready_layout.setPadding(0, statusBarHeight, 0, 0);
        }
        if (liveUserModel.isticket.equals("1")) {//主播是否有设置门票权限
            layout_ticket.setVisibility(View.VISIBLE);
            initTickets();
        } else {
            layout_ticket.setVisibility(View.GONE);
        }

        if (liveUserModel.ispwdroom.equals("1")) {
            layout_lock.setVisibility(View.VISIBLE);
        } else {
            layout_lock.setVisibility(View.GONE);
        }
        getRoomInfo(loginUserModel.userid, loginUserModel.token);
        commonAdapter = new CommonAdapter<LableModel>(getActivity(), typeList, R.layout.item_type) {
            @Override
            public void convert(ViewHolder helper, LableModel item, int position) {
                helper.setText(R.id.type_1, typeList.get(position).lable);
                if (item.isCinle) {
                    helper.setTextBackground(R.id.type_1, ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_red));
                } else {
                    helper.setTextBackground(R.id.type_1, ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_wirte));
                }
            }
        };

        gridView.setAdapter(commonAdapter);
        gridView.setSelection(0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                for (int m = 0; m < adapterView.getCount(); m++) {
//                    View v = adapterView.getChildAt(m);
//                    if (position == m) {
//                        v.findViewById(R.id.type_1).setBackgroundResource(R.drawable.bg_circle_red);
//                    } else {
//                        v.findViewById(R.id.type_1).setBackgroundResource(R.drawable.bg_circle_wirte);
//                    }
//                }
                if (!typeList.get(position).isCinle) {
                    typeList.get(position).isCinle = true;
                } else {
                    typeList.get(position).isCinle = false;
                }
                commonAdapter.notifyDataSetChanged();
            }
        });
    }

    //初始化门票功能,
    private void initTickets() {
        chatRoom.getPayTicketsList(loginUserModel.userid, loginUserModel.token, callback);
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
        if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
            getLocationCity();
            mLocationInfo.setText(straddres);
            App.chatRoomApplication.setOpenFB();
        }
    }

    private void findView() {
        btn_start.setOnClickListener(this);
        btn_sign_on_location.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_webchatmoments.setOnClickListener(this);
        btn_wechat.setOnClickListener(this);
        btn_weibo.setOnClickListener(this);
        Front_cover.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);

        if (!roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            ly_body.setVisibility(View.VISIBLE);
            text = txt_title.getText().toString();
            fragmentHandler.sendEmptyMessage(LIVE_USER);
        }
        imageUrl = liveUserModel.headurl;
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

    //开播
    private void startLive() {
        String str = "";
        typeKind = "";
        int y = 0;
        for (LableModel s : typeList) {
            if (s.isCinle) {
                y++;
                typeKind += "," + s.lable;
            }
        }
        if (y > 3) {
            ToastUtils.showToast(getActivity(), "标签只能选择三个");
            return;
        }
        if (typeKind.length() > 1) {
            str = typeKind.substring(1, typeKind.length());
        }
        chatRoom.addtag(CommonUrlConfig.addtag, loginUserModel.userid, str, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonParseModel commonParseModel = JsonUtil.fromJson(response, CommonParseModel.class);
                        if (commonParseModel != null && commonParseModel.code.equals("1000")) {
                            img_location_bg.clearAnimation();
                            rotateAnimation.cancel();
                            LiveVideoBroadcast(txt_title.getText().toString(), straddres, App.price, App.roompwd);
                        }
                    }
                });
            }
        });
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
                    getLocationCity();
                } else {
                    btn_sign_on_location.setImageResource(R.drawable.btn_sign_on_location_s);
                    img_location_bg.setVisibility(View.GONE);
                    img_location_bg.clearAnimation();
                    straddres = "";
                }
                break;

            case R.id.btn_facebook:
                closekeybord();
                if (imageUrl.indexOf("http://file") > 0) {
                    imageUrl = imageUrl.substring(0, imageUrl.indexOf("?")) + "?imageView2/2/w/1200/h/650";
                }
                if (text.equals("")) {
                    text = getString(R.string.shareTitle);
                }
                String liveUrl = CommonUrlConfig.facebookURL + "?uid=" + liveUserModel.userid + "&videoid=";
                FbShare fbShare = new FbShare(getActivity(), shareListener);
                fbShare.postStatusUpdate(text, getString(R.string.shareDescription), liveUrl, imageUrl);
                break;
            case R.id.btn_webchatmoments:
                closekeybord();
                if (text.equals("")) {
                    text = getString(R.string.shareTitle);
                }
                WxShare webchatmoment = new WxShare(getActivity(), shareListener);
                webchatmoment.SceneWebPage(text, getString(R.string.shareDescription), CommonUrlConfig.facebookURL + "?uid=" + liveUserModel.userid,
                        img, 1);
                break;
            case R.id.btn_wechat:
                closekeybord();
                if (text.equals("")) {
                    text = getString(R.string.shareTitle);
                }
                WxShare wxShare = new WxShare(getActivity(), shareListener);
                wxShare.SceneWebPage(text, getString(R.string.shareDescription), CommonUrlConfig.facebookURL + "?uid=" + liveUserModel.userid, img, 0);
                break;
            case R.id.btn_weibo:
                closekeybord();
                if (text.equals("")) {
                    text = getString(R.string.shareTitle);
                }
                SinaShare sinaShare = new SinaShare(getActivity(), text, getString(R.string.shareDescription),
                        CommonUrlConfig.facebookURL + "?uid=" + liveUserModel.userid, img);
                sinaShare.registerCallback(shareListener);
                sinaShare.share(true, true, true, false, false, false);
                break;
            case R.id.button_call_switch_camera:
                callEvents.onCameraSwitch();
                break;
            case R.id.Front_cover:
                callEvents.onCamera();
                break;
            case R.id.layout_lock:
                //设置密码
                LockChooseDialogFragment.Callback callback = new LockChooseDialogFragment.Callback() {
                    @Override
                    public void onCancel() {
                        App.roompwd = "";
                        img_start_play_pwd.setImageResource(R.drawable.btn_start_play_passroom_n);
                    }

                    @Override
                    public void onEnter(String password) {
                        App.roompwd = password;
                        ToastUtils.showToast(getActivity(), getString(R.string.setpassword) + password);
                        img_start_play_pwd.setImageResource(R.drawable.btn_start_play_passroom_s);
                        spinnner.setSelection(0);
                    }
                };

                LockChooseDialogFragment lockChooseDialogFragment = new LockChooseDialogFragment(getActivity(), callback, App.roompwd, 0);
                lockChooseDialogFragment.show(getActivity().getFragmentManager(), "");
                break;
        }
    }

    /**
     * 获取定位
     */
    private void getLocationCity() {
        if (gpsTracker == null) {
            gpsTracker = new GpsTracker(getActivity());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                straddres = gpsTracker.getCity();
                fragmentHandler.sendEmptyMessage(MSG_LOCATION_SUCCESS);
            }
        }).start();
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
            case SPINNNER_CHANGE:
                App.roompwd = "";
                img_start_play_pwd.setImageResource(R.drawable.btn_start_play_passroom_n);
                break;

            case START_LIVE_CODE:
                JSONObject json;
                try {
                    json = new JSONObject((String) msg.obj);
                    if (json.getInt("code") == 1000) {
                        JSONObject jsonData = json.getJSONObject("data");
                        if (jsonData != null) {
                            ChatRoomActivity.roomModel.setId(jsonData.getInt("roomid"));
                            ChatRoomActivity.roomModel.setRtmpip(jsonData.getString("rtmpaddress"));
                            ChatRoomActivity.roomModel.setRtmpwatchaddress(jsonData.getString("rtmpwatchaddress"));
                            ChatRoomActivity.roomModel.setIp(jsonData.getString("roomserverip").split(":")[0]);
                            ChatRoomActivity.roomModel.setPort(Integer.parseInt(jsonData.getString("roomserverip").split(":")[1]));
                            ChatRoomActivity.roomModel.setLiveid(jsonData.getString("liveid"));
                            ChatRoomActivity.roomModel.setName(txt_title.getText().toString());
                        }
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_title.setFocusable(true);
                        txt_title.setFocusableInTouchMode(true);
                        txt_title.requestFocus();
                        Utility.openKeybord(txt_title, getActivity());
                    }
                });
                break;
            case MSG_LOCATION_SUCCESS:
                mLocationInfo.setText(straddres);
                break;
        }
    }

    // 获取开播地址
    private void LiveVideoBroadcast(String title, String area, String price, String pwd) {
        LoadingDialog.showLoadingDialog(getActivity(), null);
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if (map != null) {
                    if (HttpFunction.isSuc((String) map.get("code"))) {
                        fragmentHandler.obtainMessage(START_LIVE_CODE, response).sendToTarget();
                    } else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            }
        };
        if (title.isEmpty()) {
            title = String.format(getString(R.string.formatted_2), loginUserModel.nickname);
        }
        chatRoom.LiveVideoBroadcast(CommonUrlConfig.LiveVideoBroadcast, loginUserModel, title, area, price, pwd, callback);
    }

    private void getRoomInfo(String userid, final String token) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(final String response) {
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonListResult<coverInfoModel> commonListResult = JsonUtil.fromJson(response, new TypeToken<CommonListResult<coverInfoModel>>() {
                            }.getType());
                            if (commonListResult != null) {
                                if (commonListResult.code.equals(String.valueOf(HttpFunction.SUC_OK))) {
                                    if (commonListResult.hasData() && !commonListResult.data.get(0).barcover.isEmpty()) {
                                        setPhoto(Uri.parse(commonListResult.data.get(0).barcover));
                                    }
                                }
                            }
                        }
                    });
                }
            }
        };
        if (chatRoom != null) {
            chatRoom.getRoomInfo(CommonUrlConfig.roomInfo, userid, token, callback);
            chatRoom.getvideotag(CommonUrlConfig.getvideotag, new HttpBusinessCallback() {
                @Override
                public void onSuccess(final String response) {
                    super.onSuccess(response);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonListResult<VoucherModel> result = JsonUtil.fromJson(response, new TypeToken<CommonListResult<VoucherModel>>() {
                                }.getType());
                                if (result != null && result.code.equals("1000")) {
                                    Locale locale = getResources().getConfiguration().locale;
                                    String language = locale.getLanguage();
                                    switch (language) {
                                        case "en":
                                            getTypeKind = result.data.get(1).value;
                                            break;
                                        case "zh":
                                            getTypeKind = result.data.get(0).value;
                                            break;
                                        default:
                                            getTypeKind = result.data.get(1).value;
                                            break;

                                    }
                                    String[] str = getTypeKind.split(",");
                                    for (int i = 0; i < str.length; i++) {
                                        LableModel model = new LableModel();
                                        model.lable = str[i];
                                        model.isCinle = i == 0;
                                        typeList.add(model);
                                    }
                                    commonAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callEvents = (OnCallEvents) context;
    }

    public void setPhoto(Uri uri) {
        Front_cover.setImageURI(uri);
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
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinnner.setAdapter(spinnnerAdapter);
                            spinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    App.price = String.valueOf(spinnnerList.get(position));
                                    parent.setVisibility(View.VISIBLE);
                                    if (position > 0) {
                                        fragmentHandler.obtainMessage(SPINNNER_CHANGE).sendToTarget();
                                    }
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