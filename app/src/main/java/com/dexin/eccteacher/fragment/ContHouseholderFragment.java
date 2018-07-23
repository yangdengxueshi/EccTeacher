package com.dexin.eccteacher.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ContAdapter;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ContBean;
import com.dexin.eccteacher.bean.DataBean;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 联系人_家长 Fragment
 */
public class ContHouseholderFragment extends BaseFragment {
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_cont_householder)
    RecyclerView mRvContHouseholder;
    private View mEmptyView, mErrorView, mLoadingView;
    private ContAdapter mContAdapter;

    @NonNull
    public static ContHouseholderFragment newInstance() {
        return new ContHouseholderFragment();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        AppConfig.getSPUtils().put(AppConfig.KEY_SEARCH_CONT_CLASS, 1);//1 代表搜索家长
    }

    @Override
    public void onSupportInvisible() {
        mSrlSmartRefresh.finishRefresh(true);
        mSrlSmartRefresh.finishLoadMore(true);
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_cont_householder;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvContHouseholder.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvContHouseholder.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvContHouseholder.getParent(), false);
        mContAdapter = new ContAdapter();
        mContAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mContAdapter.isFirstOnly(false);
        mRvContHouseholder.setAdapter(mContAdapter);
        mRvContHouseholder.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        mRvContHouseholder.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    private RxPermissions mRxPermissions;

    @Override
    protected void initListener() {
        mEmptyView.setOnClickListener(v -> loadMoreHouseholderContacts(true));
        mErrorView.setOnClickListener(v -> loadMoreHouseholderContacts(true));
        mSrlSmartRefresh.setOnLoadMoreListener(refreshLayout -> loadMoreHouseholderContacts(false));
        mContAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            DataBean lDataBean = (DataBean) baseQuickAdapter.getItem(position);
            if (lDataBean == null) return;
            LitePal.deleteAll(DataBean.class, "rongUserId=?", lDataBean.getRongUserId());//先删除旧的联系人信息
            lDataBean.save();//FIXME 将曾经聊过天的联系人异步存进数据库
            switch (view.getId()) {
                case R.id.iv_cont_chat:
                    String targetUserId = lDataBean.getRongUserId();
                    if (!TextUtils.isEmpty(targetUserId)) {
                        RongIM.getInstance().startPrivateChat(Objects.requireNonNull(getActivity()), targetUserId, lDataBean.getName());
                    } else {
                        RxToast.error("聊天对象不存在,请刷新重试!");
                    }
                    break;
                case R.id.iv_cont_dail:
                    String telStr = lDataBean.getPhone();
                    if (!AppConfig.isTelphoneNo(telStr)) {
                        RxToast.error("不是正确的电话号码!");
                        break;
                    }
                    if (Objects.equals(telStr, AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_ACCOUNT))) {
                        RxToast.error("请不要给自己打电话!");
                        break;
                    }
                    if (mRxPermissions == null) mRxPermissions = new RxPermissions(ContHouseholderFragment.this);
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
                            new AlertDialog.Builder(Objects.requireNonNull(getActivity())).setCancelable(false).setIcon(R.drawable.ic_alarm).setTitle("系统设置中授权").setMessage("您拒绝了程序直接打电话,请在系统设置中重新授权!").setNegativeButton("取消", (dialog, which) -> {
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
    protected void initData() {
        loadMoreHouseholderContacts(true);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"家长对象通讯录"模块-------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-------------------------------------------------------
    private int mPageIndex = 1;
    private OkHttpEngine.ResponsedCallback mContHouseholderResponsedCallback;

    /**
     * 获取 家长通讯录
     */
    private void loadMoreHouseholderContacts(boolean loading) {
        if (loading) mContAdapter.setEmptyView(mLoadingView);
        if (mContHouseholderResponsedCallback == null) {
            mContHouseholderResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mContAdapter.setEmptyView(mErrorView);
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CANCEL_REFRESH_CONT));
                    mSrlSmartRefresh.finishLoadMore(false);//加载失败
                    mSrlSmartRefresh.finishRefresh(false);//刷新失败
                    mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    LogUtility.d(TAG, "onResponseJson: " + responsedJsonStr);
                    if (mPageIndex == 1) {
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CANCEL_REFRESH_CONT));
                        mSrlSmartRefresh.finishRefresh(true);
                        mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                    }
                    ContBean lContHouseholderBean = OkHttpEngine.toObject(responsedJsonStr, ContBean.class);
                    if (lContHouseholderBean != null) {
                        switch (lContHouseholderBean.getCode()) {
                            case 0:
                                ContBean.PageObjectBean lPageObjectBean = lContHouseholderBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<DataBean> lDataBeanList = lContHouseholderBean.getData();
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
                                RxToast.error(MessageFormat.format("获取家长通讯录失败! {0}:{1}", lContHouseholderBean.getCode(), lContHouseholderBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(ContHouseholderFragment.this, MessageFormat.format("{0}/teacherInfo/getPatriarchPhoneInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mContHouseholderResponsedCallback);
    }

    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
        if (!isSupportVisible()) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_REFRESH_CONT:
                mPageIndex = 1;
                loadMoreHouseholderContacts(false);
                break;
            case MessageEvent.EVENT_TOP:
                mRvContHouseholder.smoothScrollToPosition(0);
                break;
            default:
        }
    }
}
