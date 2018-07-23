package com.dexin.eccteacher.bean;

/**
 * 回复意见反馈实体
 */
public class ReplyFeedbackBean {
    /**
     * code : 0
     * data : {"content":"13e","countChildren":0,"createTime":"2018-06-07 16:38:50","creatorName":"杨灯888","id":48,"parentId":47,"photoUrl":"teacherPhotos\\67341528164592962Avatar_15281641104436734.JPG"}
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
         * content : 13e
         * countChildren : 0
         * createTime : 2018-06-07 16:38:50
         * creatorName : 杨灯888
         * id : 48
         * parentId : 47
         * photoUrl : teacherPhotos\67341528164592962Avatar_15281641104436734.JPG
         */

        private String content;
        private int countChildren;
        private String createTime;
        private String creatorName;
        private int id;
        private int parentId;
        private String photoUrl;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getCountChildren() {
            return countChildren;
        }

        public void setCountChildren(int countChildren) {
            this.countChildren = countChildren;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
}
