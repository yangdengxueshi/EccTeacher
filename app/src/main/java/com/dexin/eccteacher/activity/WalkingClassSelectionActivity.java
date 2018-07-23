package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ClassAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ChooseCourseBean;
import com.dexin.eccteacher.bean.ClassBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

public class WalkingClassSelectionActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_class)
    Spinner mSSelectClass;
    @BindView(R.id.rv_choose_course_detail)
    RecyclerView mRvChooseCourseDetail;
    private ChooseCourseAdapter mChooseCourseAdapter;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, WalkingClassSelectionActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_walking_class_selection;
    }

    @Override
    protected void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("走班选课");
        //初始化RecyclerView
        mChooseCourseAdapter = new ChooseCourseAdapter();
        mChooseCourseAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mChooseCourseAdapter.isFirstOnly(false);
        mRvChooseCourseDetail.setAdapter(mChooseCourseAdapter);
        mRvChooseCourseDetail.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    protected void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    protected void initData() {
        gainClassOfTeacher();
    }


    private void gainClassOfTeacher() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(WalkingClassSelectionActivity.this, MessageFormat.format("{0}/faultMend/getTeacherIdAdminClassAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                new MStatusDialog(WalkingClassSelectionActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("班级拉取失败!", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ClassBean lClassBean = OkHttpEngine.toObject(responsedJsonStr, ClassBean.class);
                if (lClassBean != null) {
                    switch (lClassBean.getCode()) {
                        case 0:
                            List<ClassBean.DataBean> lClassList = lClassBean.getData();
                            if ((lClassList != null) && !lClassList.isEmpty()) {
                                ClassAdapter lClassAdapter = new ClassAdapter(WalkingClassSelectionActivity.this, R.layout.item_drop_down_menu, lClassList);
                                mSSelectClass.setAdapter(lClassAdapter);
                                mSSelectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        ClassBean.DataBean lClassBean = lClassList.get(position);
                                        if (lClassBean != null) loadWalkingClassSelection(lClassBean.getId());
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
     * 根据班级id查看班级选课结果
     *
     * @param classId 班级id
     */
    private void loadWalkingClassSelection(int classId) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(WalkingClassSelectionActivity.this, MessageFormat.format("{0}/studentSubjectMinute/getResult", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("aminClassId", String.valueOf(classId)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                new MStatusDialog(WalkingClassSelectionActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("查询失败!", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ChooseCourseBean lChooseCourseBean = OkHttpEngine.toObject(responsedJsonStr, ChooseCourseBean.class);
                if (lChooseCourseBean != null) {
                    switch (lChooseCourseBean.getCode()) {
                        case 0:
                            List<ChooseCourseBean.DataBean> lChooseCourseList = lChooseCourseBean.getData();
                            if ((lChooseCourseList != null) && !lChooseCourseList.isEmpty()) {
                                mChooseCourseAdapter.replaceData(lChooseCourseList);
                            } else {
                                RxToast.info("未查询到相关数据!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("查询失败! {0}:{1}", lChooseCourseBean.getCode(), lChooseCourseBean.getMessage()));
                    }
                }
            }
        });
    }

    private class ChooseCourseAdapter extends BaseQuickAdapter<ChooseCourseBean.DataBean, BaseViewHolder> {
        ChooseCourseAdapter() {
            super(R.layout.item_choose_course);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ChooseCourseBean.DataBean dataBean) {
            baseViewHolder.setBackgroundColor(R.id.ll_score_container, ((baseViewHolder.getAdapterPosition() % 2) == 0) ? Color.parseColor("#FFFFFF") : Color.parseColor("#F8F8F8"))
                    .setText(R.id.tv_student_name, dataBean.getName()).setText(R.id.tv_course_name, dataBean.getSubjectName()).setText(R.id.tv_choose_status, dataBean.getTypeName());
        }
    }
}
