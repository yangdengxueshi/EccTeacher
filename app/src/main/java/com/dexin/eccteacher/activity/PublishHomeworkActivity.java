package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ClassAdapter;
import com.dexin.eccteacher.adapter.GradeAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ClassBean;
import com.dexin.eccteacher.bean.GradeBean;
import com.dexin.eccteacher.bean.PublishHomeworkBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 发布家庭作业Activity
 */
public class PublishHomeworkActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_grade)
    Spinner mSSelectGrade;
    @BindView(R.id.s_select_class)
    Spinner mSSelectClass;
    @BindView(R.id.s_select_course)
    Spinner mSSelectCourse;
    @BindView(R.id.tv_select_finish_time)
    TextView mTvSelectFinishTime;
    @BindView(R.id.et_homework_content)
    EditText mEtHomeworkContent;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, PublishHomeworkActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_publish_homework;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("作业发布");
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
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
    }

    @Override
    public void initData() {
        initGradeDropDown();//获取"年级下拉列表"
        initHomeworkDropDown();//获取"作业下拉列表"
    }


    private TimePickerView mTimePickerView;
    private String mTimeToFinish;

    @OnClick({R.id.tv_select_finish_time, R.id.tv_select_pic, R.id.btn_confirm_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_finish_time:
                if (mTimePickerView == null) {
                    mTimePickerView = new TimePickerBuilder(PublishHomeworkActivity.this, (date, v) -> {
                        mTimeToFinish = RxTimeTool.date2String(date);
                        mTvSelectFinishTime.setText(mTimeToFinish);
                    }).setType(new boolean[]{true, true, true, true, true, true}).setTitleText("请选择完成时间").setSubmitColor(Color.parseColor("#FE6E28")).setCancelColor(Color.GRAY).isDialog(true).build();
                }
                mTimePickerView.show();
                break;
            case R.id.tv_select_pic:
                break;
            case R.id.btn_confirm_publish:
                publishHomework();
                break;
        }
    }


    private int mGradeId = -1, mClassId = -1, mHomeworkId = -1;

    /**
     * 获取"年级下拉列表"
     */
    private void initGradeDropDown() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishHomeworkActivity.this, MessageFormat.format("{0}/ClassInform/getTeacherGradeInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
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
                                GradeAdapter lGradeAdapter = new GradeAdapter(PublishHomeworkActivity.this, R.layout.item_drop_down_menu, lGradeList);
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
     * 获取"班级下啦列表"
     */
    private void initClassDropDown() {
        if (mGradeId < 0) {
            RxToast.info("请先选择年级");
            return;
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishHomeworkActivity.this, MessageFormat.format("{0}/ClassInform/getTeacherAdminClassAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("gradeId", String.valueOf(mGradeId)).build(), new OkHttpEngine.ResponsedCallback() {
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
                                ClassAdapter lClassAdapter = new ClassAdapter(PublishHomeworkActivity.this, R.layout.item_drop_down_menu, lClassList);
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
     * 加载作业下拉列表
     */
    private void initHomeworkDropDown() {
        String urlHomeworklist = MessageFormat.format("{0}/subjectInfo/getSubjectAlls", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishHomeworkActivity.this, urlHomeworklist, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取作业列表失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                PublishHomeworkBean lPublishHomeworkBean = OkHttpEngine.toObject(responsedJsonStr, PublishHomeworkBean.class);
                if (lPublishHomeworkBean != null) {
                    if (Objects.equals(lPublishHomeworkBean.getMessage(), "ok")) {
                        List<PublishHomeworkBean.DataBean> lPublishHomeworkBeanList = lPublishHomeworkBean.getData();
                        if (lPublishHomeworkBeanList != null && !lPublishHomeworkBeanList.isEmpty()) {
                            HomeworkAdapter lHomeworkAdapter = new HomeworkAdapter(PublishHomeworkActivity.this, R.layout.item_drop_down_menu, lPublishHomeworkBeanList);
                            mSSelectCourse.setAdapter(lHomeworkAdapter);
                            mSSelectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    PublishHomeworkBean.DataBean lHomeworkBean = lPublishHomeworkBeanList.get(position);
                                    if (lHomeworkBean != null) mHomeworkId = lHomeworkBean.getId();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else {
                            RxToast.info("服务器上无作业列表!");
                        }
                    } else {
                        RxToast.error(MessageFormat.format("获取作业列表失败! {0}", lPublishHomeworkBean.getMessage()));
                    }
                }
            }

            class HomeworkAdapter extends ArrayAdapter<PublishHomeworkBean.DataBean> {
                private int itemLayoutResId;

                HomeworkAdapter(@NonNull Context context, int resource, @NonNull List<PublishHomeworkBean.DataBean> objects) {
                    super(context, resource, objects);
                    itemLayoutResId = resource;
                }

                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    PublishHomeworkBean.DataBean lHomeworkBean = getItem(position);
                    if (lHomeworkBean != null) {
                        ViewHolder viewHolder;
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                            viewHolder = new ViewHolder();
                            viewHolder.mTvHomework = convertView.findViewById(R.id.tv_item);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }
                        viewHolder.mTvHomework.setText(lHomeworkBean.getName());
                    }
                    return convertView;
                }

                class ViewHolder {
                    TextView mTvHomework;
                }
            }
        });
    }

    /**
     * 发布家庭作业
     */
    private void publishHomework() {
        if (mGradeId < 0) {
            RxToast.info("请选择'年级'!");
            return;
        }
        if (mClassId < 0) {
            RxToast.info("请选择'班级'!");
            return;
        }
        if (mHomeworkId < 0) {
            RxToast.info("请选择'课程'!");
            return;
        }
        String lHomeworkContent = mEtHomeworkContent.getText().toString();
        if (TextUtils.isEmpty(lHomeworkContent)) {
            RxToast.info("请输入'作业内容描述'!");
            return;
        }
        String urlPublishHomework = MessageFormat.format("{0}/ClassInform/addFamilyTask", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(PublishHomeworkActivity.this, urlPublishHomework, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("gradeId", String.valueOf(mGradeId)).add("classId", String.valueOf(mClassId)).add("subjectId", String.valueOf(mHomeworkId)).add("content", lHomeworkContent).add("endTime", mTimeToFinish).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,请重试!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                if (responsedJsonStr.contains("操作成功")) {
                    RxToast.success("发布成功!");
                } else {
                    RxToast.error("发布失败!");
                }
            }
        });
    }
}
