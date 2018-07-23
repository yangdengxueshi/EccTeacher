package com.dexin.eccteacher.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 修改评分Fragment
 */
public class ModifyMarkFragment extends BaseDialogFragment {
    @BindView(R.id.tv_mark_name_and_value)
    TextView mTvMarkNameAndValue;
    @BindView(R.id.rb_mark)
    RatingBar mRbMark;
    @BindView(R.id.et_mark_comment)
    EditText mEtMarkComment;
    private int mMarkId, mTotalStar;
    private float mScore, mStarScore;
    private String mName, mMark;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle lArgumentsBundle = getArguments();
        if (lArgumentsBundle != null) {
            mMarkId = lArgumentsBundle.getInt("id");
            mMark = lArgumentsBundle.getString("mark");
            mScore = lArgumentsBundle.getFloat("score");
            mName = lArgumentsBundle.getString("name");
            mStarScore = lArgumentsBundle.getFloat("starScore");
            mTotalStar = lArgumentsBundle.getInt("totalStar");
        }
        mTvMarkNameAndValue.setText(MessageFormat.format("{0}:{1}", mName, mScore));
        mRbMark.setNumStars(mTotalStar);
        mRbMark.setRating((mStarScore != 0) ? (mScore / mStarScore) : 0);
        mRbMark.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) mTvMarkNameAndValue.setText(MessageFormat.format("{0}:{1}", mName, rating * mStarScore));
        });
        mEtMarkComment.setText(!TextUtils.isEmpty(mMark) ? mMark : "");
        mEtMarkComment.setSelection(mEtMarkComment.getText().toString().length());
    }

    public static ModifyMarkFragment newInstance(int id, String mark, float score, String name, float starScore, int totalStar) {
        ModifyMarkFragment fragment = new ModifyMarkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("mark", mark);
        bundle.putFloat("score", score);
        bundle.putString("name", name);
        bundle.putFloat("starScore", starScore);
        bundle.putInt("totalStar", totalStar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_modify_mark;
    }

    @OnClick(R.id.btn_confirm_modify)
    public void onClick() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(ModifyMarkFragment.this, MessageFormat.format("{0}/gradePoint/updateGradePointId", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mMarkId)).add("mark", mEtMarkComment.getText().toString()).add("score", String.valueOf(mRbMark.getRating() * mStarScore)).add("star", String.valueOf((mRbMark.getRating()))).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                new MStatusDialog(getActivity(), new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("操作失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ResponseResultBean lResponseResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                if (lResponseResultBean != null) {
                    switch (lResponseResultBean.getCode()) {
                        case 0://成功:
                            new MStatusDialog(getActivity()).show("修改成功!", getResources().getDrawable(R.drawable.ic_success));
                            dismiss();
                            break;
                        default:
                            RxToast.error("修改失败! " + lResponseResultBean.getCode() + ":" + lResponseResultBean.getMessage());
                    }
                }
            }
        });
    }
}
