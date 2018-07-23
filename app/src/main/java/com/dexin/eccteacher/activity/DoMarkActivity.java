package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ClassAdapter;
import com.dexin.eccteacher.adapter.TabFragmentAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ClassBean;
import com.dexin.eccteacher.bean.GradeBean;
import com.dexin.eccteacher.bean.RankClassBean;
import com.dexin.eccteacher.fragment.DoMarkFragment;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;
import q.rorbin.badgeview.QBadgeView;

public class DoMarkActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.s_select_grade)
    Spinner mSSelectGrade;
    @BindView(R.id.s_select_class)
    Spinner mSSelectClass;
    @BindView(R.id.tl_mark_classify)
    TabLayout mTlMarkClassify;
    @BindView(R.id.vp_mark_classify)
    ViewPager mVpMarkClassify;
    private int mGradeId, mClassId;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, DoMarkActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_do_mark;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("评分");
        mSSelectClass.setEnabled(false);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    public void initData() {
        loadGradeData();
    }

    /**
     * 加载年级数据
     */
    private void loadGradeData() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoMarkActivity.this, MessageFormat.format("{0}/ClassInform/getGradeInfoAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'年级列表'失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                GradeBean lGradeBean = OkHttpEngine.toObject(responsedJsonStr, GradeBean.class);
                if (lGradeBean != null) {
                    switch (lGradeBean.getCode()) {
                        case 0://成功
                            List<GradeBean.DataBean> lGradeList = lGradeBean.getData();
                            if ((lGradeList != null) && !lGradeList.isEmpty()) {
                                lGradeList.add(0, new GradeBean.DataBean(Integer.MAX_VALUE, "年级选择"));
                                GradeAdapter lGradeAdapter = new GradeAdapter(DoMarkActivity.this, R.layout.item_drop_down_menu, lGradeList);
                                mSSelectGrade.setAdapter(lGradeAdapter);
                                mSSelectGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        GradeBean.DataBean lGradeBean = lGradeList.get(position);
                                        if (lGradeBean != null) mGradeId = lGradeBean.getId();
                                        if (mGradeId != Integer.MAX_VALUE) loadClassData(mGradeId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("服务器上无年级列表!");
                            }
                            break;
                        default:
                            RxToast.error("获取'年级列表'失败! " + lGradeBean.getCode() + ":" + lGradeBean.getMessage());
                    }
                }
            }

            class GradeAdapter extends ArrayAdapter<GradeBean.DataBean> {
                private int itemLayoutResId;

                GradeAdapter(@NonNull Context context, int resource, @NonNull List<GradeBean.DataBean> dataBeanList) {
                    super(context, resource, dataBeanList);
                    itemLayoutResId = resource;
                }

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    GradeBean.DataBean lGradeClassBean = getItem(position);
                    if (lGradeClassBean != null) {
                        ViewHolder lViewHolder;
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                            lViewHolder = new ViewHolder();
                            lViewHolder.mTvGradeOrClass = convertView.findViewById(R.id.tv_item);
                            convertView.setTag(lViewHolder);
                        } else {
                            lViewHolder = (ViewHolder) convertView.getTag();
                        }
                        lViewHolder.mTvGradeOrClass.setText(lGradeClassBean.getName());
                    }
                    return convertView;
                }

                class ViewHolder {
                    TextView mTvGradeOrClass;
                }
            }
        });
    }

    /**
     * 根据年级id 加载其下面的班级信息
     *
     * @param gradeId 年级id
     */
    private void loadClassData(int gradeId) {
        if (!mSSelectClass.isEnabled()) mSSelectClass.setEnabled(true);
        String urlClass = MessageFormat.format("{0}/ClassInform/getAdminClassInfo", AppConfig.SERVER_ADDRESS);
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoMarkActivity.this, urlClass, new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("gradeId", String.valueOf(gradeId)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'年级下班级列表'失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ClassBean lClassBean = OkHttpEngine.toObject(responsedJsonStr, ClassBean.class);
                if (lClassBean != null) {
                    switch (lClassBean.getCode()) {
                        case 0://成功
                            List<ClassBean.DataBean> lClassBeanList = lClassBean.getData();
                            if (lClassBeanList != null && !lClassBeanList.isEmpty()) {
                                lClassBeanList.add(0, new ClassBean.DataBean(Integer.MAX_VALUE, "班级选择"));
                                ClassAdapter lClassAdapter = new ClassAdapter(DoMarkActivity.this, R.layout.item_drop_down_menu, lClassBeanList);
                                mSSelectClass.setAdapter(lClassAdapter);
                                mSSelectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        ClassBean.DataBean lClassBean = lClassBeanList.get(position);
                                        mClassId = lClassBean.getId();
                                        if (mClassId != Integer.MAX_VALUE) loadMarkClass(mClassId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                RxToast.info("当前年级下无班级!");
                                mSSelectClass.setEnabled(false);
                            }
                            break;
                        default:
                            RxToast.error("获取'年级下班级列表'失败! " + lClassBean.getCode() + ":" + lClassBean.getMessage());
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 加载"班级评分类别Fragment"模块-------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-------------------------------------------------

    /**
     * 加载"班级评分"类别
     *
     * @param classId 班级id
     */
    private void loadMarkClass(int classId) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoMarkActivity.this, MessageFormat.format("{0}/gradePoint/getGradeSys", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("adminClassId", String.valueOf(classId)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,获取'班级评分类别'失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                RankClassBean lRankClassBean = OkHttpEngine.toObject(responsedJsonStr, RankClassBean.class);
                if (lRankClassBean != null) {
                    switch (lRankClassBean.getCode()) {
                        case 0://成功
                            List<RankClassBean.DataBean> lRankBeanList = lRankClassBean.getData();
                            if ((lRankBeanList != null) && !lRankBeanList.isEmpty()) {
                                initTabAndViewPager(lRankBeanList);
                            } else {
                                RxToast.error("服务器上未添加'班级评分类别'数据!");
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'班级评分类别'失败! {0}:{1}", lRankClassBean.getCode(), lRankClassBean.getMessage()));
                    }
                }
            }
        });
    }

    private List<String> mTabTitleList;
    private List<Fragment> mFragmentList;
    private TabFragmentAdapter mTabFragmentAdapter;

    private void initTabAndViewPager(List<RankClassBean.DataBean> rankBeanList) {
        mTlMarkClassify.removeAllTabs();
        mVpMarkClassify.removeAllViews();
        if (mTabTitleList == null) mTabTitleList = new ArrayList<>();
        if (mFragmentList == null) mFragmentList = new ArrayList<>();
        mTabTitleList.clear();
        mFragmentList.clear();
        for (RankClassBean.DataBean rankClassBean : rankBeanList) {
            if (rankClassBean != null) {
                mTabTitleList.add(rankClassBean.getName());
                mFragmentList.add(DoMarkFragment.newInstance(mClassId, rankClassBean.getId(), rankClassBean.getTotalStar(), rankClassBean.getTotalScore()));
            }
        }
        mVpMarkClassify.setOffscreenPageLimit(50);
        if (mTabFragmentAdapter == null) {
            mTabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), mTabTitleList, mFragmentList);
            mVpMarkClassify.setAdapter(mTabFragmentAdapter);
            mTlMarkClassify.setupWithViewPager(mVpMarkClassify);
        } else {
            mTabFragmentAdapter.notifyDataSetChanged();
        }
        mVpMarkClassify.setCurrentItem(0);
        initTabBadge();
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 设置Tablayout Badge 模块-----------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-----------------------------------------------------
    private void initTabBadge() {
        for (int i = 0; i < mFragmentList.size(); i++) updateTabBadge(i, "0.0");
    }

    private void updateTabBadge(int position, String badgeText) {
        TabLayout.Tab lTabAt = mTlMarkClassify.getTabAt(position % mTlMarkClassify.getTabCount());
        if (lTabAt != null) {
            View customView = lTabAt.getCustomView();//更新Badge前,先remove原来的customView,否则Badge无法更新
            if (customView != null) {
                ViewParent parent = customView.getParent();
                if (parent != null) ((ViewGroup) parent).removeView(customView);
            }
            // 更新CustomView
            View tabItemView = LayoutInflater.from(DoMarkActivity.this).inflate(R.layout.item_mark_tab, null, false);
            TextView tvTabTitle = tabItemView.findViewById(R.id.tv_tab_title);
            tvTabTitle.setText(mTabTitleList.get(position));
            new QBadgeView(CustomApplication.getContext()).setBadgeGravity(Gravity.BOTTOM | Gravity.CENTER).setBadgeText(badgeText).bindTarget(tabItemView.findViewById(R.id.v_badge_target));
            lTabAt.setCustomView(tabItemView);
        }
        // 需加上以下代码,不然会出现更新Tab角标后,选中的Tab字体颜色不是选中状态的颜色
        TabLayout.Tab lTab = mTlMarkClassify.getTabAt(mTlMarkClassify.getSelectedTabPosition());
        if (lTab != null) {
            View customView = lTab.getCustomView();
            if (customView != null) customView.setSelected(true);
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onLibStickyEvent(Float selectedScore) {
        if (!AppConfig.isComponentAlive(DoMarkActivity.this)) return;
        updateTabBadge(mTlMarkClassify.getSelectedTabPosition(), String.valueOf(selectedScore));
    }
}
