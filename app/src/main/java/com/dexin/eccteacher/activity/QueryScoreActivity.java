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
import com.classic.common.MultipleStatusView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.adapter.ExamClassAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ClassScoreBean;
import com.dexin.eccteacher.bean.ExamClassBean;
import com.dexin.eccteacher.bean.GradeClassBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

public class QueryScoreActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_swipe_refresh)
    SwipeRefreshLayout mSrlSwipeRefresh;
    @BindView(R.id.refresh_multiple_status_view)
    MultipleStatusView mRefreshMultipleStatusView;
    @BindView(R.id.ddm_query_class_score)
    DropDownMenu mDdmQueryClassScore;
    private boolean mIsLoadMenuSuccess;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, QueryScoreActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_query_score;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("班级分数");
        //初始化RefreshMultipleStatusView
        mRefreshMultipleStatusView.showLoading();
        mSrlSwipeRefresh.setColorSchemeResources(R.color.register_yellow_red);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSrlSwipeRefresh.setOnRefreshListener(() -> {
//            if (!mIsLoadMenuSuccess)//loadGradeClassData();
            RxToast.success("刷新成功!");
            mSrlSwipeRefresh.setRefreshing(false);
        });
    }

    @Override
    public void initData() {
        loadGradeClassData();
    }

    @Override
    protected void onStop() {
        if (mDdmQueryClassScore.isShowing()) mDdmQueryClassScore.closeMenu();
        super.onStop();
    }


    /**
     * 加载 年级-班级 数据
     */
    private void loadGradeClassData() {
        String urlGradeClass = MessageFormat.format("{0}/adminClassInfo/adminClassAll", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(QueryScoreActivity.this, urlGradeClass, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            private List<GradeClassBean.DataBean> mGradeClassBeanList;//年级-班级 数据

            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'年级-班级'失败!");
                mSrlSwipeRefresh.setRefreshing(false);
                mRefreshMultipleStatusView.showContent();
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                GradeClassBean lGradeClassBean = OkHttpEngine.toObject(responsedJsonStr, GradeClassBean.class);
                if (lGradeClassBean != null) {
                    switch (lGradeClassBean.getCode()) {
                        case 0://成功:
                            mGradeClassBeanList = lGradeClassBean.getData();
                            if ((mGradeClassBeanList != null) && !mGradeClassBeanList.isEmpty()) {
                                loadExamClassData();//FIXME 年级-班级 数据加载完成,开始加载 考试类型 数据
                            } else {
                                RxToast.info("没有'年级-班级'信息!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'年级-班级'失败! {0}:{1}", lGradeClassBean.getCode(), lGradeClassBean.getMessage()));
                    }
                }
            }

            /**
             * 加载 考试类型 数据
             */
            private void loadExamClassData() {
                String urlExamClass = MessageFormat.format("{0}/ExamInfo/getAllExamInfo", AppConfig.SERVER_ADDRESS);
                OkHttpEngine.getInstance().sendAsyncPostRequest(QueryScoreActivity.this, urlExamClass, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
                    private List<ExamClassBean.DataBean> mExamClassDataBeanList;//考试类型 数据
                    private int mClassId = -500, mExamClassId = -500;
                    private String mExamClassName;
                    private ClassScoreAdapter mClassScoreAdapter;
                    private RecyclerView mRvClassScoreDetail;

                    @Override
                    public void onFailure(Call call, Exception e) {
                        RxToast.error("服务错误,获取'考试类型'失败!");
                        mSrlSwipeRefresh.setRefreshing(false);
                        mRefreshMultipleStatusView.showContent();
                    }

                    @Override
                    public void onResponseJson(Call call, String responsedJsonStr) {
                        mSrlSwipeRefresh.setRefreshing(false);
                        mRefreshMultipleStatusView.showContent();
                        ExamClassBean lExamClassBean = OkHttpEngine.toObject(responsedJsonStr, ExamClassBean.class);
                        if (lExamClassBean != null) {
                            switch (lExamClassBean.getCode()) {
                                case 0://成功
                                    mExamClassDataBeanList = lExamClassBean.getData();
                                    if (mExamClassDataBeanList != null && !mExamClassDataBeanList.isEmpty()) {
                                        inflateQueryScoreMenu();//FIXME 年级-班级 和 考试类型 数据都加载完成,开始加载 填充菜单布局
                                    } else {
                                        RxToast.info("没有'考试类型数据'信息!");
                                    }
                                    break;
                                default:
                                    RxToast.error(MessageFormat.format("获取'考试类型'失败! {0}:{1}", lExamClassBean.getCode(), lExamClassBean.getMessage()));
                            }
                        }
                    }

                    /**
                     * 填充 查询分数 的菜单
                     */
                    private void inflateQueryScoreMenu() {
                        //初始化"显示内容"
                        final View lClassScoreDetailContentView = LayoutInflater.from(QueryScoreActivity.this).inflate(R.layout.content_class_score_detail, null, false);
                        mRvClassScoreDetail = lClassScoreDetailContentView.findViewById(R.id.rv_class_score_detail);
                        mRvClassScoreDetail.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                        mClassScoreAdapter = new ClassScoreAdapter();
                        mClassScoreAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                        mClassScoreAdapter.isFirstOnly(false);
                        mClassScoreAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
                            ClassScoreBean.DataBean lDataBean = ((ClassScoreBean.DataBean) baseQuickAdapter.getItem(position));
                            if (lDataBean != null) {
                                ClassScoreBean.DataBean.StudentBean lStudentBean = lDataBean.getStudent();
                                if (lStudentBean != null) {
                                    startActivity(PersonScoreActivity.createIntent(CustomApplication.getContext(), mExamClassId, lStudentBean.getId(), mExamClassName, lStudentBean.getName()));
                                }
                            }
                        });
                        mRvClassScoreDetail.setAdapter(mClassScoreAdapter);

                        //初始化"年级-班级"
                        final RecyclerView lRvGradeClassView = new RecyclerView(QueryScoreActivity.this);
                        lRvGradeClassView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                        lRvGradeClassView.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
                        GradeClassAdapter lGradeClassAdapter = new GradeClassAdapter();
                        lGradeClassAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                        lGradeClassAdapter.isFirstOnly(false);
                        lGradeClassAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
                            lGradeClassAdapter.setFocusPosition(position);
                            GradeClassBean.DataBean lGradeClassDataBean = (GradeClassBean.DataBean) baseQuickAdapter.getItem(position);
                            if (lGradeClassDataBean != null) {
                                mDdmQueryClassScore.setTabText(lGradeClassDataBean.getName());
                                mClassId = lGradeClassDataBean.getId();
                            }
                            mDdmQueryClassScore.closeMenu();
                            queryClassScore();
                        });
                        lRvGradeClassView.setAdapter(lGradeClassAdapter);
                        lGradeClassAdapter.replaceData(mGradeClassBeanList);

                        //初始化"考试类型"
                        final RecyclerView lRvExamClassView = new RecyclerView(QueryScoreActivity.this);
                        lRvExamClassView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                        lRvExamClassView.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
                        ExamClassAdapter lExamClassAdapter = new ExamClassAdapter();
                        lExamClassAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                        lExamClassAdapter.isFirstOnly(false);
                        lExamClassAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
                            lExamClassAdapter.setFocusPosition(position);
                            ExamClassBean.DataBean lExamClassDataBean = (ExamClassBean.DataBean) baseQuickAdapter.getItem(position);
                            if (lExamClassDataBean != null) {
                                mExamClassName = lExamClassDataBean.getExamName();
                                mDdmQueryClassScore.setTabText(mExamClassName);
                                mExamClassId = lExamClassDataBean.getId();
                            }
                            mDdmQueryClassScore.closeMenu();
                            queryClassScore();
                        });
                        lRvExamClassView.setAdapter(lExamClassAdapter);
                        lExamClassAdapter.replaceData(mExamClassDataBeanList);

                        List<View> mPopViewList = new ArrayList<>();
                        mPopViewList.add(lRvGradeClassView);
                        mPopViewList.add(lRvExamClassView);
                        mDdmQueryClassScore.setDropDownMenu(Arrays.asList("年级-班级", "考试类型"), mPopViewList, lClassScoreDetailContentView);
                        mIsLoadMenuSuccess = true;
                    }

                    /**
                     * 查询班级分数
                     */
                    private void queryClassScore() {
                        if (mClassId < 0) {
                            RxToast.error("请选择'年级-班级'!");
                            return;
                        }
                        if (mExamClassId < 0) {
                            RxToast.error("请选择'考试类型'!");
                            return;
                        }
                        String urlClassScore = MessageFormat.format("{0}/studentScoreSum/getAdminClassStudentScore", AppConfig.SERVER_ADDRESS);
                        OkHttpEngine.getInstance().sendAsyncPostRequest(QueryScoreActivity.this, urlClassScore, new FormBody.Builder()
                                .add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN))
                                .add("adminClassInfoId", String.valueOf(mClassId))
                                .add("examId", String.valueOf(mExamClassId))
//                                .add("pageIndex", "1")
//                                .add("pageSize", AppConfig.PAGE_SIZE)
                                .build(), new OkHttpEngine.ResponsedCallback() {
                            private List<ClassScoreBean.DataBean> mEmptyList = new ArrayList<>();

                            @Override
                            public void onFailure(Call call, Exception e) {
                                RxToast.error("服务错误,获取班级学生分数失败!");
                            }

                            @Override
                            public void onResponseJson(Call call, String responsedJsonStr) {
                                ClassScoreBean lClassScoreBean = OkHttpEngine.toObject(responsedJsonStr, ClassScoreBean.class);
                                if (lClassScoreBean != null) {
                                    switch (lClassScoreBean.getCode()) {
                                        case 0:
                                            List<ClassScoreBean.DataBean> lClassScoreDataBeanList = lClassScoreBean.getData();
                                            if ((lClassScoreDataBeanList != null) && !lClassScoreDataBeanList.isEmpty()) {
                                                Map<List<ClassScoreBean.DataBean>, List<ExamClassBean.DataBean>> lListListMap = new HashMap<>();
                                                lListListMap.put(lClassScoreDataBeanList, mExamClassDataBeanList);
                                                EventBus.getDefault().postSticky(lListListMap);//发送"包含全班学生列表"的事件总线
                                                mClassScoreAdapter.replaceData(lClassScoreDataBeanList);
                                            } else {
                                                RxToast.info("无班级学生成绩信息!");
                                                mClassScoreAdapter.replaceData(mEmptyList);
                                            }
                                            break;
                                        default:
                                            RxToast.error(MessageFormat.format("获取班级学生分数失败! {0}:{1}", lClassScoreBean.getCode(), lClassScoreBean.getMessage()));
                                    }
                                }
                            }
                        });
                    }

                    class ClassScoreAdapter extends BaseQuickAdapter<ClassScoreBean.DataBean, BaseViewHolder> {
                        ClassScoreAdapter() {
                            super(R.layout.item_class_score);
                        }

                        @Override
                        protected void convert(BaseViewHolder baseViewHolder, ClassScoreBean.DataBean dataBean) {
                            baseViewHolder.setBackgroundColor(R.id.ll_score_container, ((baseViewHolder.getAdapterPosition() % 2) == 0) ? Color.parseColor("#FFFFFF") : Color.parseColor("#F8F8F8"));
                            ClassScoreBean.DataBean.StudentBean lStudentBean = dataBean.getStudent();
                            baseViewHolder.setText(R.id.tv_student_name, (lStudentBean != null) ? lStudentBean.getName() : "").setText(R.id.tv_total_score, String.valueOf(dataBean.getScoreSum())).setText(R.id.tv_class_ranking, String.valueOf(dataBean.getRankingAdminClass())).setText(R.id.tv_grade_ranking, String.valueOf(dataBean.getRankingGrade()));
                        }
                    }

                    class GradeClassAdapter extends BaseQuickAdapter<GradeClassBean.DataBean, BaseViewHolder> {
                        private int mFocusPosition = -500;

                        GradeClassAdapter() {
                            super(R.layout.item_drop_down_menu);
                        }

                        @Override
                        protected void convert(BaseViewHolder baseViewHolder, GradeClassBean.DataBean dataBean) {
                            TextView mTvGradeClassname = baseViewHolder.getView(R.id.tv_item);
                            mTvGradeClassname.setTextColor((mFocusPosition == baseViewHolder.getAdapterPosition()) ? mContext.getResources().getColor(R.color.register_yellow_red) : mContext.getResources().getColor(R.color.light_black));
                            mTvGradeClassname.setText(dataBean.getName());
                        }

                        void setFocusPosition(int focusPosition) {
                            mFocusPosition = focusPosition;
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
}
