package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

public class SubmitFaultActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.et_fault_class)
    EditText mEtFaultClass;
    @BindView(R.id.et_fault_content)
    EditText mEtFaultContent;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_submit_fault;
    }

    @Override
    protected void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("故障上报");
    }

    @Override
    protected void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    protected void initData() {
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, SubmitFaultActivity.class);
    }

    @OnClick({R.id.tv_select_pic, R.id.btn_confirm_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_pic:
                break;
            case R.id.btn_confirm_publish:
                submitFault(mEtFaultClass.getText().toString(), mEtFaultContent.getText().toString(), "");
                break;
            default:
        }
    }


    /**
     * 提交故障
     *
     * @param type    故障类型
     * @param mark    故障详细描述
     * @param content
     */
    private void submitFault(String type, String mark, String content) {
        if (TextUtils.isEmpty(type)) {
            RxToast.info("请输入故障类型!");
            return;
        }
        if (TextUtils.isEmpty(mark)) {
            RxToast.info("请输入故障详细描述!");
            return;
        }
        if (RxNetTool.isAvailable(CustomApplication.getContext())) MProgressDialog.showProgress(SubmitFaultActivity.this, "提交中...");
        OkHttpEngine.getInstance().sendAsyncPostRequest(SubmitFaultActivity.this, MessageFormat.format("{0}/faultMend/addFaultMend", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("type", "2").add("typeName", type).add("mark", mark).add("content", content).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                MProgressDialog.dismissProgress();
                new MStatusDialog(SubmitFaultActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("提交失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                MProgressDialog.dismissProgress();
                ResponseResultBean lResponseResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lResponseResultBean != null) {
                    switch (lResponseResultBean.getCode()) {
                        case 0://成功
                            new MStatusDialog(SubmitFaultActivity.this).show("提交成功", getResources().getDrawable(R.drawable.ic_success));
                            break;
                        default:
                            RxToast.error("提交失败! " + lResponseResultBean.getCode() + ":" + lResponseResultBean.getMessage());
                    }
                }
            }
        });
    }
}
