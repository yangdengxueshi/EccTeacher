package com.dexin.eccteacher.fragment;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
 * 忘记密码Fragment
 */
public class ForgetPwdFragment extends BaseFragment {
    public static final String TAG = "TAG_ForgetPwdFragment";
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.et_telphone_no)
    EditText mEtTelphoneNo;
    @BindView(R.id.iv_clean_account)
    ImageView mIvCleanAccount;
    @BindView(R.id.et_identifying_code)
    EditText mEtIdentifyingCode;
    @BindView(R.id.btn_gain_identifying_code)
    Button mBtnGainIdentifyingCode;
    @BindView(R.id.actv_new_pwd)
    AutoCompleteTextView mActvNewPwd;
    @BindView(R.id.iv_clean_new_pwd)
    ImageView mIvCleanNewPwd;
    @BindView(R.id.actv_confirm_new_pwd)
    AutoCompleteTextView mActvConfirmNewPwd;
    @BindView(R.id.iv_clean_confirm_new_pwd)
    ImageView mIvCleanConfirmNewPwd;
    private CountDownTimer mIdentifyingCodeTimer;

    @NonNull
    public static ForgetPwdFragment newInstance() {
        return new ForgetPwdFragment();
    }

    @Override
    public void onSupportInvisible() {
        if (mIdentifyingCodeTimer != null) mIdentifyingCodeTimer.cancel();
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_forget_pwd;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("密码重置");
    }

    @Override
    protected void initListener() {
        mEtTelphoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanAccount.getVisibility() == View.GONE)) {
                    mIvCleanAccount.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanAccount.getVisibility() == View.VISIBLE)) {
                    mIvCleanAccount.setVisibility(View.GONE);
                }
                if (!mBtnGainIdentifyingCode.isEnabled()) canGainIndentfyCode();
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
        mActvConfirmNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanConfirmNewPwd.getVisibility() == View.GONE)) {
                    mIvCleanConfirmNewPwd.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanConfirmNewPwd.getVisibility() == View.VISIBLE)) {
                    mIvCleanConfirmNewPwd.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void initData() {
    }


    @OnClick({R.id.btn_back, R.id.iv_clean_account, R.id.btn_gain_identifying_code, R.id.iv_clean_new_pwd, R.id.iv_clean_confirm_new_pwd, R.id.btn_confirm_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                pop();
                break;
            case R.id.iv_clean_account:
                mEtTelphoneNo.setText("");
                break;
            case R.id.btn_gain_identifying_code:
                String telPhoneNo = mEtTelphoneNo.getText().toString();//1.检查手机号是否合法
                if (!AppConfig.isTelphoneNo(telPhoneNo)) {
                    RxToast.error(getString(R.string.telphone_no_format_error));
                    break;
                }
                gainIndentfyCode(telPhoneNo);
                break;
            case R.id.iv_clean_new_pwd:
                mActvNewPwd.setText("");
                break;
            case R.id.iv_clean_confirm_new_pwd:
                mActvConfirmNewPwd.setText("");
                break;
            case R.id.btn_confirm_reset:
                hideSoftInput();
                telPhoneNo = mEtTelphoneNo.getText().toString();//1.检查手机号是否合法
                if (!AppConfig.isTelphoneNo(telPhoneNo)) {
                    RxToast.error(getString(R.string.telphone_no_format_error));
                    break;
                }
                String newPwdStr = mActvNewPwd.getText().toString();//2.检查密码是否合法
                String newPwdStrAgain = mActvConfirmNewPwd.getText().toString();
                if (!Objects.equals(newPwdStr, newPwdStrAgain)) {
                    RxToast.error("两次密码不一致,请确认!");
                    break;
                }
                if ((newPwdStr.length() < 6) || (newPwdStrAgain.length() < 6)) {
                    RxToast.error("密码至少6位!");
                    break;
                }
                String identifyCodeStr = mEtIdentifyingCode.getText().toString();//3.检查验证码是否合法
                if (TextUtils.isEmpty(identifyCodeStr)) {
                    RxToast.error("验证码不能为空!");
                    break;
                }
                resetPwd(telPhoneNo, newPwdStrAgain, identifyCodeStr);
                break;
            default:
        }
    }

    /**
     * 能够获取验证码
     */
    private void canGainIndentfyCode() {
        if (mIdentifyingCodeTimer != null) mIdentifyingCodeTimer.cancel();
        if (mBtnGainIdentifyingCode != null) {
            mBtnGainIdentifyingCode.setEnabled(true);
            mBtnGainIdentifyingCode.setTextColor(getResources().getColor(R.color.white));
            mBtnGainIdentifyingCode.setText(getString(R.string.gain_identifying_code));
        }
    }

    /**
     * 获取手机验证码
     *
     * @param telPhoneNo 手机号
     */
    private void gainIndentfyCode(String telPhoneNo) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(ForgetPwdFragment.this, MessageFormat.format("{0}/access/getUpdataCode", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("phoneNum", telPhoneNo).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取验证码失败!");
                canGainIndentfyCode();
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lIdentfyCodeResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lIdentfyCodeResultBean != null) {
                    switch (lIdentfyCodeResultBean.getCode()) {
                        case 0://成功
                            RxToast.success("获取验证码成功!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取验证码失败! {0}:{1}", lIdentfyCodeResultBean.getCode(), lIdentfyCodeResultBean.getMessage()));
                            canGainIndentfyCode();
                    }
                } else {
                    canGainIndentfyCode();
                }
            }
        });

        if (mIdentifyingCodeTimer == null) {
            mIdentifyingCodeTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mBtnGainIdentifyingCode != null) {
                        if (mBtnGainIdentifyingCode.isEnabled()) {
                            mBtnGainIdentifyingCode.setEnabled(false);
                            mBtnGainIdentifyingCode.setTextColor(getResources().getColor(R.color.gray));
                        }
                        mBtnGainIdentifyingCode.setText(MessageFormat.format("已请求({0})", millisUntilFinished / 1000));
                    }
                }

                @Override
                public void onFinish() {
                    canGainIndentfyCode();
                }
            };
        }
        mIdentifyingCodeTimer.start();
    }

    /**
     * 重置密码
     *
     * @param telPhoneNo      手机号
     * @param newPwdStr       新密码
     * @param identifyCodeStr 验证码
     */
    private void resetPwd(String telPhoneNo, String newPwdStr, String identifyCodeStr) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(ForgetPwdFragment.this, MessageFormat.format("{0}/access/resetTeacherPassword", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("phoneNumber", telPhoneNo).add("password", RxEncryptTool.encryptMD5ToString(newPwdStr).toLowerCase()).add("phoneCode", identifyCodeStr).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,重置密码失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lForgetPwdResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lForgetPwdResultBean != null) {
                    switch (lForgetPwdResultBean.getCode()) {
                        case 0://成功
                            RxToast.success("重置密码成功,下次请使用新密码登录!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("重置密码失败! {0}:{1}", lForgetPwdResultBean.getCode(), lForgetPwdResultBean.getMessage()));
                    }
                }
            }
        });
    }
}
