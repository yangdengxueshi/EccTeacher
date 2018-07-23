package com.dexin.eccteacher.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ContAdapter;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ContBean;
import com.dexin.eccteacher.bean.DataBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.FormBody;

public class SearchContActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.et_search_cont)
    EditText mEtSearchCont;
    @BindView(R.id.btn_search_cont)
    Button mBtnSearchCont;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_cont_searched_list)
    RecyclerView mRvContSearched;
    private View mEmptyView, mErrorView, mLoadingView;
    private ContAdapter mContAdapter;
    private RxPermissions mRxPermissions;
    private String mSearchKeyword;
    private String mUrlSearch[] = {MessageFormat.format("{0}/teacherInfo/getTeacherPhoneInfo", AppConfig.SERVER_ADDRESS), MessageFormat.format("{0}/teacherInfo/getPatriarchPhoneInfo", AppConfig.SERVER_ADDRESS)};
    private String mContClass[] = {"教师", "家长"};//待搜索的联系人类别

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_search_cont;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar;
        if ((lActionBar = getSupportActionBar()) != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText(MessageFormat.format("搜索 {0}", mContClass[AppConfig.getSPUtils().getInt(AppConfig.KEY_SEARCH_CONT_CLASS)]));
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvContSearched.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvContSearched.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvContSearched.getParent(), false);
        mContAdapter = new ContAdapter();
        mContAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mContAdapter.isFirstOnly(false);
        mRvContSearched.setAdapter(mContAdapter);
        mRvContSearched.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        mRvContSearched.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEtSearchCont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (!mBtnSearchCont.isEnabled()) {
                        mBtnSearchCont.setEnabled(true);
                        mBtnSearchCont.setBackgroundResource(R.drawable.ic_search_able);
                    }
                } else {
                    if (mBtnSearchCont.isEnabled()) {
                        mBtnSearchCont.setEnabled(false);
                        mBtnSearchCont.setBackgroundResource(R.drawable.ic_search_disable);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEmptyView.setOnClickListener(v -> loadMoreKeywordContacts(mSearchKeyword, true));
        mErrorView.setOnClickListener(v -> loadMoreKeywordContacts(mSearchKeyword, true));
        //初始化SmartRefreshLayout
        mSrlSmartRefresh.setOnLoadMoreListener(refreshLayout -> loadMoreKeywordContacts(mSearchKeyword, false));
        mContAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            DataBean lDataBean = (DataBean) baseQuickAdapter.getItem(position);
            if (lDataBean == null) return;
            switch (view.getId()) {
                case R.id.iv_cont_chat:
                    String targetUserId = lDataBean.getRongUserId();
                    if (!TextUtils.isEmpty(targetUserId)) {
                        RongIM.getInstance().startPrivateChat(SearchContActivity.this, targetUserId, lDataBean.getName());
                    } else {
                        RxToast.error("聊天对象不存在,请重新搜索!");
                    }
                    break;
                case R.id.iv_cont_dail:
                    String telStr = lDataBean.getPhone();
                    if (!AppConfig.isTelphoneNo(telStr)) {
                        RxToast.error("不是正确的电话号码!");
                        break;
                    }
                    if (mRxPermissions == null) mRxPermissions = new RxPermissions(SearchContActivity.this);
                    mRxPermissions.requestEach(Manifest.permission.CALL_PHONE).subscribe(permission -> {
                        if (permission.granted) {
                            if (ContextCompat.checkSelfPermission(CustomApplication.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(MessageFormat.format("tel:{0}", telStr))));
                            } else {
                                RxToast.info("请授权程序拨打电话!");
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {//单纯地拒绝权限
                            RxToast.error("'打电话'依赖于拨号权限,请重新授权!");
                        } else {//不再询问地拒绝权限
                            new AlertDialog.Builder(SearchContActivity.this).setCancelable(false).setIcon(R.drawable.ic_alarm).setTitle("系统设置中授权").setMessage("您拒绝了程序直接打电话,请在系统设置中重新授权!").setNegativeButton("取消", (dialog, which) -> {
                            }).setPositiveButton("确定", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + Utils.getApp().getPackageName()));
                                Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }).show();
                        }
                    });
                    break;
                default:
            }
        });
    }

    @Override
    public void initData() {
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, SearchContActivity.class);
    }

    @OnClick({R.id.btn_search_cont, R.id.btn_cancel_search, R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_cont:
                RxKeyboardTool.hideSoftInput(SearchContActivity.this);
                mSearchKeyword = mEtSearchCont.getText().toString();
                mPageIndex = 1;
                loadMoreKeywordContacts(mSearchKeyword, true);
                break;
            case R.id.btn_cancel_search:
                mEtSearchCont.setText("");
                break;
            case R.id.fab_top:
                mRvContSearched.smoothScrollToPosition(0);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "按照关键字搜索联系人"模块-----------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mContResponsedCallback;

    /**
     * 按照关键字搜索联系人
     *
     * @param keyword 关键字
     */
    private void loadMoreKeywordContacts(String keyword, boolean loading) {
        if (loading) mContAdapter.setEmptyView(mLoadingView);
        if (mContResponsedCallback == null) {
            mContResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    new MStatusDialog(SearchContActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("搜索失败", getResources().getDrawable(R.drawable.ic_failure));
                    mContAdapter.setEmptyView(mErrorView);
                    mSrlSmartRefresh.finishLoadMore(false);//加载失败
                    mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    LogUtility.d(TAG, "onResponseJson: " + responsedJsonStr);
                    if (mPageIndex == 1) {
                        mSrlSmartRefresh.setEnableLoadMore(true);
                        mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                    }
                    ContBean lContSearchedBean = OkHttpEngine.toObject(responsedJsonStr, ContBean.class);
                    if (lContSearchedBean != null) {
                        switch (lContSearchedBean.getCode()) {
                            case 0://成功
                                ContBean.PageObjectBean lPageObjectBean = lContSearchedBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<DataBean> lDataBeanList = lContSearchedBean.getData();
                                    if ((lDataBeanList != null) && !lDataBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mContAdapter.replaceData(lDataBeanList);
                                        } else {
                                            mContAdapter.addData(lDataBeanList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mContAdapter.setNewData(null);
                                            mContAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("搜索失败! {0}:{1}", lContSearchedBean.getCode(), lContSearchedBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(SearchContActivity.this, mUrlSearch[AppConfig.getSPUtils().getInt(AppConfig.KEY_SEARCH_CONT_CLASS)], new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("keyword", keyword).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mContResponsedCallback);
    }
}
