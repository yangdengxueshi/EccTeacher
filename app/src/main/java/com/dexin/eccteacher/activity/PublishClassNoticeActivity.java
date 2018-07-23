package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ClassAdapter;
import com.dexin.eccteacher.adapter.GradeAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ClassBean;
import com.dexin.eccteacher.bean.GradeBean;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

public class PublishClassNoticeActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_grade)
    Spinner mSSelectGrade;
    @BindView(R.id.s_select_class)
    Spinner mSSelectClass;
    @BindView(R.id.s_select_notice_level)
    Spinner mSSelectNoticeLevel;
    @BindView(R.id.et_notice_title)
    EditText mEtNoticeTitle;
    @BindView(R.id.et_notice_content)
    EditText mEtNoticeContent;
    @BindView(R.id.cb_push_notice_or_not)
    CheckBox mCbPushNoticeOrNot;
    private int mGradeId = -1, mClassId = -1, mClassNoticeLevel;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, PublishClassNoticeActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_publish_class_notice;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("通知发布");
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        //初始化'选择班级'Spinner
        mSSelectClass.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mClassId < 0) initClassDropDown();
                    return true;
                default:
            }
            view.performClick();
            return false;
        });
        //初始化'通知级别'Spinner
        mSSelectNoticeLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setVisibility((position == 0) ? View.GONE : View.VISIBLE);
                mSSelectNoticeLevel.setSelected(position != 0);
                mClassNoticeLevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void initData() {
        initGradeDropDown();//获取"年级下拉列表"
    }

    @OnClick({R.id.tv_select_pic, R.id.btn_confirm_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_pic:
                break;
            case R.id.btn_confirm_publish:
                publishClassNotice();
                break;
            default:
        }
    }


    /**
     * 获取"年级下拉列表"
     */
    private void initGradeDropDown() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishClassNoticeActivity.this, MessageFormat.format("{0}/ClassInform/getGradeInfoAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取年级失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                GradeBean lGradeBean = OkHttpEngine.toObject(responsedJsonStr, GradeBean.class);
                if (lGradeBean != null) {
                    switch (lGradeBean.getCode()) {
                        case 0:
                            List<GradeBean.DataBean> lGradeList = lGradeBean.getData();
                            if (lGradeList != null && !lGradeList.isEmpty()) {
                                GradeAdapter lGradeAdapter = new GradeAdapter(PublishClassNoticeActivity.this, R.layout.item_drop_down_menu, lGradeList);
                                mSSelectGrade.setAdapter(lGradeAdapter);
                                mSSelectGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        GradeBean.DataBean lGradeBean = lGradeList.get(position);
                                        if (lGradeBean != null) mGradeId = lGradeBean.getId();
                                        mClassId = -500;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.error("服务器上无年级信息!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'年级'失败! {0}:{1}", lGradeBean.getCode(), lGradeBean.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 获取"班级下拉列表"
     */
    private void initClassDropDown() {
        if (mGradeId < 0) {
            RxToast.info("请先选择 年级");
            return;
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishClassNoticeActivity.this, MessageFormat.format("{0}/ClassInform/getTeacherAdminClassAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("gradeId", String.valueOf(mGradeId)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取班级失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ClassBean lClassBean = OkHttpEngine.toObject(responsedJsonStr, ClassBean.class);
                if (lClassBean != null) {
                    switch (lClassBean.getCode()) {
                        case 0:
                            List<ClassBean.DataBean> lClassList = lClassBean.getData();
                            if ((lClassList != null) && !lClassList.isEmpty()) {
                                ClassAdapter lClassAdapter = new ClassAdapter(PublishClassNoticeActivity.this, R.layout.item_drop_down_menu, lClassList);
                                mSSelectClass.setAdapter(lClassAdapter);
                                mSSelectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        ClassBean.DataBean lClassBean = lClassList.get(position);
                                        if (lClassBean != null) mClassId = lClassBean.getId();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.error("服务器上无班级信息");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'班级'失败! {0}:{1}", lClassBean.getCode(), lClassBean.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 发布班级通知
     */
    private void publishClassNotice() {
        if (mGradeId < 0) {
            RxToast.info("请先选择'年级'!");
            return;
        }
        if (mClassId == -1) {
            RxToast.info("请先选择'班级'!");
            return;
        } else if (mClassId == -500) {
            RxToast.info("请重新选择'班级'!");
            return;
        }
        if (mClassNoticeLevel == 0) {
            RxToast.info("请选择班级通知级别");
            return;
        }
        String lClassNoticeTitle = mEtNoticeTitle.getText().toString();
        if (TextUtils.isEmpty(lClassNoticeTitle)) {
            RxToast.info("请输入通知标题");
            return;
        }
        String lClassNoticeContent = mEtNoticeContent.getText().toString();
        if (TextUtils.isEmpty(lClassNoticeContent)) {
            RxToast.info("请输入通知通知内容描述");
            return;
        }
        int lToPush = mCbPushNoticeOrNot.isChecked() ? 1 : 0;
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishClassNoticeActivity.this, MessageFormat.format("{0}/ClassInform/addClassInforms", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("theme", lClassNoticeTitle).add("gradeId", String.valueOf(mGradeId)).add("classId", String.valueOf(mClassId)).add("content", lClassNoticeContent).add("pushType", String.valueOf(lToPush)).add("informType", String.valueOf(mClassNoticeLevel)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lPublishResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lPublishResultBean != null) {
                    switch (lPublishResultBean.getCode()) {
                        case 0:
                            RxToast.success("发布成功!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("发布失败! {0}:{1}", lPublishResultBean.getCode(), lPublishResultBean.getMessage()));
                    }
                }
            }
        });
    }
}
