package com.dexin.eccteacher.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 评分Fragment
 */
public class DoMarkFragment extends BaseFragment {
    @BindView(R.id.rb_mark)
    RatingBar mRbMark;
    @BindView(R.id.et_mark_comment)
    EditText mEtMarkComment;
    @BindView(R.id.btn_confirm_submit)
    Button mBtnConfirmSubmit;
    private int mClassId, mMarkClassId, mTotalStar;
    private float mTotalScore;

    public static DoMarkFragment newInstance(int classId, int markClassId, int totalStar, float totalScore) {
        DoMarkFragment fragment = new DoMarkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("classId", classId);
        bundle.putInt("markClassId", markClassId);
        bundle.putInt("totalStar", totalStar);
        bundle.putFloat("totalScore", totalScore);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_do_mark;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        Bundle lArgumentsBundle = getArguments();
        if (lArgumentsBundle != null) {
            mClassId = lArgumentsBundle.getInt("classId");
            mMarkClassId = lArgumentsBundle.getInt("markClassId");
            mTotalStar = lArgumentsBundle.getInt("totalStar");
            mTotalScore = lArgumentsBundle.getFloat("totalScore");
        }
        mRbMark.setNumStars(mTotalStar);
    }

    @Override
    protected void initListener() {
        mRbMark.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) EventBus.getDefault().postSticky(rating);
        });
    }

    @Override
    protected void initData() {
    }

    @OnClick({R.id.btn_confirm_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm_submit:
                doMarkAction();
                break;
            default:
        }
    }

    private void doMarkAction() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(DoMarkFragment.this, MessageFormat.format("{0}/gradePoint/addGradePoint", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("adminClassId", String.valueOf(mClassId)).add("gradeSysId", String.valueOf(mMarkClassId)).add("mark", mEtMarkComment.getText().toString()).add("score", (mTotalStar != 0) ? String.valueOf(((mRbMark.getRating() * (mTotalScore / mTotalStar)))) : "0").add("star", String.valueOf((mRbMark.getRating()))).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,评分失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lMarkResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lMarkResultBean != null) {
                    switch (lMarkResultBean.getCode()) {
                        case 0://成功:
                            RxToast.success("评分成功!");
                            mBtnConfirmSubmit.setBackgroundColor(Color.GRAY);
                            mBtnConfirmSubmit.setEnabled(false);
                            break;
                        default:
                            RxToast.error(MessageFormat.format("评分失败! {0}:{1}", lMarkResultBean.getCode(), lMarkResultBean.getMessage()));
                    }
                } else {
                    RxToast.error("解析为空,评分失败!");
                }
            }
        });
    }
}
