package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.NewAppBean;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更新Fragment
 */
public class UpdateFragment extends BaseDialogFragment {
    @BindView(R.id.pb_download_progress)
    ProgressBar mPbDownloadProgress;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.vs_data_update)
    ViewStub mVsDataUpdate;
    private NewAppBean.DataBean mNewAppDataBean;

    @NonNull
    public static UpdateFragment newInstance() {
        return new UpdateFragment();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_update;
    }

    @OnClick({R.id.btn_cancel_update, R.id.btn_confirm_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_update:
                dismiss();
                break;
            case R.id.btn_confirm_update:
                if (mNewAppDataBean != null) {
                    if (RxNetTool.isAvailable(CustomApplication.getContext())) {//网络可用
                        String apkFileUrl = MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, mNewAppDataBean.getUrl());
                        if (RxNetTool.isWifiConnected(CustomApplication.getContext())) {//Wifi已连接
                            downloadFile(apkFileUrl);
                        } else {//Wifi未连接
                            View lDataInflateView = mVsDataUpdate.inflate();
                            lDataInflateView.findViewById(R.id.btn_cancel_data_update).setOnClickListener(v -> {
                                dismiss();
                                RxToast.error("取消更新!");
                            });
                            lDataInflateView.findViewById(R.id.btn_confirm_data_update).setOnClickListener(v -> {
                                downloadFile(apkFileUrl);
                                RxToast.info("开始下载!");
                            });
                        }
                    } else {//网络不可用
                        EventBus.getDefault().post(MessageEvent.EVENT_NET_UNAVAILABLE);
                    }
                }
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onLibStickyEvent(NewAppBean.DataBean dataBean) {
        mNewAppDataBean = dataBean;
        mTvTitle.setText(MessageFormat.format("当前检测到''V{0}''新版本,是否更新?", mNewAppDataBean.getVersionName()));
    }

    /**
     * 下载并安装Apk文件
     *
     * @param fileUrl 文件url
     */
    private void downloadFile(String fileUrl) {
        File lApkFile = new File(AppConfig.NEW_APK_PATH);
        if (lApkFile.exists()) lApkFile.delete();
        FileDownloader.getImpl().create(fileUrl).setPath(AppConfig.NEW_APK_PATH).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (!AppConfig.isComponentAlive(UpdateFragment.this)) return;
                if (mPbDownloadProgress != null) {
                    if (mPbDownloadProgress.getVisibility() == View.GONE) mPbDownloadProgress.setVisibility(View.VISIBLE);
                    mPbDownloadProgress.setProgress(soFarBytes * 100 / totalBytes);
                }
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                RxToast.success("下载成功,开始校验!");
                String apkFileMD5 = FileUtils.getFileMD5ToString(AppConfig.NEW_APK_PATH);
                if (mNewAppDataBean.getMd5Code().equalsIgnoreCase(apkFileMD5)) {//mNewAppDataBean.getMd5Code()肯定不为空,前一步已判断!
                    RxToast.success("校验成功,开始安装!");
                    AppUtils.installApp(AppConfig.NEW_APK_PATH, "");//TODO 下载完毕直接安装
                } else {
                    RxToast.error("校验失败,文件损坏,请重试!");
                }
                if (AppConfig.isComponentAlive(UpdateFragment.this)) dismiss();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                RxToast.error("下载出错,请重试!");
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        }).start();
    }
}
