package com.dexin.eccteacher.bean;

/**
 * 用户信息 实体
 */
public class UserInfoBean {
    /**
     * code : 0
     * data : {"name":"dts","photoUrl":"ParentAppPhotos/81527578935113.jpg","rongUserId":"P_8"}
     * message : ok
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * name : dts
         * photoUrl : ParentAppPhotos/81527578935113.jpg
         * rongUserId : P_8
         */

        private String name;
        private String photoUrl;
        private String rongUserId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }
}
