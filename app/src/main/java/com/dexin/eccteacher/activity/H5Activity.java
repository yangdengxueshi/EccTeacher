package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.widget.CoolIndicatorLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.NestedScrollAgentWebView;
import com.vondear.rxtool.RxClipboardTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;

public class H5Activity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    private AgentWeb mAgentWeb;
    private WebView mWebView;
    private String mTitle, urlToLoad;

    @Override
    protected void onResume() {
        super.onResume();
        mAgentWeb.getWebLifeCycle().onResume();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressedSupport() {
        if (mWebView.canGoBack()) {
            mAgentWeb.back();
        } else {
            super.onBackPressedSupport();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        return mAgentWeb.handleKeyEvent(keyCode, keyEvent) || super.onKeyDown(keyCode, keyEvent);
    }

    @NonNull
    public static Intent createIntent(Context context, boolean directUrl, String title, String urlLoaded) {
        return new Intent(context, H5Activity.class).putExtra("directUrl", directUrl).putExtra("title", title).putExtra("UrlLoaded", urlLoaded);
    }

    @NonNull
    public static Intent createIntent(Context context, String title, String urlLoaded, int id, int type) {
        return new Intent(context, H5Activity.class).putExtra("title", title).putExtra("UrlLoaded", urlLoaded).putExtra("id", id).putExtra("type", type);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_h5;
    }

    @Override
    protected void initView() {
        //获取待加载的Url
        Intent lIntent = getIntent();
        if (lIntent != null) {
            mTitle = lIntent.getStringExtra("title");
            if (lIntent.getBooleanExtra("directUrl", false)) {
                urlToLoad = lIntent.getStringExtra("UrlLoaded");
            } else {
                String lUrl = lIntent.getStringExtra("UrlLoaded");
                int lId = lIntent.getIntExtra("id", -1);
                int lType = lIntent.getIntExtra("type", -1);
                urlToLoad = MessageFormat.format("{0}{1}?token={2}&id={3}&type={4}", AppConfig.SERVER_HOST, lUrl, AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN), String.valueOf(lId), String.valueOf(lType));
            }
        }
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText(mTitle);
        mIvMore.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @BindView(R.id.cl_h5_container)
    CoordinatorLayout mClH5Container;

    @Override
    protected void initData() {
        CoordinatorLayout.LayoutParams lLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        mAgentWeb = AgentWeb.with(H5Activity.this)
                .setAgentWebParent(mClH5Container, 1, lLayoutParams)//index=1 否则FloatingActionButton不显示,lLayoutParams记得设置behavior属性
                .setCustomIndicator(new CoolIndicatorLayout(H5Activity.this))
                .setWebView(new NestedScrollAgentWebView(H5Activity.this))
                .setWebViewClient(new WebViewClient())
                .setWebChromeClient(new WebChromeClient())
                .setMainFrameErrorView(R.layout.custom_error_view, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时,弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl()//拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(urlToLoad);
        addBGChild(mAgentWeb.getWebCreator().getWebParentLayout());//得到 AgentWeb 最底层的控件     添加回弹效果

        mWebView = mAgentWeb.getWebCreator().getWebView();
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings lWebSettings = mWebView.getSettings();
        //region WebView设置
        lWebSettings.setBuiltInZoomControls(true);                                      //TODO 支持手势缩放
        //endregion
    }

    protected void addBGChild(FrameLayout frameLayout) {
        TextView lTextView = new TextView(H5Activity.this);
        lTextView.setGravity(Gravity.CENTER);
        lTextView.setText(R.string.web_support);
        lTextView.setLineSpacing(3, 2);
        lTextView.setTextSize(16);
        lTextView.setTextColor(Color.parseColor("#AAAAAA"));

        frameLayout.setBackgroundColor(Color.parseColor("#555555"));
        FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER_HORIZONTAL);
        lLayoutParams.topMargin = (int) (getResources().getDisplayMetrics().density * 25);
        frameLayout.addView(lTextView, 0, lLayoutParams);
    }

    @OnClick({R.id.iv_more, R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                showPoPup(view);
                break;
            case R.id.fab_top:
                mWebView.scrollTo(0, 0);
                break;
            default:
        }
    }


    private PopupMenu mPopupMenu;

    /**
     * 显示更多菜单
     *
     * @param view 菜单依附在该View下面
     */
    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(H5Activity.this, view, Gravity.BOTTOM);
            mPopupMenu.inflate(R.menu.h5_more);
            mPopupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.item_refresh:
                        mAgentWeb.getUrlLoader().reload();//刷新
                        return true;
                    case R.id.item_copy://复制链接
                        RxClipboardTool.copyText(CustomApplication.getContext(), mWebView.getUrl());
                        RxToast.success(MessageFormat.format("{0}", RxClipboardTool.getText(CustomApplication.getContext())));
                        return true;
                    case R.id.item_default_browser://系统浏览器打开
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mWebView.getUrl())));
                        return true;
                    default:
                        return false;
                }
            });
        }
        mPopupMenu.show();
    }
}
