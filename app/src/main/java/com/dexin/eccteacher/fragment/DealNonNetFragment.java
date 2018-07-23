package com.dexin.eccteacher.fragment;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;

import com.dexin.eccteacher.R;

import butterknife.OnClick;

/**
 * 处理手机无网络的Fragment
 */
public class DealNonNetFragment extends BaseDialogFragment {
    @NonNull
    public static DealNonNetFragment newInstance() {
        return new DealNonNetFragment();
    }

    @OnClick({R.id.btn_cancel_set_net, R.id.btn_confirm_set_net})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_set_net:
                dismiss();
                break;
            case R.id.btn_confirm_set_net:
                dismiss();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            default:
        }
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_net_unavailable;
    }
}
