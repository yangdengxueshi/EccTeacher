package com.dexin.eccteacher.bean;

/**
 * 回复故障报修 实体
 */
public class ReplyFaultBean {
    /**
     * code : 0
     * data : {"createrTime":1530513861063,"id":14,"markInfo":"123","name":"杨灯888","photoUrl":"teacherPhotos/67341530265449096Avatar_15302653647936734.JPG"}
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
         * createrTime : 1530513861063
         * id : 14
         * markInfo : 123
         * name : 杨灯888
         * photoUrl : teacherPhotos/67341530265449096Avatar_15302653647936734.JPG
         */

        private long createrTime;
        private int id;
        private String markInfo;
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

        public String getMarkInfo() {
            return markInfo;
        }

        public void setMarkInfo(String markInfo) {
            this.markInfo = markInfo;
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
