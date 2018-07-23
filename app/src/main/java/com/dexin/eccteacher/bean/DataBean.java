package com.dexin.eccteacher.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class DataBean extends LitePalSupport {
    /**
     * name : 杨灯888
     * phone : 17078020229
     * photoUrl :
     * rongUserId : T_6734
     */

    private String name;
    @Column(ignore = true)
    private String phone;
    @Column(ignore = true)
    private String photoUrl;
    private String rongUserId;
    private boolean updateStatus;

    public DataBean() {
    }

    public DataBean(String name, String phone, String photoUrl, String rongUserId) {
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.rongUserId = rongUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRongUserId() {
        return rongUserId;
    }

    public void setRongUserId(String rongUserId) {
        this.rongUserId = rongUserId;
    }

    public boolean isUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(boolean updateStatus) {
        this.updateStatus = updateStatus;
    }
}
