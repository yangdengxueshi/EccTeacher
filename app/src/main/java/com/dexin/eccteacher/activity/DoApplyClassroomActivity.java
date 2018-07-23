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
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ClassroomBean;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.bean.TeachBuildingBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

public class DoApplyClassroomActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_teaching_building)
    Spinner mSSelectTeachingBuilding;
    @BindView(R.id.s_select_class_room)
    Spinner mSSelectClassRoom;
    @BindView(R.id.et_apply_usage_detail)
    EditText mEtApplyUsageDetail;
    @BindView(R.id.tv_select_start_time)
    TextView mTvSelectStartTime;
    @BindView(R.id.tv_select_end_time)
    TextView mTvSelectEndTime;
    private int mTeachBuildingId = -1, mClassroomId = -1;
    private TimePickerView mTimePickerView;
    private int mTimeType = -500;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, DoApplyClassroomActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_do_apply_classroom;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("申请教室");
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSSelectClassRoom.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mClassroomId < 0) initClassroomDropDown();
                    return true;
                default:
            }
            view.performClick();
            return false;
        });
    }

    @Override
    public void initData() {
        initTeachBuilding();
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"教学楼下拉列表"模块-------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-------------------------------------------------------

    /**
     * 获取"教学楼下拉列表"
     */
    private void initTeachBuilding() {
        String urlTeachBuilding = MessageFormat.format("{0}/applyClassRoom/getRegionInfoll", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoApplyClassroomActivity.this, urlTeachBuilding, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取教学楼列表失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                TeachBuildingBean lTeachBuildingBean = OkHttpEngine.toObject(responsedJsonStr, TeachBuildingBean.class);
                if (lTeachBuildingBean != null) {
                    switch (lTeachBuildingBean.getCode()) {
                        case 0:
                            List<TeachBuildingBean.DataBean> teachBuildingList = lTeachBuildingBean.getData();
                            if ((teachBuildingList != null) && !teachBuildingList.isEmpty()) {
                                TeachBuildingAdapter lTeachBuildingAdapter = new TeachBuildingAdapter(DoApplyClassroomActivity.this, R.layout.item_drop_down_menu, teachBuildingList);
                                mSSelectTeachingBuilding.setAdapter(lTeachBuildingAdapter);
                                mSSelectTeachingBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        TeachBuildingBean.DataBean lTeachBuilding = teachBuildingList.get(position);
                                        if (lTeachBuilding != null) mTeachBuildingId = lTeachBuilding.getId();
                                        mClassroomId = -500;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.error("服务器上未添加教学楼信息!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取教学楼列表失败! {0}:{1}", lTeachBuildingBean.getCode(), lTeachBuildingBean.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 获取"教室下拉列表"
     */
    private void initClassroomDropDown() {
        if (mTeachBuildingId < 0) {
            RxToast.info("请先选择 教学楼");
            return;
        }
        String urlClassRoom = MessageFormat.format("{0}/applyClassRoom/getRegionIdClassroomAll", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoApplyClassroomActivity.this, urlClassRoom, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("regionId", String.valueOf(mTeachBuildingId)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取教室失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ClassroomBean lClassroomBean = OkHttpEngine.toObject(responsedJsonStr, ClassroomBean.class);
                if (lClassroomBean != null) {
                    switch (lClassroomBean.getCode()) {
                        case 0://成功
                            List<ClassroomBean.DataBean> lClassroomList = lClassroomBean.getData();
                            if ((lClassroomList != null) && !lClassroomList.isEmpty()) {
                                ClassroomAdapter lClassroomAdapter = new ClassroomAdapter(DoApplyClassroomActivity.this, R.layout.item_drop_down_menu, lClassroomList);
                                mSSelectClassRoom.setAdapter(lClassroomAdapter);
                                mSSelectClassRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        ClassroomBean.DataBean lClassroom = lClassroomList.get(position);
                                        if (lClassroom != null) mClassroomId = lClassroom.getId();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("服务器上当前教学楼下未添加教室!");
                            }
                            break;
                        default:
                            RxToast.error("获取教室失败! " + lClassroomBean.getCode() + ":" + lClassroomBean.getMessage());
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"教室下拉列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------

    @OnClick({R.id.tv_select_start_time, R.id.tv_select_end_time, R.id.btn_confirm_apply})
    public void onViewClicked(View view) {
        if (mTimePickerView == null) {
            mTimePickerView = new TimePickerBuilder(DoApplyClassroomActivity.this, (date, v) -> {
                switch (mTimeType) {
                    case 1://开始时间
                        mTvSelectStartTime.setText(RxTimeTool.date2String(date));
                        break;
                    case 2://结束时间
                        mTvSelectEndTime.setText(RxTimeTool.date2String(date));
                        break;
                    default:
                }
            }).setType(new boolean[]{true, true, true, true, true, true}).setSubmitColor(Color.parseColor("#FE6E28")).setCancelColor(Color.GRAY).isDialog(true).build();
        }
        switch (view.getId()) {
            case R.id.tv_select_start_time:
                mTimeType = 1;//1代表开始时间
                mTimePickerView.setTitleText("请选择'开始时间'");
                mTimePickerView.show();
                break;
            case R.id.tv_select_end_time:
                mTimeType = 2;//2代表结束时间
                mTimePickerView.setTitleText("请选择'结束时间'");
                mTimePickerView.show();
                break;
            case R.id.btn_confirm_apply:
                applyClassroom();
                break;
            default:
        }
    }

    private void applyClassroom() {
        if (mTeachBuildingId < 0) {
            RxToast.info("请先选择'教学楼'!");
            return;
        }
        if (mClassroomId == -1) {
            RxToast.info("请先选择'教室'!");
            return;
        } else if (mClassroomId == -500) {
            RxToast.info("请重新选择'教室'!");
            return;
        }
        String startTime = mTvSelectStartTime.getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            RxToast.info("请选择'开始时间'!");
            return;
        }
        String endTime = mTvSelectEndTime.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            RxToast.info("请选择'结束时间'!");
            return;
        }
        String applyUsage = mEtApplyUsageDetail.getText().toString();
        if (TextUtils.isEmpty(applyUsage)) {
            RxToast.info("请输入申请教室用途!");
            return;
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoApplyClassroomActivity.this, MessageFormat.format("{0}/applyClassRoom/addApplyClassRoom", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("mark", applyUsage).add("type", "3").add("classRoomId", String.valueOf(mClassroomId)).add("startTime", startTime).add("endTime", endTime).add("parentId", "0").build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,申请教室失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lApplyClassroomResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lApplyClassroomResultBean != null) {
                    switch (lApplyClassroomResultBean.getCode()) {
                        case 0:
                            RxToast.success("申请教室成功!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("申请教室失败! {0}:{1}", lApplyClassroomResultBean.getCode(), lApplyClassroomResultBean.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 教学楼 Adapter
     */
    private class TeachBuildingAdapter extends ArrayAdapter<TeachBuildingBean.DataBean> {
        private int itemLayoutResId;

        TeachBuildingAdapter(@NonNull Context context, int resource, @NonNull List<TeachBuildingBean.DataBean> dataBeanList) {
            super(context, resource, dataBeanList);
            itemLayoutResId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TeachBuildingBean.DataBean lTeachBuildingBean = getItem(position);
            if (lTeachBuildingBean != null) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.mTvTeachBuilding = convertView.findViewById(R.id.tv_item);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.mTvTeachBuilding.setText(lTeachBuildingBean.getFullName());
            }
            return convertView;
        }

        class ViewHolder {
            TextView mTvTeachBuilding;
        }
    }

    /**
     * 教室 Adapter
     */
    private class ClassroomAdapter extends ArrayAdapter<ClassroomBean.DataBean> {
        private int itemLayoutResId;

        ClassroomAdapter(@NonNull Context context, int resource, @NonNull List<ClassroomBean.DataBean> objects) {
            super(context, resource, objects);
            itemLayoutResId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ClassroomBean.DataBean classroomBean = getItem(position);
            if (classroomBean != null) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.mTvClassroomName = convertView.findViewById(R.id.tv_item);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.mTvClassroomName.setText(classroomBean.getName());
            }
            return convertView;
        }

        class ViewHolder {
            TextView mTvClassroomName;
        }
    }
}
