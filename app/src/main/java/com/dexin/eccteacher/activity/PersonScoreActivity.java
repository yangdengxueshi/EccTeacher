package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.adapter.ExamClassAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ClassScoreBean;
import com.dexin.eccteacher.bean.ExamClassBean;
import com.dexin.eccteacher.bean.PersonScoreBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MProgressDialog;
import com.vondear.rxtool.view.RxToast;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

public class PersonScoreActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_swipe_refresh)
    SwipeRefreshLayout mSrlSwipeRefresh;
    @BindView(R.id.ddm_query_person_score)
    DropDownMenu mDdmQueryPersonScore;
    private int mExamClassId = -500, mStudentId = -500;
    private String examClassName, studentName;

    @Override
    protected void onStop() {
        if (mDdmQueryPersonScore.isShowing()) mDdmQueryPersonScore.closeMenu();
        super.onStop();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_person_score;
    }

    @Override
    public void initView() {
        Intent lIntent = getIntent();
        if (lIntent != null) {
            mExamClassId = lIntent.getIntExtra("examClassId", -500);
            mStudentId = lIntent.getIntExtra("studentId", -500);
            examClassName = lIntent.getStringExtra("examClassName");
            studentName = lIntent.getStringExtra("studentName");
        }
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("学生分数");
        //初始化SmartRefreshLayout
        mSrlSwipeRefresh.setColorSchemeResources(R.color.register_yellow_red);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSrlSwipeRefresh.setOnRefreshListener(() -> {
//            if (!mIsLoadMenuSuccess)//loadPersonScoreData();
            RxToast.success("刷新成功!");
            mSrlSwipeRefresh.setRefreshing(false);
        });
    }

    @Override
    public void initData() {
        loadPersonScoreData();
    }

    @NonNull
    public static Intent createIntent(Context context, int examClassId, int studentId, String examClassName, String studentName) {
        Intent intent = new Intent(context, PersonScoreActivity.class);
        intent.putExtra("examClassId", examClassId);
        intent.putExtra("studentId", studentId);
        intent.putExtra("examClassName", examClassName);
        intent.putExtra("studentName", studentName);
        return intent;
    }


    private List<ClassScoreBean.DataBean> mClassScoreDataBeanList;
    private List<ExamClassBean.DataBean> mExamClassDataBeanList;

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onGradeClassStickyEvent(Map<List<ClassScoreBean.DataBean>, List<ExamClassBean.DataBean>> listListMap) {
        Set<Map.Entry<List<ClassScoreBean.DataBean>, List<ExamClassBean.DataBean>>> lEntrySet = listListMap.entrySet();
        for (Map.Entry<List<ClassScoreBean.DataBean>, List<ExamClassBean.DataBean>> lListListEntry : lEntrySet) {
            mClassScoreDataBeanList = lListListEntry.getKey();
            mExamClassDataBeanList = lListListEntry.getValue();
        }
    }


    private RecyclerView mRvPersonScoreDetail;

    /**
     * 加载"学生个人分数详情"
     */
    private void loadPersonScoreData() {
        MProgressDialog.showProgress(PersonScoreActivity.this, "");
        if (mClassScoreDataBeanList == null) {
            MProgressDialog.dismissProgress();
            RxToast.error("加载'班级学生列表'失败!");
            return;
        }
        if (mExamClassDataBeanList == null) {
            MProgressDialog.dismissProgress();
            RxToast.error("加载'考试类型列表'失败!");
            return;
        }

        //初始化"显示内容"
        final View lPersonScoreDetailContentView = LayoutInflater.from(PersonScoreActivity.this).inflate(R.layout.content_person_score_detail, null, false);
        mRvPersonScoreDetail = lPersonScoreDetailContentView.findViewById(R.id.rv_person_score_detail);
        mRvPersonScoreDetail.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));

        //初始化"学生姓名列表"
        final RecyclerView lRvPersonNameView = new RecyclerView(PersonScoreActivity.this);
        lRvPersonNameView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        lRvPersonNameView.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
        PersonNameAdapter lPersonNameAdapter = new PersonNameAdapter();
        lPersonNameAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        lPersonNameAdapter.isFirstOnly(false);
        lPersonNameAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            lPersonNameAdapter.setFocusPosition(position);
            ClassScoreBean.DataBean lDataBean = (ClassScoreBean.DataBean) baseQuickAdapter.getItem(position);
            if (lDataBean != null) {
                ClassScoreBean.DataBean.StudentBean lStudentBean = lDataBean.getStudent();
                if (lStudentBean != null) {
                    mDdmQueryPersonScore.setTabText(lStudentBean.getName());
                    mStudentId = lStudentBean.getId();
                }
            }
            mDdmQueryPersonScore.closeMenu();
            queryPersonScore();
        });
        lRvPersonNameView.setAdapter(lPersonNameAdapter);
        lPersonNameAdapter.replaceData(mClassScoreDataBeanList);

        //初始化"考试类型"
        final RecyclerView lRvExamClassView = new RecyclerView(PersonScoreActivity.this);
        lRvExamClassView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        lRvExamClassView.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
        ExamClassAdapter lExamClassAdapter = new ExamClassAdapter();
        lExamClassAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        lExamClassAdapter.isFirstOnly(false);
        lExamClassAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            lExamClassAdapter.setFocusPosition(position);
            ExamClassBean.DataBean lExamClassDataBean = (ExamClassBean.DataBean) baseQuickAdapter.getItem(position);
            if (lExamClassDataBean != null) {
                mDdmQueryPersonScore.setTabText(lExamClassDataBean.getExamName());
                mExamClassId = lExamClassDataBean.getId();
            }
            mDdmQueryPersonScore.closeMenu();
            queryPersonScore();
        });
        lRvExamClassView.setAdapter(lExamClassAdapter);
        lExamClassAdapter.replaceData(mExamClassDataBeanList);

        List<View> mPopViewList = new ArrayList<>();
        mPopViewList.add(lRvPersonNameView);
        mPopViewList.add(lRvExamClassView);
        mDdmQueryPersonScore.setDropDownMenu(Arrays.asList(studentName, examClassName), mPopViewList, lPersonScoreDetailContentView);

        queryPersonScore();
    }

    /**
     * 查询学生个人成绩
     */
    private void queryPersonScore() {
        if (mExamClassId < 0) {
            RxToast.error("请选择'考试类型'!");
            return;
        }
        if (mStudentId < 0) {
            RxToast.error("请选择'学生'!");
            return;
        }
        String urlGradeClass = MessageFormat.format("{0}/ScStudentScore/getStudentScoreApp", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(PersonScoreActivity.this, urlGradeClass, new FormBody.Builder()
                .add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN))
                .add("examId", String.valueOf(mExamClassId))
                .add("studentId", String.valueOf(mStudentId))
                .build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'学生分数'失败!");
                mSrlSwipeRefresh.setRefreshing(false);
                MProgressDialog.dismissProgress();
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                MProgressDialog.dismissProgress();
                PersonScoreBean lPersonScoreBean = OkHttpEngine.toObject(responsedJsonStr, PersonScoreBean.class);
                if (lPersonScoreBean != null) {
                    switch (lPersonScoreBean.getCode()) {
                        case 0://成功:
                            List<PersonScoreBean.DataBean> lPersonScoreBeanDataList = lPersonScoreBean.getData();
                            if ((lPersonScoreBeanDataList != null) && !lPersonScoreBeanDataList.isEmpty()) {
                                PersonScoreAdapter lPersonScoreAdapter = new PersonScoreAdapter();
                                lPersonScoreAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                                lPersonScoreAdapter.isFirstOnly(false);
                                mRvPersonScoreDetail.setAdapter(lPersonScoreAdapter);
                                lPersonScoreAdapter.replaceData(lPersonScoreBeanDataList);
                            } else {
                                RxToast.info("无此学生成绩信息");
                                mRvPersonScoreDetail.setAdapter(null);
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'学生分数'失败! {0}:{1}", lPersonScoreBean.getCode(), lPersonScoreBean.getMessage()));
                    }
                }
            }
        });
    }

    private class PersonNameAdapter extends BaseQuickAdapter<ClassScoreBean.DataBean, BaseViewHolder> {
        private int mFocusPosition = -500;

        PersonNameAdapter() {
            super(R.layout.item_drop_down_menu);
        }

        void setFocusPosition(int focusPosition) {
            mFocusPosition = focusPosition;
            notifyDataSetChanged();
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ClassScoreBean.DataBean dataBean) {
            ClassScoreBean.DataBean.StudentBean lStudentBean = dataBean.getStudent();
            if (lStudentBean != null) {
                TextView mTvPersonName = baseViewHolder.getView(R.id.tv_item);
                mTvPersonName.setTextColor((mFocusPosition == baseViewHolder.getAdapterPosition()) ? mContext.getResources().getColor(R.color.register_yellow_red) : mContext.getResources().getColor(R.color.light_black));
                mTvPersonName.setText(lStudentBean.getName());
            }
        }
    }

    private class PersonScoreAdapter extends BaseQuickAdapter<PersonScoreBean.DataBean, BaseViewHolder> {
        PersonScoreAdapter() {
            super(R.layout.item_person_score);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, PersonScoreBean.DataBean dataBean) {
            baseViewHolder.setBackgroundColor(R.id.ll_score_container, ((baseViewHolder.getAdapterPosition() % 2) == 0) ? Color.parseColor("#FFFFFF") : Color.parseColor("#F8F8F8"));
            baseViewHolder.setText(R.id.tv_subject_name, dataBean.getSubjectName()).setText(R.id.tv_score, String.valueOf(dataBean.getScore())).setText(R.id.tv_class_ranking, String.valueOf(dataBean.getRankingAdminClass())).setText(R.id.tv_grade_ranking, String.valueOf(dataBean.getRankingGrade()));
        }
    }
}
