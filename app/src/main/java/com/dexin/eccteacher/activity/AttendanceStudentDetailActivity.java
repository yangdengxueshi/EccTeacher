package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.AttendanceStudentBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

public class AttendanceStudentDetailActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.rv_attend_student_detail)
    RecyclerView mRvAttendStudentDetail;
    private AttendStudentAdapter mAttendStudentAdapter;
    private int mGradeClassId, mCourseId;
    private String mTimeToQuery, mStatus;

    public static Intent createIntent(Context context, int gradeClassId, int courseId, String timeToQuery, String status) {
        Intent intent = new Intent(context, AttendanceStudentDetailActivity.class);
        intent.putExtra("gradeClassId", gradeClassId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("timeToQuery", timeToQuery);
        intent.putExtra("status", status);
        return intent;
    }

    @Override
    public void initView() {
        Intent lIntent = getIntent();
        if (lIntent != null) {
            mGradeClassId = lIntent.getIntExtra("gradeClassId", 0);
            mCourseId = lIntent.getIntExtra("courseId", 0);
            mTimeToQuery = lIntent.getStringExtra("timeToQuery");
            mStatus = lIntent.getStringExtra("status");
        }
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        switch (mStatus) {
            case "1":
                mTvCommomTitle.setText("正常签到");
                break;
            case "2":
                mTvCommomTitle.setText("迟到签到");
                break;
            case "3":
                mTvCommomTitle.setText("未签到");
                break;
            default:
        }
        //初始化RecyclerView
        mAttendStudentAdapter = new AttendStudentAdapter();
        mAttendStudentAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAttendStudentAdapter.isFirstOnly(false);
        mRvAttendStudentDetail.setAdapter(mAttendStudentAdapter);
        mRvAttendStudentDetail.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_attendance_student_detail;
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    public void initData() {
        queryAttendanceStudent();
    }

    /**
     * 查询学生列表
     */
    private void queryAttendanceStudent() {
        String urlStudentDetail = MessageFormat.format("{0}/StudentSign/getStudentSignStudent", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(AttendanceStudentDetailActivity.this, urlStudentDetail, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("adminClassId", String.valueOf(mGradeClassId)).add("subjectMinuteId", String.valueOf(mCourseId)).add("time", mTimeToQuery).add("status", String.valueOf(mStatus)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,查询失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                AttendanceStudentBean lAttendanceStudentBean = OkHttpEngine.toObject(responsedJsonStr, AttendanceStudentBean.class);
                if (lAttendanceStudentBean != null) {
                    switch (lAttendanceStudentBean.getCode()) {
                        case 0://成功
                            List<AttendanceStudentBean.DataBean> lAttendStudentBeanList = lAttendanceStudentBean.getData();
                            if ((lAttendStudentBeanList != null) && !lAttendStudentBeanList.isEmpty()) {
                                mAttendStudentAdapter.replaceData(lAttendStudentBeanList);
                            } else {
                                RxToast.info("无相关学生!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("查询失败! {0}:{1}", lAttendanceStudentBean.getCode(), lAttendanceStudentBean.getMessage()));
                    }
                }
            }
        });
    }

    class AttendStudentAdapter extends BaseQuickAdapter<AttendanceStudentBean.DataBean, BaseViewHolder> {
        AttendStudentAdapter() {
            super(R.layout.item_attend_student);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, AttendanceStudentBean.DataBean dataBean) {
            baseViewHolder.setBackgroundColor(R.id.ll_student_container, ((baseViewHolder.getAdapterPosition() % 2) == 0) ? Color.parseColor("#FFFFFF") : Color.parseColor("#F8F8F8"));
            baseViewHolder.setText(R.id.tv_student_name, dataBean.getName()).setText(R.id.tv_student_id, dataBean.getNumber());
        }
    }
}
