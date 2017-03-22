package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonResultCode;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.PlayRecord;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.mediaplayer.util.PlayerUtil;
import com.angelatech.yeyelive1.model.CommonVideoModel;
import com.angelatech.yeyelive1.model.LiveModel;
import com.angelatech.yeyelive1.model.LiveVideoModel;
import com.angelatech.yeyelive1.model.VideoModel;
import com.angelatech.yeyelive1.qiniu.QiniuUpload;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.PictureObtain;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.UriHelper;
import com.angelatech.yeyelive1.view.ActionSheetDialog;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 10:04
 * 个人视频
 */
public class UserVideoActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private PlayRecord playRecord;
    private BasicUserInfoDBModel loginUser = null;
    private int videoId = 0;
    private final int pageSize = 10;
    private int pageIndex = 1;
    private final int MSG_VIDEO_LIST_SUCCESS = 1;
    private final int MSG_VIDEO_LIST_NODATA = 2;
    private final int MSG_DELETE_VIDEO_SUCCESS = 3;
    private final int MSG_DELETE_VIDEO_ERROR = 4;
    private final int MSG_UP_COVER_SUCCESS = 5;
    private final int MSG_NO_MORE = 9;
    private CommonAdapter<LiveVideoModel> adapter;
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<LiveVideoModel> list = new ArrayList<>();
    private FrameLayout layout_delete;
    private int itemPosition = 0;
    private RelativeLayout noDataLayout;
    private volatile boolean IS_REFRESH = true;  //是否需要刷新
    private String otherId = null;
    private PictureObtain mObtain;
    private Uri distUri;
    private TextView tops;
    private QiniuUpload qiNiuUpload;
    private String imgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_video);
        otherId = StartActivityHelper.getTransactionSerializable_1(this);
        setView();
        initData();
    }

    private void setView() {
        if (otherId == null) {
            headerLayout.showTitle(getString(R.string.activity_title_video));
        } else {
            headerLayout.showTitle(getString(R.string.activity_video));
        }
        headerLayout.showLeftBackButton();
        mObtain = new PictureObtain();
        qiNiuUpload = new QiniuUpload(this);
        ListView list_view_user_videos = (ListView) findViewById(R.id.list_view_user_videos);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        layout_delete = (FrameLayout) findViewById(R.id.layout_delete);
        TextView tv_delete = (TextView) findViewById(R.id.tv_delete);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_frontCover = (TextView) findViewById(R.id.tv_frontCover);
        tops = (TextView) findViewById(R.id.tops);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        tv_delete.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_frontCover.setOnClickListener(this);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        list_view_user_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                VideoModel item = (VideoModel) parent.getItemAtPosition(position);
                StartActivityHelper.jumpActivity(UserVideoActivity.this, PlayActivity.class, item);
            }
        });

        list_view_user_videos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (otherId == null) {
                    layout_delete.setVisibility(View.VISIBLE);
                }
                itemPosition = position;
                if (itemPosition < list.size()) {
                    if (list.get(itemPosition).type == LiveModel.TYPE_RECORD) {
                        VideoModel model = (VideoModel) list.get(itemPosition);
                        videoId = Integer.parseInt(model.videoid);
                    }
                }
                return true;
            }
        });

        adapter = new CommonAdapter<LiveVideoModel>(this, list, R.layout.item_video) {
            @Override
            public void convert(ViewHolder helper, LiveVideoModel item, int position) {
                if (item.type == LiveVideoModel.TYPE_RECORD) { //录像
                    VideoModel model = (VideoModel) item;
                    if (model.barcoverurl.contains("http")) {
                        helper.setImageViewByImageLoader1(R.id.iv_cover, model.barcoverurl);
                    } else {
                        helper.setImageViewByImageLoader1(R.id.iv_cover, String.valueOf(UriHelper.fromFile(model.barcoverurl)));
                    }
                    helper.setText(R.id.tv_title, model.introduce);
                    helper.setText(R.id.tv_video_time, model.addtime);
                    helper.setText(R.id.tv_play_num, model.playnum);
                    helper.setText(R.id.tv_video_time_second, model.durations == null ? PlayerUtil.showTime(0) : PlayerUtil.showTime3(Integer.valueOf(model.durations)));
                }
            }
        };
        list_view_user_videos.setAdapter(adapter);
    }

    private void initData() {
        loginUser = CacheDataManager.getInstance().loadUser();
        playRecord = new PlayRecord(this);
        swipyRefreshLayout.setRefreshing(true);
        if (loginUser != null) {
            if (otherId == null) {
                playRecord.getUserRecord(loginUser.userid, loginUser.token, loginUser.userid, pageSize, pageIndex, callback);
            } else {
                playRecord.getUserRecord(loginUser.userid, loginUser.token, String.valueOf(otherId), pageSize, pageIndex, callback);
            }
            if (loginUser.isv.equals("1")) {
                tops.setText(String.format(getString(R.string.tops_video), "50"));
            } else {
                tops.setText(String.format(getString(R.string.tops_video), "10"));
            }
        }
    }

    private void showNodataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_no_video));
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_VIDEO_LIST_SUCCESS:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_VIDEO_LIST_NODATA:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                showNodataLayout();
                //adapter.notifyDataSetChanged();
                break;
            case MSG_NO_MORE:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                ToastUtils.showToast(this, getString(R.string.no_data_more));
                break;
            case MSG_DELETE_VIDEO_SUCCESS:
                layout_delete.setVisibility(View.GONE);
                if (list.size() > 0) {
                    list.remove(itemPosition);
                }
                adapter.notifyDataSetChanged();
                ToastUtils.showToast(this, getString(R.string.video_delete_success));
                break;
            case MSG_DELETE_VIDEO_ERROR:
                layout_delete.setVisibility(View.GONE);
                ToastUtils.showToast(this, getString(R.string.video_delete_error));
                break;
            case MSG_UP_COVER_SUCCESS:
                list.get(itemPosition).barcoverurl = (String) msg.obj;
                adapter.setData(list);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete:
                if (itemPosition < list.size()) {
                    if (list.get(itemPosition).type == LiveModel.TYPE_RECORD) {
                        VideoModel model = (VideoModel) list.get(itemPosition);
                        videoId = Integer.parseInt(model.videoid);
                        playRecord.deleteRecord(loginUser.userid, loginUser.token, videoId, deleteVideo);
                    }
                }
                break;
            case R.id.tv_cancel:
                layout_delete.setVisibility(View.GONE);
                break;
            case R.id.tv_frontCover:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.camera), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(UserVideoActivity.this, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.album), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(UserVideoActivity.this, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                layout_delete.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 接收用户返回头像参数
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CommonResultCode.SET_ADD_PHOTO_CAMERA:
                    //拍照
                    distUri = mObtain.obtainUrl();
                    mObtain.notifyChange(this, mObtain.getUri(this));
                    mObtain.cropBig(this, mObtain.getUri(this), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    break;
                case CommonResultCode.SET_ADD_PHOTO_ALBUM:
                    //从相册获取
                    if (data != null) {
                        distUri = mObtain.obtainUrl();
                        mObtain.cropBig(this, data.getData(), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    }
                    break;
                case CommonResultCode.REQUEST_CROP_PICTURE:
                    //裁剪后的图片
                    final String path = mObtain.getRealPathFromURI(this, distUri);
                    if (!new File(path).exists()) {
                        return;
                    }
                    try {
                        Bitmap bitmap = mObtain.getimage(path);
                        imgPath = mObtain.saveBitmapFile(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.get(itemPosition).barcoverurl = imgPath;
                    adapter.setData(list);
                    qiNiuUpload.setQiniuResultCallback(new QiniuUpload.QiniuResultCallback() {
                        @Override
                        public void onUpTokenError() {

                        }

                        @Override
                        public void onUpQiniuError() {
                            LoadingDialog.cancelLoadingDialog();
                            ToastUtils.showToast(UserVideoActivity.this, getString(R.string.upload_photo_error));
                        }

                        @Override
                        public void onCallServerError() {

                        }

                        @Override
                        public void onUpQiniuSuc(String key) {
                            uiHandler.obtainMessage(MSG_UP_COVER_SUCCESS, imgPath).sendToTarget();
                        }

                        @Override
                        public void onUpQinniuResult(String key) {

                        }

                        @Override
                        public void onUpProgress(String key, double percent) {
                        }
                    });
                    qiNiuUpload.doUpload(loginUser.userid, loginUser.token, imgPath, String.valueOf(videoId), "3");
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private HttpBusinessCallback deleteVideo = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            uiHandler.sendEmptyMessage(MSG_DELETE_VIDEO_SUCCESS);
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            uiHandler.sendEmptyMessage(MSG_DELETE_VIDEO_ERROR);
        }
    };

    /**
     * 请求数据 回调接口
     */
    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            if (response != null) {
                CommonVideoModel<LiveModel, VideoModel> result = JsonUtil.fromJson(response, new TypeToken<CommonVideoModel<LiveModel, VideoModel>>() {
                }.getType());
                if (result != null) {
                    if (HttpFunction.isSuc(result.code)) {
                        if (result.videodata != null && result.videodata.size() > 0) {
                            if (IS_REFRESH) {
                                list.clear();
                            }
                            list.addAll(result.videodata);
                            uiHandler.obtainMessage(MSG_VIDEO_LIST_SUCCESS).sendToTarget();
                        } else {
                            if (!IS_REFRESH) {
                                uiHandler.obtainMessage(MSG_NO_MORE).sendToTarget();
                            }
                        }
                    }
                }
                if (list.isEmpty()) {
                    uiHandler.sendEmptyMessage(MSG_VIDEO_LIST_NODATA);
                }
            }
        }
    };

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    IS_REFRESH = true;
                    pageIndex = 0;
                } else {
                    IS_REFRESH = false;
                }
                pageIndex++;
                initData();
            }
        });

    }
}
