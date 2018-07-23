package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.blankj.utilcode.util.DeviceUtils;
import com.dexin.eccteacher.BuildConfig;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.activity.MainActivity;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.LoginBean;
import com.dexin.eccteacher.custom_view.RxAutoImageView;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.vondear.rxtool.RxEncryptTool;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.view.RxToast;

import java.io.File;
import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 登录Fragment
 */
public class LoginFragment extends BaseFragment {
    @BindView(R.id.raiv_bg_login)
    RxAutoImageView mRaivBgLogin;
    @BindView(R.id.actv_account)
    AutoCompleteTextView mActvAccount;
    @BindView(R.id.iv_clean_account)
    ImageView mIvCleanAccount;
    @BindView(R.id.actv_pwd)
    AutoCompleteTextView mActvPwd;
    @BindView(R.id.iv_clean_pwd)
    ImageView mIvCleanPwd;

    @NonNull
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mRaivBgLogin.startAutoImageView();
    }

    @Override
    public void onSupportInvisible() {
        mRaivBgLogin.releaseAutoImageView();
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        mActvAccount.setText(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_ACCOUNT));
        mActvPwd.setText(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_PWD));
    }

    @Override
    protected void initListener() {
        mActvAccount.addTextChangedListener(new TextWatcher() {
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
        mActvPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (AppConfig.isComponentAlive(getActivity()) && AppConfig.isComponentAlive(LoginFragment.this)) {
                if (hasFocus) {
                    String tempAccountStr = mActvAccount.getText().toString();
                    if (!AppConfig.isTelphoneNo(tempAccountStr)) {
                        RxToast.error(getString(R.string.telphone_no_format_error));
                    }
                } else {
                    String tempPwdStr = mActvPwd.getText().toString();
                    if (tempPwdStr.length() < 6) {
                        RxToast.error("密码至少6位!");
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        freshAutoTipAdapterToAccount();
    }


    /**
     * FIXME 刷新账户输入自动提示
     */
    private void freshAutoTipAdapterToAccount() {
        String allAccountStr = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_ACCOUNT_ALL_SIGNED).trim();
        if (!TextUtils.isEmpty(allAccountStr)) {
            String[] accountStrArr = allAccountStr.split(" ");
            if (accountStrArr.length > 8) {
                String[] accountStrArrTopTen = new String[8];
                System.arraycopy(accountStrArr, 0, accountStrArrTopTen, 0, accountStrArrTopTen.length);
                accountStrArr = accountStrArrTopTen;
            }
            mActvAccount.setAdapter(new ArrayAdapter<>(CustomApplication.getContext(), R.layout.item_tip_account, accountStrArr));
        }
    }

    @OnClick({R.id.iv_clean_account, R.id.iv_clean_pwd, R.id.v_forget_pwd, R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clean_account:
                mActvAccount.setText("");
                break;
            case R.id.iv_clean_pwd:
                mActvPwd.setText("");
                break;
            case R.id.v_forget_pwd:
                start(ForgetPwdFragment.newInstance());
                break;
            case R.id.btn_login:
                hideSoftInput();
                String accountStr = mActvAccount.getText().toString();
                String pwdStr = mActvPwd.getText().toString();
                if (!AppConfig.isTelphoneNo(accountStr)) {
                    mActvAccount.setError(getString(R.string.telphone_no_format_error));
                    break;
                } else {
                    String teacherAccountAllSigned = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_ACCOUNT_ALL_SIGNED);
                    if (!teacherAccountAllSigned.contains(accountStr)) {
                        AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_ACCOUNT_ALL_SIGNED, MessageFormat.format("{0} {1}", accountStr, teacherAccountAllSigned));
                        freshAutoTipAdapterToAccount();
                    }
                }
                if (pwdStr.length() < 6) {
                    mActvPwd.setError("密码至少6位!");
                    break;
                }
                login(accountStr, pwdStr);
                break;
            case R.id.btn_register:
                start(RegisterFragment.newInstance());
                break;
            default:
        }
    }

    /**
     * 提交"账户"和"密码"进行登录
     *
     * @param accountStr 账户字符串
     * @param pwdStr     密码(还是明码)字符串
     */
    private void login(String accountStr, String pwdStr) {
        if (RxNetTool.isAvailable(CustomApplication.getContext())) MProgressDialog.showProgress(Objects.requireNonNull(getActivity()), "登录中...");
        OkHttpEngine.getInstance().sendAsyncPostRequest(LoginFragment.this, MessageFormat.format("{0}/access/teacherLogin", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("phoneNum", accountStr).add("password", RxEncryptTool.encryptMD5ToString(pwdStr).toLowerCase()).add("phoneModel", DeviceUtils.getModel()).add("mobileVersion", DeviceUtils.getManufacturer()).add("androidVersion", DeviceUtils.getSDKVersionName()).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                MProgressDialog.dismissProgress();
                new MStatusDialog(getActivity(), new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("登录失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                MProgressDialog.dismissProgress();
                LoginBean lLoginBean = OkHttpEngine.toObject(responsedJsonStr, LoginBean.class);
                if (lLoginBean != null) {
                    switch (lLoginBean.getCode()) {
                        case 0://成功:
                            //新登录用户,先清除老用户"头像文件"和"全部缓存"
                            File lTeacherAvatartFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR));
                            if (lTeacherAvatartFile.exists()) lTeacherAvatartFile.delete();
                            AppConfig.getSPUtils().clear();
                            AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_ACCOUNT, accountStr);
                            AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_PWD, pwdStr);
                            LoginBean.DataBean lTeacherDataBean = lLoginBean.getData();
                            if (lTeacherDataBean != null) {
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_TOKEN, lTeacherDataBean.getToken());
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_ID, lTeacherDataBean.getId());
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_SCHOOL_ID, lTeacherDataBean.getSchoolId());
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_ID, lTeacherDataBean.getUserId());
                                AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_NAME, lTeacherDataBean.getUserName());
                                String rongUserId = lTeacherDataBean.getRongUserId();
                                String rongToken = lTeacherDataBean.getRongToken();
                                if (TextUtils.isEmpty(rongUserId) || TextUtils.isEmpty(rongToken)) {
                                    RxToast.error("获取融云UserId或Token失败,请重试!");
                                    return;
                                }
                                AppConfig.getSPUtils().put(AppConfig.KEY_RONG_USER_ID, rongUserId);
                                AppConfig.getSPUtils().put(AppConfig.KEY_RONG_USER_TOKEN, rongToken);
                            }
                            startActivity(MainActivity.createIntent(CustomApplication.getContext()));
                            Objects.requireNonNull(getActivity()).finish();
                            break;
                        default:
                            RxToast.error(MessageFormat.format("登录失败! {0}:{1}", lLoginBean.getCode(), lLoginBean.getMessage()));
                    }
                }
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (BuildConfig.DEBUG) {
            login("17078020229", "123456");
            return true;
        } else {
            return super.onBackPressedSupport();
        }
    }
}
