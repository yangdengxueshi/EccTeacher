package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.GradeBean;
import com.dexin.eccteacher.bean.RankingBean;
import com.dexin.eccteacher.custom_view.CustomPopupWindow;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 班级评分 Activity
 */
public class MarkingActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;
    @BindView(R.id.s_select_grade)
    Spinner mSSelectGrade;
    @BindView(R.id.tv_select_date)
    TextView mTvSelectDate;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_ranking_list)
    RecyclerView mRvRanking;
    private RankingAdapter mRankingAdapter;

    @Override
    protected void onDestroy() {
        if (mCustomPopupWindow != null) mCustomPopupWindow.dismiss();
        super.onDestroy();
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, MarkingActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_marking;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("班级评分");
        mTvMenu.setVisibility(View.VISIBLE);
        mTvMenu.setText("全校 ");
        mTvSelectDate.setText(RxTimeTool.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)));
        //初始化RecyclerView
        mRankingAdapter = new RankingAdapter();
        mRankingAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRankingAdapter.isFirstOnly(false);
        mRvRanking.setAdapter(mRankingAdapter);
        mRvRanking.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                queryRanking();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                queryRanking();
            }
        });
        mRankingAdapter.setOnItemClickListener((adapter, view, position) -> {
            RankingBean.DataBean lRankingBean = (RankingBean.DataBean) adapter.getItem(position);
            if (lRankingBean != null) startActivity(MarkingDetailActivity.createIntent(CustomApplication.getContext(), lRankingBean.getAdminClassName(), lRankingBean.getAdminClassId(), MessageFormat.format("{0} 00:00:00", mTvSelectDate.getText().toString())));
        });
    }

    @Override
    public void initData() {
        queryRanking();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marking, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_mark:
                startActivity(DoMarkActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private CustomPopupWindow mCustomPopupWindow;
    private TimePickerView mTimePickerView;

    @OnClick({R.id.tv_menu, R.id.tv_select_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_menu:
                if (mCustomPopupWindow == null) {
                    mCustomPopupWindow = new CustomPopupWindow.Builder().setContext(MarkingActivity.this).setHeight(260).setWidth(300).setContentView(R.layout.menu_ranking).setOutsideCancelable(true).build();
                    TextView lTvCampus = (TextView) mCustomPopupWindow.findViewById(R.id.tv_campus);
                    TextView lTvGrade = (TextView) mCustomPopupWindow.findViewById(R.id.tv_grade);
                    lTvCampus.findViewById(R.id.tv_campus).setOnClickListener(v -> {
                        lTvCampus.setTextColor(Color.parseColor("#FE6E28"));
                        lTvGrade.setTextColor(Color.parseColor("#000000"));
                        mTvMenu.setText("全校 ");
                        mCustomPopupWindow.dismiss();
                        mSSelectGrade.setVisibility(View.GONE);
                        mGradeId = -1;
                    });
                    lTvGrade.findViewById(R.id.tv_grade).setOnClickListener(v -> {
                        lTvCampus.setTextColor(Color.parseColor("#000000"));
                        lTvGrade.setTextColor(Color.parseColor("#FE6E28"));
                        mTvMenu.setText("年级 ");
                        mCustomPopupWindow.dismiss();
                        mSSelectGrade.setVisibility(View.VISIBLE);
                        loadGradeData();
                    });
                }
                mCustomPopupWindow.showAsDropDown(mTvMenu, 0, 50, Gravity.BOTTOM);
                break;
            case R.id.tv_select_date:
                if (mTimePickerView == null) {
                    mTimePickerView = new TimePickerBuilder(MarkingActivity.this, (date, v) -> {
                        mTvSelectDate.setText(RxTimeTool.date2String(date, new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)));
                        mPageIndex = 1;
                        queryRanking();
                    }).setType(new boolean[]{true, true, true, false, false, false}).setTitleText("请选择查询日期").setSubmitColor(Color.parseColor("#FE6E28")).setCancelColor(Color.GRAY).isDialog(true).build();
                }
                mTimePickerView.show();
                break;
            default:
        }
    }


    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    //---------------------------------------------------------FIXME 加载"年级数据"模块------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private int mGradeId = -1;

    /**
     * 加载年级数据
     */
    private void loadGradeData() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(MarkingActivity.this, MessageFormat.format("{0}/ClassInform/getGradeInfoAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'年级列表'失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                GradeBean lGradeBean = OkHttpEngine.toObject(responsedJsonStr, GradeBean.class);
                if (lGradeBean != null) {
                    switch (lGradeBean.getCode()) {
                        case 0://成功
                            List<GradeBean.DataBean> lGradeList = lGradeBean.getData();
                            if ((lGradeList != null) && !lGradeList.isEmpty()) {
                                lGradeList.add(0, new GradeBean.DataBean(Integer.MAX_VALUE, "年级选择"));
                                GradeAdapter lGradeAdapter = new GradeAdapter(MarkingActivity.this, R.layout.item_drop_down_menu, lGradeList);
                                mSSelectGrade.setAdapter(lGradeAdapter);
                                mSSelectGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        GradeBean.DataBean lGradeBean = lGradeList.get(position);
                                        if (lGradeBean != null) mGradeId = lGradeBean.getId();
                                        mPageIndex = 1;
                                        queryRanking();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("服务器上无年级列表!");
                            }
                            break;
                        default:
                            RxToast.error("获取'年级列表'失败! " + lGradeBean.getCode() + ":" + lGradeBean.getMessage());
                    }
                }
            }

            class GradeAdapter extends ArrayAdapter<GradeBean.DataBean> {
                private int itemLayoutResId;

                GradeAdapter(@NonNull Context context, int resource, @NonNull List<GradeBean.DataBean> dataBeanList) {
                    super(context, resource, dataBeanList);
                    itemLayoutResId = resource;
                }

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    GradeBean.DataBean lGradeClassBean = getItem(position);
                    if (lGradeClassBean != null) {
                        ViewHolder lViewHolder;
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                            lViewHolder = new ViewHolder();
                            lViewHolder.mTvGradeOrClass = convertView.findViewById(R.id.tv_item);
                            convertView.setTag(lViewHolder);
                        } else {
                            lViewHolder = (ViewHolder) convertView.getTag();
                        }
                        lViewHolder.mTvGradeOrClass.setText(lGradeClassBean.getName());
                    }
                    return convertView;
                }

                class ViewHolder {
                    TextView mTvGradeOrClass;
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "查询德育排名"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mRankingResponsedCallback;

    private void queryRanking() {
        if (mGradeId == Integer.MAX_VALUE) {
            RxToast.error("请选择查询年级!");
            mSrlSmartRefresh.finishRefresh(false);
            mSrlSmartRefresh.finishLoadMore(false);
            return;//代表"年级选择"
        }
        FormBody lFormBody;
        if (mGradeId < 0) {//代表查询全校排名
            lFormBody = new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("time", MessageFormat.format("{0} 00:00:00", mTvSelectDate.getText().toString())).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build();
        } else {//代表查询年级排名
            lFormBody = new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("time", MessageFormat.format("{0} 00:00:00", mTvSelectDate.getText().toString())).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).add("gradeId", String.valueOf(mGradeId)).build();
        }
        if (mRankingResponsedCallback == null) {
            mRankingResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                private List<RankingBean.DataBean> mEmptyList = new ArrayList<>();

                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,查询评分排名失败!");
                    mSrlSmartRefresh.finishLoadMore(false);//加载失败
                    mSrlSmartRefresh.finishRefresh(false);//刷新失败
                    mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    if (mPageIndex == 1) {//刷新成功  或   首次加载成功
                        mSrlSmartRefresh.finishRefresh(true);
                        mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                    }
                    RankingBean lRankingBean = OkHttpEngine.toObject(responsedJsonStr, RankingBean.class);
                    if (lRankingBean != null) {
                        switch (lRankingBean.getCode()) {
                            case 0://成功:
                                RankingBean.PageObjectBean lPageObjectBean = lRankingBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<RankingBean.DataBean> lLRankingBeanDataList = lRankingBean.getData();
                                    if ((lLRankingBeanDataList != null) && !lLRankingBeanDataList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mRankingAdapter.replaceData(lLRankingBeanDataList);
                                        } else {
                                            mRankingAdapter.addData(lLRankingBeanDataList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        mRankingAdapter.replaceData(mEmptyList);
                                        RxToast.info("未查询到有效数据!");
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error("查询评分排名失败! " + lRankingBean.getCode() + ":" + lRankingBean.getMessage());
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(MarkingActivity.this, MessageFormat.format("{0}/gradePoint/getAdminClassGradePointSum", AppConfig.SERVER_ADDRESS), lFormBody, mRankingResponsedCallback);
    }

    class RankingAdapter extends BaseQuickAdapter<RankingBean.DataBean, BaseViewHolder> {
        RankingAdapter() {
            super(R.layout.item_roral_ranking);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, RankingBean.DataBean dataBean) {
            baseViewHolder.setBackgroundColor(R.id.ll_ranking_container, ((baseViewHolder.getAdapterPosition() % 2) == 0) ? Color.parseColor("#FFFFFF") : Color.parseColor("#F8F8F8"));
            baseViewHolder.setText(R.id.tv_ranking, String.valueOf(dataBean.getRanking())).setText(R.id.tv_class, dataBean.getAdminClassName()).setText(R.id.tv_roral_total_score, String.valueOf(dataBean.getScoreSum()));
        }
    }
}
