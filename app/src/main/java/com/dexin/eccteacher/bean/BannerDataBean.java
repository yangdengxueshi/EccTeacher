package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * Banner数据实体
 */
public class BannerDataBean {
    /**
     * code : 0
     * data : [{"createTime":"2018-03-12 10:18:48","doType":"1","doUrl":"","id":11,"name":"图片1","photoType":1,"photoUrl":"teacherPhotos/chemistry.png","schoolId":7},{"createTime":"2018-03-12 10:18:48","doType":"1","doUrl":"","id":12,"name":"图片2","photoType":1,"photoUrl":"teacherPhotos/biography.png","schoolId":7},{"createTime":"2018-03-12 10:18:48","doType":"1","doUrl":"","id":13,"name":"图片3","photoType":1,"photoUrl":"teacherPhotos/biography.png","schoolId":7},{"createTime":"2018-03-12 10:18:48","doType":"1","doUrl":"","id":14,"name":"图片4","photoType":1,"photoUrl":"teacherPhotos/biography.png","schoolId":7},{"createTime":"2018-03-12 10:18:48","doType":"1","doUrl":"","id":15,"name":"图片5","photoType":1,"photoUrl":"teacherPhotos/biography.png","schoolId":7}]
     * message : ok
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * createTime : 2018-03-12 10:18:48
         * doStatus : 0
         * doType : 1
         * doUrl :
         * titles
         * id : 11
         * name : 图片1
         * photoType : 1
         * photoUrl : teacherPhotos/chemistry.png
         * schoolId : 7
         */

        private String createTime;
        private int doStatus;
        private int doType;
        private String doUrl;
        private String titles;
        private int id;
        private String name;
        private int photoType;
        private String photoUrl;
        private int schoolId;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getDoStatus() {
            return doStatus;
        }

        public void setDoStatus(int doStatus) {
            this.doStatus = doStatus;
        }

        public int getDoType() {
            return doType;
        }

        public void setDoType(int doType) {
            this.doType = doType;
        }

        public String getDoUrl() {
            return doUrl;
        }

        public void setDoUrl(String doUrl) {
            this.doUrl = doUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPhotoType() {
            return photoType;
        }

        public void setPhotoType(int photoType) {
            this.photoType = photoType;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }
    }
}
