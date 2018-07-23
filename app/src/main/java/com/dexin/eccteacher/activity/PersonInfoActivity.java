package com.dexin.eccteacher.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.bean.TeacherInfoBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxtool.view.RxToast;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonInfoActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.iv_teacher_avatar)
    ImageView mIvTeacherAvatar;//头像需要先判断缓存再联网获取
    @BindView(R.id.tv_telphone_no)
    TextView mTvTelphoneNo;
    @BindView(R.id.et_teacher_name)
    EditText mEtTeacherName;
    @BindView(R.id.rg_sex)
    RadioGroup mRgSex;
    @BindView(R.id.rb_male)
    RadioButton mRbMale;
    @BindView(R.id.rb_female)
    RadioButton mRbFemale;
    private boolean mIsMale = true;
    private String mImgFilePath = "";

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_person_info;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar;
        if ((lActionBar = getSupportActionBar()) != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("个人资料");
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mRgSex.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_male:
                    mIsMale = true;
                    break;
                case R.id.rb_female:
                    mIsMale = false;
                    break;
                default:
                    mIsMale = true;
            }
        });
    }

    @Override
    public void initData() {
        loadTeacherInfo();
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, PersonInfoActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_teacher_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_home:
                updateTeacherInfoAndCache();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 加载教师头像的逻辑-----------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-----------------------------------------------------------

    /**
     * 加载老师基本信息(先判断本地缓存,再决定是否联网)
     */
    private void loadTeacherInfo() {
        mImgFilePath = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR);
        File lCacheTeacherAvatarFile = new File(mImgFilePath);
        String lCacheTeacherAccount = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_ACCOUNT);
        String lCacheTeacherName = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_NAME);
        int lCacheTeacherSexId = AppConfig.getSPUtils().getInt(AppConfig.KEY_TEACHER_SEX);
        if (lCacheTeacherAvatarFile.exists() && !TextUtils.isEmpty(lCacheTeacherAccount) && !TextUtils.isEmpty(lCacheTeacherName) && (lCacheTeacherSexId != -1)) {//每一项本地都有缓存值,直接加载本地缓存
            Glide.with(PersonInfoActivity.this).load(lCacheTeacherAvatarFile).apply(AppConfig.CIRCLE_GLIDE_REQUEST_OPTIONS).into(mIvTeacherAvatar);
            mTvTelphoneNo.setText(lCacheTeacherAccount);
            mEtTeacherName.setText(lCacheTeacherName);
            mEtTeacherName.setSelection(mEtTeacherName.getText().toString().length());
            switch (lCacheTeacherSexId) {
                case 1://男
                    mRbMale.setChecked(true);
                    break;
                case 2://女
                    mRbFemale.setChecked(true);
                    break;
                default:
                    mRbMale.setChecked(true);
            }
        } else {//某一项没有缓存值,就联网请求,请求后缓存至本地
            OkHttpEngine.getInstance().sendAsyncPostRequest(PersonInfoActivity.this, MessageFormat.format("{0}/teacherInfo/getTeacherInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    Glide.with(PersonInfoActivity.this).load(R.drawable.ic_default_avatar).apply(AppConfig.CIRCLE_GLIDE_REQUEST_OPTIONS).into(mIvTeacherAvatar);
                    mTvTelphoneNo.setText("服务错误,获取失败");
                    mEtTeacherName.setText("服务错误,获取失败");
                    mEtTeacherName.setSelection(mEtTeacherName.getText().toString().length());
                    mRbMale.setChecked(true);
                    RxToast.error("服务错误,获取教师信息失败!");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    TeacherInfoBean lTeacherInfoBean = OkHttpEngine.toObject(responsedJsonStr, TeacherInfoBean.class);
                    if (lTeacherInfoBean != null) {
                        switch (lTeacherInfoBean.getCode()) {
                            case 0://成功
                                TeacherInfoBean.DataBean lTeacherInfoBeanData = lTeacherInfoBean.getData();
                                if (lTeacherInfoBeanData != null) {
                                    Glide.with(PersonInfoActivity.this).downloadOnly().load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, lTeacherInfoBeanData.getPhotoPath()).replaceAll("\\\\", "/")).listener(new RequestListener<File>() {// 缓存教师头像
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                            LogUtility.e(TAG, MessageFormat.format("onLoadFailed: {0}", isFirstResource), e);
                                            return true;
                                        }

                                        @Override
                                        public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                            Glide.with(PersonInfoActivity.this).load(resource).apply(AppConfig.CIRCLE_GLIDE_REQUEST_OPTIONS).into(mIvTeacherAvatar);
                                            File cacheFile;
                                            if ((cacheFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR))).exists()) cacheFile.delete();
                                            AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_AVATAR, resource.getAbsolutePath());
                                            return true;
                                        }
                                    }).submit(200, 200);
                                    mTvTelphoneNo.setText(lTeacherInfoBeanData.getTelephone());
                                    mEtTeacherName.setText(lTeacherInfoBeanData.getName());
                                    mEtTeacherName.setSelection(mEtTeacherName.getText().toString().length());
                                    switch (lTeacherInfoBeanData.getSex()) {
                                        case 1:
                                            mRbMale.setChecked(true);
                                            break;
                                        case 2:
                                            mRbFemale.setChecked(true);
                                            break;
                                        default:
                                            mRbMale.setChecked(true);
                                    }
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_ACCOUNT, lTeacherInfoBeanData.getTelephone());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_NAME, lTeacherInfoBeanData.getName());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_SEX, lTeacherInfoBeanData.getSex());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_ID, lTeacherInfoBeanData.getNumber());
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取教师信息失败! {0}:{1}", lTeacherInfoBean.getCode(), lTeacherInfoBean.getMessage()));
                        }
                    }
                }
            });
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 更换教师头像的逻辑-----------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-----------------------------------------------------------
    private RxPermissions mRxPermissions;
    private BottomSheetDialog mBottomSheetDialog;

    @OnClick({R.id.fl_change_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_change_avatar:
                if (mRxPermissions == null) mRxPermissions = new RxPermissions(PersonInfoActivity.this);
                mRxPermissions.requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permissionSD -> {
                    if (permissionSD.granted) {
                        if (mBottomSheetDialog == null) {
                            mBottomSheetDialog = new BottomSheetDialog(PersonInfoActivity.this);
                            View lView = LayoutInflater.from(PersonInfoActivity.this).inflate(R.layout.menu_choose_photo_bottom_sheet, null, false);
                            lView.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {//1.拍照获取图片
                                mBottomSheetDialog.dismiss();
                                mRxPermissions.requestEach(Manifest.permission.CAMERA).subscribe(permissionCamera -> {
                                    if (permissionCamera.granted) {
                                        RxPhotoTool.openCameraImage(PersonInfoActivity.this);
                                    } else if (permissionCamera.shouldShowRequestPermissionRationale) {
                                        RxToast.error("'拍照'依赖于相机权限,请重新授权!");
                                    } else {
                                        new AlertDialog.Builder(PersonInfoActivity.this).setCancelable(false).setIcon(R.drawable.ic_alarm).setTitle("系统设置中授权").setMessage("您拒绝了程序访问相机,请在系统设置中重新授权!").setNegativeButton("取消", (dialog, which) -> {
                                        }).setPositiveButton("确定", (dialog, which) -> {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + Utils.getApp().getPackageName()));
                                            Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }).show();
                                    }
                                });
                            });
                            lView.findViewById(R.id.btn_choose_photo).setOnClickListener(v -> {//2.打开相册选取图片
                                mBottomSheetDialog.dismiss();
                                RxPhotoTool.openLocalImage(PersonInfoActivity.this);
                            });
                            lView.findViewById(R.id.btn_cancel).setOnClickListener(v -> mBottomSheetDialog.dismiss());
                            mBottomSheetDialog.setContentView(lView);
                        }
                        mBottomSheetDialog.show();
                    } else if (permissionSD.shouldShowRequestPermissionRationale) {
                        RxToast.error("'更换头像'依赖于读写存储卡权限,请重新授权!");
                    } else {
                        new AlertDialog.Builder(PersonInfoActivity.this).setCancelable(false).setIcon(R.drawable.ic_alarm).setTitle("系统设置中授权").setMessage("您拒绝了程序访问存储卡,请在系统设置中重新授权!").setNegativeButton("取消", (dialog, which) -> {
                        }).setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + Utils.getApp().getPackageName()));
                            Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }).show();
                    }
                });
                break;
            default:
        }
    }

    private void initUCrop(Uri sourceUri) {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date(System.currentTimeMillis()));
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setCropFrameColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setCircleDimmedLayer(true);
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(options).start(PersonInfoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) initUCrop(RxPhotoTool.imageUriFromCamera);
                break;
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == RESULT_OK) initUCrop(data.getData());
                break;
            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {
                    mImgFilePath = RxPhotoTool.getImageAbsolutePath(PersonInfoActivity.this, UCrop.getOutput(data));
                    Glide.with(PersonInfoActivity.this).load(mImgFilePath).apply(AppConfig.CIRCLE_GLIDE_REQUEST_OPTIONS).into(mIvTeacherAvatar);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    LogUtility.e(TAG, "onActivityResult: ", UCrop.getError(data));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 向接口提交教师数据并缓存在本地的逻辑--------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------

    /**
     * 更新教师信息并实现本地缓存
     */
    private void updateTeacherInfoAndCache() {
        if (RxNetTool.isAvailable(CustomApplication.getContext())) MProgressDialog.showProgress(PersonInfoActivity.this, "上传中...");

        File lChooseTeacherAvatarFile = new File(mImgFilePath);//重新选择的教师头像文件
        String lTeacherName = mEtTeacherName.getText().toString();//重新编辑的教师姓名
        switch (mRgSex.getCheckedRadioButtonId()) {
            case R.id.rb_male:
                mIsMale = true;
                break;
            case R.id.rb_female:
                mIsMale = false;
                break;
            default:
                mIsMale = true;
        }
        String lTeacherSex = mIsMale ? "1" : "2";//重新选择的教师性别

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        requestBuilder.addFormDataPart("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).addFormDataPart("sex", lTeacherSex);
        if (lChooseTeacherAvatarFile.exists()) requestBuilder.addFormDataPart("file", MessageFormat.format("Avatar_{0}.JPG", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)), RequestBody.create(MediaType.parse("image/jpeg"), lChooseTeacherAvatarFile));
        if (!TextUtils.isEmpty(lTeacherName)) requestBuilder.addFormDataPart("name", lTeacherName);
        RequestBody lRequestBody = requestBuilder.build();
        OkHttpEngine.getInstance().sendAsyncUploadFileRequest(PersonInfoActivity.this, MessageFormat.format("{0}/teacherInfo/updataTeacherInfo", AppConfig.SERVER_ADDRESS), lRequestBody, new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                MProgressDialog.dismissProgress();
                new MStatusDialog(PersonInfoActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("操作失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                MProgressDialog.dismissProgress();
                ResponseResultBean lModifyTeacherInfoResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lModifyTeacherInfoResultBean != null) {
                    switch (lModifyTeacherInfoResultBean.getCode()) {
                        case 0://成功
                            new MStatusDialog(PersonInfoActivity.this).show("操作成功", getResources().getDrawable(R.drawable.ic_success));
                            if (lChooseTeacherAvatarFile.exists()) {//更新教师头像文件路径
                                File cacheFile;
                                if ((cacheFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR))).exists()) cacheFile.delete();
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_AVATAR, mImgFilePath);
                            }
                            if (!TextUtils.isEmpty(lTeacherName)) AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_NAME, lTeacherName);//更新教师姓名
                            AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_SEX, mIsMale ? 1 : 2);//更新教师性别
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_LOAD_TEACHER_INFO));//发送EventBus事件更新MineFragment中教师信息
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(AppConfig.getSPUtils().getString(AppConfig.KEY_RONG_USER_ID), AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_NAME), Uri.parse(MessageFormat.format("file://{0}", mImgFilePath))));//FIXME 更新聊天自己的聊天用户信息
                            break;
                        default:
                            RxToast.error(MessageFormat.format("更新个人信息失败! {0}:{1}", lModifyTeacherInfoResultBean.getCode(), lModifyTeacherInfoResultBean.getMessage()));
                    }
                }
            }
        });
    }
}
