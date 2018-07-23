package com.dexin.eccteacher.fragment;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.dexin.eccteacher.R;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * 会话容器Fragment
 */
public class ConversationContainerFragment extends BaseFragment {
    @NonNull
    public static ConversationContainerFragment newInstance() {
        return new ConversationContainerFragment();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_conversation_container;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);

        ConversationListFragment conversationListFragment = new ConversationListFragment();//设置私聊会话是否聚合显示   设置系统会话非聚合显示
        conversationListFragment.setUri(Uri.parse("rong://" + mFragmentActivity.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false").appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true").build());
        getChildFragmentManager().beginTransaction().add(R.id.fl_fragment_container, conversationListFragment).commit();
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }
}
