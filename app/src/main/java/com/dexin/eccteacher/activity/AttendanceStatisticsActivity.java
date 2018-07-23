package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.AttendanceCourseBean;
import com.dexin.eccteacher.bean.AttendanceGradeClassBean;
import com.dexin.eccteacher.bean.QueryResultBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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

public class AttendanceStatisticsActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_grade_class)
    Spinner mSSelectGradeClass;
    @BindView(R.id.s_select_course)
    Spinner mSSelectCourse;
    @BindView(R.id.tv_select_date)
    TextView mTvSelectDate;
    @BindView(R.id.pc_attendance_rate)
    PieChart mPcAttendanceRate;
    @BindView(R.id.tv_attend_normal)
    TextView mTvAttendNormal;
    @BindView(R.id.tv_attend_late)
    TextView mTvAttendLate;
    @BindView(R.id.tv_attend_not)
    TextView mTvAttendNot;
    private TimePickerView mTimePickerView;
    private int mGradeClassId = -1, mCourseId = -1;
    private boolean mIsQuerySuccess;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, AttendanceStatisticsActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_attendance_statistics;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("考勤统计");
        //饼图初始化
        mPcAttendanceRate.setUsePercentValues(true);//设置使用百分比数值,否则直接显示 数量%
//        {//描述
        Description lDescription = mPcAttendanceRate.getDescription();
        lDescription.setEnabled(false);
//            lDescription.setTextColor(R.color.register_yellow_red);
//            lDescription.setText("走班考勤");
//        }
//        mPcAttendanceRate.setExtraOffsets(50, 0, 0, 0);//设置"饼图"的偏移量
        mPcAttendanceRate.setDragDecelerationFrictionCoef(0.95F);//设置拖动减速摩擦系数(越小越难拖动)
//        mPcAttendanceRate.setCenterTextTypeface();//设置中心文本字体
        {//中心洞
            mPcAttendanceRate.setDrawHoleEnabled(true);
//            mPcAttendanceRate.setHoleColor(Color.WHITE);
            mPcAttendanceRate.setHoleRadius(58);
        }
        {
//            mPcAttendanceRate.setTransparentCircleColor(Color.WHITE);
            mPcAttendanceRate.setTransparentCircleAlpha(110);
            mPcAttendanceRate.setTransparentCircleRadius(61);
        }
        mPcAttendanceRate.setDrawCenterText(true);
        {
            mPcAttendanceRate.setRotationEnabled(true);
//            mPcAttendanceRate.setRotationAngle(0);
        }

//        mPcAttendanceRate.setEntryLabelColor(Color.YELLOW);

        mPcAttendanceRate.setHighlightPerTapEnabled(true);
        mPcAttendanceRate.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                LogUtility.d(TAG, "value: " + entry.getY() + "    index: " + highlight.getX() + "    DataSet index: " + highlight.getDataSetIndex());

                entry.getX();
                entry.getY();

                highlight.getX();
                highlight.getY();
                highlight.getDataIndex();
                highlight.getDataSetIndex();
                highlight.getStackIndex();
            }

            @Override
            public void onNothingSelected() {
            }
        });
        mPcAttendanceRate.animateXY(1500, 1500);

        //图例
        Legend lLegend = mPcAttendanceRate.getLegend();
        lLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        lLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lLegend.setForm(Legend.LegendForm.CIRCLE);
        lLegend.setDrawInside(true);
        lLegend.setXOffset(2);
        lLegend.setYOffset(1);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    public void initData() {
        initSpinner();//初始化Spinner
    }


    /**
     * 初始化Spinner
     */
    private void initSpinner() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(AttendanceStatisticsActivity.this, MessageFormat.format("{0}/StudentSign/getAminClassAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'年级-班级'列表失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                AttendanceGradeClassBean lAttendanceGradeClassBean = OkHttpEngine.toObject(responsedJsonStr, AttendanceGradeClassBean.class);
                if (lAttendanceGradeClassBean != null) {
                    switch (lAttendanceGradeClassBean.getCode()) {
                        case 0://成功:
                            List<AttendanceGradeClassBean.DataBean> lGradeClassList = lAttendanceGradeClassBean.getData();
                            if ((lGradeClassList != null) && !lGradeClassList.isEmpty()) {
                                lGradeClassList.add(0, new AttendanceGradeClassBean.DataBean(-1, -1, "班级选择"));
                                GradeClassAdapter lGradeClassAdapter = new GradeClassAdapter(AttendanceStatisticsActivity.this, R.layout.item_drop_down_menu, lGradeClassList);
                                mSSelectGradeClass.setAdapter(lGradeClassAdapter);
                                mSSelectGradeClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        AttendanceGradeClassBean.DataBean lGradeClassBean = lGradeClassList.get(position);
                                        if (lGradeClassBean != null) mGradeClassId = lGradeClassBean.getId();
                                        queryAttendanceRate();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("服务器上未添加'年级-班级'数据!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'年级-班级'列表失败! {0}:{1}", lAttendanceGradeClassBean.getCode(), lAttendanceGradeClassBean.getMessage()));
                    }
                }
            }

            class GradeClassAdapter extends ArrayAdapter<AttendanceGradeClassBean.DataBean> {
                private int itemLayoutResId;

                GradeClassAdapter(@NonNull Context context, int resource, @NonNull List<AttendanceGradeClassBean.DataBean> dataBeanList) {
                    super(context, resource, dataBeanList);
                    itemLayoutResId = resource;
                }

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    AttendanceGradeClassBean.DataBean lGradeClassBean = getItem(position);
                    if (lGradeClassBean != null) {
                        ViewHolder lViewHolder;
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                            lViewHolder = new ViewHolder();
                            lViewHolder.mTvGradeClass = convertView.findViewById(R.id.tv_item);
                            convertView.setTag(lViewHolder);
                        } else {
                            lViewHolder = (ViewHolder) convertView.getTag();
                        }
                        lViewHolder.mTvGradeClass.setText(lGradeClassBean.getName());
                    }
                    return convertView;
                }

                class ViewHolder {
                    TextView mTvGradeClass;
                }
            }
        });


        OkHttpEngine.getInstance().sendAsyncPostRequest(AttendanceStatisticsActivity.this, MessageFormat.format("{0}/StudentSign/getGradeSubjectMinuteAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'课程'列表失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                AttendanceCourseBean lAttendanceCourseBean = OkHttpEngine.toObject(responsedJsonStr, AttendanceCourseBean.class);
                if (lAttendanceCourseBean != null) {
                    switch (lAttendanceCourseBean.getCode()) {
                        case 0://成功:
                            List<AttendanceCourseBean.DataBean> lCourseList = lAttendanceCourseBean.getData();
                            if ((lCourseList != null) && !lCourseList.isEmpty()) {
                                lCourseList.add(0, new AttendanceCourseBean.DataBean(-1, "课程选择"));
                                CourseAdapter lCourseAdapter = new CourseAdapter(AttendanceStatisticsActivity.this, R.layout.item_drop_down_menu, lCourseList);
                                mSSelectCourse.setAdapter(lCourseAdapter);
                                mSSelectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        AttendanceCourseBean.DataBean lCourseBean = lCourseList.get(position);
                                        if (lCourseBean != null) mCourseId = lCourseBean.getId();
                                        queryAttendanceRate();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("服务器上未添加'课程'数据!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'课程'列表失败! {0}:{1}", lAttendanceCourseBean.getCode(), lAttendanceCourseBean.getMessage()));
                    }
                }
            }

            class CourseAdapter extends ArrayAdapter<AttendanceCourseBean.DataBean> {
                private int itemLayoutResId;

                CourseAdapter(@NonNull Context context, int resource, @NonNull List<AttendanceCourseBean.DataBean> objects) {
                    super(context, resource, objects);
                    itemLayoutResId = resource;
                }

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    AttendanceCourseBean.DataBean lCourseBean = getItem(position);
                    if (lCourseBean != null) {
                        ViewHolder lViewHolder;
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                            lViewHolder = new ViewHolder();
                            lViewHolder.mTvCourseName = convertView.findViewById(R.id.tv_item);
                            convertView.setTag(lViewHolder);
                        } else {
                            lViewHolder = (ViewHolder) convertView.getTag();
                        }
                        lViewHolder.mTvCourseName.setText(lCourseBean.getName());
                    }
                    return convertView;
                }

                class ViewHolder {
                    TextView mTvCourseName;
                }
            }
        });
        mTvSelectDate.setText(RxTimeTool.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)));
        mTvSelectDate.setOnClickListener(view -> {
            if (mTimePickerView == null) {
                mTimePickerView = new TimePickerBuilder(AttendanceStatisticsActivity.this, (date, v) -> {
                    mTvSelectDate.setText(RxTimeTool.date2String(date, new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)));
                    queryAttendanceRate();
                }).setType(new boolean[]{true, true, true, false, false, false}).setTitleText("请选择查询日期").setSubmitColor(Color.parseColor("#FE6E28")).setCancelColor(Color.GRAY).isDialog(true).build();
            }
            mTimePickerView.show();
        });
    }

    private void queryAttendanceRate() {
        if (mGradeClassId < 0) {
            RxToast.info("请选择'班级'!");
            return;
        }
        if (mCourseId < 0) {
            RxToast.info("请选择'课程'!");
            return;
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(AttendanceStatisticsActivity.this, MessageFormat.format("{0}/StudentSign/getStudentSigns", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("adminClassId", String.valueOf(mGradeClassId)).add("subjectMinuteId", String.valueOf(mCourseId)).add("time", MessageFormat.format("{0} 00:00:00", mTvSelectDate.getText().toString())).build(), new OkHttpEngine.ResponsedCallback() {
            private List<PieEntry> mPieEntryList = new ArrayList<>();

            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,查询失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                LogUtility.d(TAG, "onResponseJson: " + responsedJsonStr);
                mIsQuerySuccess = false;
                QueryResultBean lQueryResultBean = OkHttpEngine.toObject(responsedJsonStr, QueryResultBean.class);
                if (lQueryResultBean != null) {
                    switch (lQueryResultBean.getCode()) {
                        case 0://成功
                            QueryResultBean.DataBean lResultBean = lQueryResultBean.getData();
                            if (lResultBean != null) {
                                mIsQuerySuccess = true;
                                int normalNum = lResultBean.getNormalNum(), lateNum = lResultBean.getTimeOutNum(), notNum = lResultBean.getNotSignInNum();
                                mPcAttendanceRate.setCenterText(MessageFormat.format("总人数\n{0}人", lResultBean.getSum()));
                                mPieEntryList.clear();
                                mPieEntryList.add(new PieEntry(normalNum, "正常"));
                                mPieEntryList.add(new PieEntry(lateNum, "迟到"));
                                mPieEntryList.add(new PieEntry(notNum, "未到"));
                                PieDataSet lPieDataSet = new PieDataSet(mPieEntryList, "");//考勤统计
                                lPieDataSet.setSliceSpace(3);
                                lPieDataSet.setSelectionShift(5);
                                lPieDataSet.setColors(Color.parseColor("#1594FF"), Color.parseColor("#F79441"), Color.parseColor("#E64365"));
                                lPieDataSet.setValueLineVariableLength(true);
                                lPieDataSet.setValueLineColor(Color.GRAY);
                                lPieDataSet.setValueLinePart1OffsetPercentage(80);
                                lPieDataSet.setValueLinePart1Length(0.2F);
                                lPieDataSet.setValueLinePart2Length(0.6F);
                                lPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                                List<Integer> valueColorList = new ArrayList<>();
                                valueColorList.add(Color.parseColor("#1594FF"));
                                valueColorList.add(Color.parseColor("#F79441"));
                                valueColorList.add(Color.parseColor("#E64365"));
                                PieData lPieData = new PieData(lPieDataSet);
                                lPieData.setValueFormatter(new PercentFormatter());
                                lPieData.setValueTextColors(valueColorList);
                                mPcAttendanceRate.setData(lPieData);
                                mPcAttendanceRate.highlightValues(null);
                                mPcAttendanceRate.spin(1000, mPcAttendanceRate.getRotationAngle(), mPcAttendanceRate.getRotationAngle() + 360, Easing.EasingOption.EaseInCubic);
                                mPcAttendanceRate.invalidate();

                                mTvAttendNormal.setText(MessageFormat.format("正常签到人数\n{0} 人", normalNum));
                                mTvAttendLate.setText(MessageFormat.format("迟到签到人数\n{0} 人", lateNum));
                                mTvAttendNot.setText(MessageFormat.format("未签到人数\n{0} 人", notNum));
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("查询失败! {0}:{1}", lQueryResultBean.getCode(), lQueryResultBean.getMessage()));
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "查询具体学生列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    @OnClick({R.id.tv_attend_normal, R.id.tv_attend_late, R.id.tv_attend_not})
    public void onViewClicked(View view) {
        if (!mIsQuerySuccess) {
            RxToast.info("请成功执行查询后再操作!");
            return;
        }
        String status = "";
        switch (view.getId()) {
            case R.id.tv_attend_normal:
                status = "1";
                break;
            case R.id.tv_attend_late:
                status = "2";
                break;
            case R.id.tv_attend_not:
                status = "3";
                break;
        }
        if (TextUtils.isEmpty(status)) return;
        startActivity(AttendanceStudentDetailActivity.createIntent(CustomApplication.getContext(), mGradeClassId, mCourseId, MessageFormat.format("{0} 00:00:00", mTvSelectDate.getText().toString()), status));
    }
}
