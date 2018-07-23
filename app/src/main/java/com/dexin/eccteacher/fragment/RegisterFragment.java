package com.dexin.eccteacher.fragment;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.activity.H5Activity;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxEncryptTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 注册Fragment
 */
public class RegisterFragment extends BaseFragment {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.et_telphone_no)
    EditText mEtTelphoneNo;
    @BindView(R.id.iv_clean_account)
    ImageView mIvCleanAccount;
    @BindView(R.id.actv_pwd)
    AutoCompleteTextView mActvPwd;
    @BindView(R.id.iv_clean_pwd)
    ImageView mIvCleanPwd;
    @BindView(R.id.actv_confirm_pwd)
    AutoCompleteTextView mActvConfirmPwd;
    @BindView(R.id.iv_clean_confirm_pwd)
    ImageView mIvCleanConfirmPwd;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.iv_clean_code)
    ImageView mIvCleanCode;
    @BindView(R.id.et_card_no)
    EditText mEtCardNo;
    @BindView(R.id.iv_clean_card_no)
    ImageView mIvCleanCardNo;
    @BindView(R.id.et_identifying_code)
    EditText mEtIdentifyingCode;
    @BindView(R.id.btn_gain_identifying_code)
    Button mBtnGainIdentifyingCode;
    @BindView(R.id.tv_agree_register_protocol)
    TextView mTvAgreeRegisterProtocol;
    private CountDownTimer mIdentifyingCodeTimer;

    @NonNull
    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onSupportInvisible() {
        if (mIdentifyingCodeTimer != null) mIdentifyingCodeTimer.cancel();
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_register;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("注册");
        mTvAgreeRegisterProtocol.setMovementMethod(LinkMovementMethod.getInstance());//响应点击事件必须配置该属性
        RxTextTool.getBuilder("").append("注册即代表您已阅读并同意").append("《德芯APP注册服务协议》").setClickSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(H5Activity.createIntent(CustomApplication.getContext(), true, "成都德芯数字科技股份有限公司", "http://www.dsdvb.com/"));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.GRAY);//custom_green_dark
                ds.setUnderlineText(false);
            }
        }).into(mTvAgreeRegisterProtocol);
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
        mActvPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanPwd.getVisibility() == View.GONE)) {
                    mIvCleanPwd.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanPwd.getVisibility() == View.VISIBLE)) {
                    mIvCleanPwd.setVisibility(View.GONE);
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
        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanCode.getVisibility() == View.GONE)) {
                    mIvCleanCode.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanCode.getVisibility() == View.VISIBLE)) {
                    mIvCleanCode.setVisibility(View.GONE);
                }
            }
        });
        mEtCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (mIvCleanCardNo.getVisibility() == View.GONE)) {
                    mIvCleanCardNo.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s) && (mIvCleanCardNo.getVisibility() == View.VISIBLE)) {
                    mIvCleanCardNo.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void initData() {
    }


    @OnClick({R.id.btn_back, R.id.iv_clean_account, R.id.btn_gain_identifying_code, R.id.iv_clean_pwd, R.id.iv_clean_confirm_pwd, R.id.iv_clean_code, R.id.iv_clean_card_no, R.id.btn_confirm_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                hideSoftInput();
                mFragmentActivity.onBackPressed();
                break;
            case R.id.iv_clean_account:
                mEtTelphoneNo.setText("");
                break;
            case R.id.btn_gain_identifying_code:
                String telPhoneNo = mEtTelphoneNo.getText().toString();
                if (!AppConfig.isTelphoneNo(telPhoneNo)) {
                    RxToast.error(getString(R.string.telphone_no_format_error));
                    break;
                }
                gainIndentfyCode(telPhoneNo);
                break;
            case R.id.iv_clean_pwd:
                mActvPwd.setText("");
                break;
            case R.id.iv_clean_confirm_pwd:
                mActvConfirmPwd.setText("");
                break;
            case R.id.iv_clean_code:
                mEtCode.setText("");
                break;
            case R.id.iv_clean_card_no:
                mEtCardNo.setText("");
                break;
            case R.id.btn_confirm_register:
                hideSoftInput();
                telPhoneNo = mEtTelphoneNo.getText().toString();
                if (!AppConfig.isTelphoneNo(telPhoneNo)) {
                    RxToast.error(getString(R.string.telphone_no_format_error));
                    break;
                }
                String pwdStr = mActvPwd.getText().toString();
                String pwdStrAgain = mActvConfirmPwd.getText().toString();
                if (!Objects.equals(pwdStr, pwdStrAgain)) {
                    RxToast.error("两次密码不一致,请确认!");
                    break;
                }
                if ((pwdStr.length() < 6) || (pwdStrAgain.length() < 6)) {
                    RxToast.error("密码至少6位!");
                    break;
                }
                String codeStr = mEtCode.getText().toString();
                if (TextUtils.isEmpty(codeStr)) {
                    RxToast.error("Code码不能为空!");
                    break;
                }
                String cardNoStr = mEtCardNo.getText().toString();
                if (TextUtils.isEmpty(cardNoStr)) {
                    RxToast.error("卡号不能为空!");
                    break;
                }
                String identifyCodeStr = mEtIdentifyingCode.getText().toString();
                if (TextUtils.isEmpty(identifyCodeStr)) {
                    RxToast.error("验证码不能为空!");
                    break;
                }
                register(telPhoneNo, pwdStrAgain, codeStr, cardNoStr, identifyCodeStr);
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
        String urlIdentfyCode = MessageFormat.format("{0}/access/getRegistCode", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(RegisterFragment.this, urlIdentfyCode, new FormBody.Builder().add("phoneNum", telPhoneNo).build(), new OkHttpEngine.ResponsedCallback() {
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
     * 注册新用户
     *
     * @param telPhoneNo      手机号
     * @param pwdStrAgain     密码
     * @param codeStr         Code码
     * @param cardNoStr       卡号
     * @param identifyCodeStr 手机验证码
     */
    private void register(String telPhoneNo, String pwdStrAgain, String codeStr, String cardNoStr, String identifyCodeStr) {
        String urlRegister = MessageFormat.format("{0}/access/teacherRegister", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(RegisterFragment.this, urlRegister, new FormBody.Builder().add("phoneNumber", telPhoneNo).add("password", RxEncryptTool.encryptMD5ToString(pwdStrAgain).toLowerCase()).add("code", codeStr).add("icCardNum", cardNoStr).add("phoneCode", identifyCodeStr).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,注册失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lRegisterResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lRegisterResultBean != null) {
                    switch (lRegisterResultBean.getCode()) {
                        case 0://成功
                            RxToast.success("注册成功,请返回页面登录!");
                            break;
                        default:
                            RxToast.error(MessageFormat.format("注册失败! {0}:{1}", lRegisterResultBean.getCode(), lRegisterResultBean.getMessage()));
                    }
                }
            }
        });
    }
}
