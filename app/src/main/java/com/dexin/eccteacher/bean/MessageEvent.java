package com.dexin.eccteacher.bean;

import org.jetbrains.annotations.Contract;

/**
 * Event事件
 */
public final class MessageEvent {
    public static final String EVENT_LOAD_TEACHER_INFO = "EVENT_LOAD_TEACHER_INFO";//加载教师信息 事件
    public static final String EVENT_REFRESH_CONT = "EVENT_REFRESH_CONT";//刷新联系人 事件
    public static final String EVENT_REFRESH_CLASSROOM = "EVENT_REFRESH_CLASSROOM";//刷新联系人 事件
    public static final String EVENT_CANCEL_REFRESH_CONT = "EVENT_CANCEL_REFRESH_CONT";//取消刷新联系人 事件
    public static final String EVENT_NEW_APP = "EVENT_NEW_APP";//发现新版本App事件
    public static final String EVENT_NET_UNAVAILABLE = "EVENT_NET_UNAVAILABLE";//网络不可用 事件
    public static final String EVENT_TOP = "EVENT_TOP";//置顶事件
    public static final String EVENT_FINISH_REFRESH = "EVENT_FINISH_REFRESH";//结束刷新

    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    @Contract(pure = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
