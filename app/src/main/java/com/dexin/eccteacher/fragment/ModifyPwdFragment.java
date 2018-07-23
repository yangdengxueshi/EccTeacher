package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxEncryptTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 修改密码Fragment
 */
public class ModifyPwdFragment extends BaseFragment {
    public static final String TAG = "TAG_ModifyPwdFragment";
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.actv_old_pwd)
    AutoCompleteTextView mActvOldPwd;
    @BindView(R.id.iv_clean_old_pwd)
    ImageView mIvCleanOldPwd;
    @BindView(R.id.actv_new_pwd)
    AutoCompleteTextView mActvNewPwd;
    @BindView(R.id.iv_clean_new_pwd)
    ImageView mIvCleanNewPwd;
    @BindView(R.id.actv_confirm_pwd)
    AutoCompleteTextView mActvConfirmPwd;
    @BindView(R.id.iv_clean_confirm_pwd)
    ImageView mIvCleanConfirmPwd;

    @NonNull
    public static ModifyPwdFragment newInstance() {
        return new ModifyPwdFragment();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_modify_pwd;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("密码修改");
    }

    @Override
    protected void initListener() {
        mActvOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanOldPwd.getVisibility() == View.GONE)) {
                    mIvCleanOldPwd.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanOldPwd.getVisibility() == View.VISIBLE)) {
                    mIvCleanOldPwd.setVisibility(View.GONE);
                }
            }
        });
        mActvNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanNewPwd.getVisibility() == View.GONE)) {
                    mIvCleanNewPwd.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanNewPwd.getVisibility() == View.VISIBLE)) {
                    mIvCleanNewPwd.setVisibility(View.GONE);
                }
            }
        });
        mActvConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanConfirmPwd.getVisibility() == View.GONE)) {
                    mIvCleanConfirmPwd.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanConfirmPwd.getVisibility() == View.VISIBLE)) {
                    mIvCleanConfirmPwd.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void initData() {
    }


    @OnClick({R.id.btn_back, R.id.iv_clean_old_pwd, R.id.iv_clean_new_pwd, R.id.iv_clean_confirm_pwd, R.id.btn_confirm_modify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                pop();
                break;
            case R.id.iv_clean_old_pwd:
                mActvOldPwd.setText("");
                break;
            case R.id.iv_clean_new_pwd:
                mActvNewPwd.setText("");
                break;
            case R.id.iv_clean_confirm_pwd:
                mActvConfirmPwd.setText("");
                break;
            case R.id.btn_confirm_modify:
                hideSoftInput();
                String oldPwdStr = mActvOldPwd.getText().toString();
                if (oldPwdStr.length() < 6) {
                    RxToast.error("旧密码至少6位!");
                    break;
                }
                String newPwdStr = mActvNewPwd.getText().toString();//2.检查密码是否合法
                String newPwdStrAgain = mActvConfirmPwd.getText().toString();
                if (!Objects.equals(newPwdStr, newPwdStrAgain)) {
                    RxToast.error("两次新密码不一致,请确认!");
                    break;
                }
                if ((newPwdStr.length() < 6) || (newPwdStrAgain.length() < 6)) {
                    RxToast.error("新密码至少6位!");
                    break;
                }
                modifyPwd(oldPwdStr, newPwdStrAgain);
                break;
        }
    }

    /**
     * 修改密码
     *
     * @param oldPwdStr      旧密码
     * @param newPwdStrAgain 新密码
     */
    private void modifyPwd(String oldPwdStr, String newPwdStrAgain) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(ModifyPwdFragment.this, MessageFormat.format("{0}/access/updateTeacherPassword", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("oldPassword", RxEncryptTool.encryptMD5ToString(oldPwdStr).toLowerCase()).add("newPassword", RxEncryptTool.encryptMD5ToString(newPwdStrAgain)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,修改密码失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lModifyPwdResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lModifyPwdResultBean != null) {
                    switch (lModifyPwdResultBean.getCode()) {
                        case 0://成功
                            RxToast.success("修改密码成功,下次请使用新密码登录!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("修改密码失败! {0}:{1}", lModifyPwdResultBean.getCode(), lModifyPwdResultBean.getMessage()));
                    }
                }
            }
        });
    }
}
