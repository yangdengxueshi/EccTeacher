package com.dexin.eccteacher.bean;

/**
 * 首页菜单项实体
 */
public class HomeEntranceBean {
    private int imageId;
    private String name;

    public HomeEntranceBean(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
