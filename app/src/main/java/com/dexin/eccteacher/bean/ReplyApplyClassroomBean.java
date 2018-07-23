package com.dexin.eccteacher.bean;

/**
 * 回复我的教室申请 实体
 */
public final class ReplyApplyClassroomBean {
    /**
     * code : 0
     * data : {"createrTime":1530102932439,"id":23,"mark":"不可以","name":"杨灯666","photoUrl":"teacherPhotos/69701530100200962Avatar_15300996535926970.JPG"}
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
         * createrTime : 1530102932439
         * id : 23
         * mark : 不可以
         * name : 杨灯666
         * photoUrl : teacherPhotos/69701530100200962Avatar_15300996535926970.JPG
         */

        private long createrTime;
        private int id;
        private String mark;
        private String name;
        private String photoUrl;

        public long getCreaterTime() {
            return createrTime;
        }

        public void setCreaterTime(long createrTime) {
            this.createrTime = createrTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

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
    }
}
