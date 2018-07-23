package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 申请请假实体
 */
public class ApplyLeaveBean {
    /**
     * code : 0
     * data : [{"comment":"谢谢","countChildren":1,"endTime":"2018-04-02 14:05:21","id":10,"list":[{"comment":"************","id":16,"name":"陈锦伟","photoUrl":"teacherPhotos/initImage.bmp","submitTime":"2018-04-27 17:09:47"}],"name":"赵武","photoUrl":"ParentAppPhotos/41525924129136.jpg","reason":"病假","startTime":"2018-04-02 14:05:21","submitTime":"2018-06-11 16:14:32","type":0}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":10,"sortFields":[],"totalCount":1,"totalPages":1}
     */

    private int code;
    private String message;
    private PageObjectBean pageObject;
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

    public PageObjectBean getPageObject() {
        return pageObject;
    }

    public void setPageObject(PageObjectBean pageObject) {
        this.pageObject = pageObject;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class PageObjectBean {
        /**
         * firstIsZero : false
         * orderByString :
         * pageIndex : 1
         * pageSize : 10
         * sortFields : []
         * totalCount : 1
         * totalPages : 1
         */

        private boolean firstIsZero;
        private String orderByString;
        private int pageIndex;
        private int pageSize;
        private int totalCount;
        private int totalPages;
        private List<?> sortFields;

        public boolean isFirstIsZero() {
            return firstIsZero;
        }

        public void setFirstIsZero(boolean firstIsZero) {
            this.firstIsZero = firstIsZero;
        }

        public String getOrderByString() {
            return orderByString;
        }

        public void setOrderByString(String orderByString) {
            this.orderByString = orderByString;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<?> getSortFields() {
            return sortFields;
        }

        public void setSortFields(List<?> sortFields) {
            this.sortFields = sortFields;
        }
    }

    public static class DataBean {
        /**
         * comment : 谢谢
         * countChildren : 1
         * endTime : 2018-04-02 14:05:21
         * id : 10
         * list : [{"comment":"************","id":16,"name":"陈锦伟","photoUrl":"teacherPhotos/initImage.bmp","submitTime":"2018-04-27 17:09:47"}]
         * name : 赵武
         * photoUrl : ParentAppPhotos/41525924129136.jpg
         * reason : 病假
         * startTime : 2018-04-02 14:05:21
         * submitTime : 2018-06-11 16:14:32
         * type : 0
         */

        private String comment;
        private int countChildren;
        private String endTime;
        private int id;
        private String name;
        private String photoUrl;
        private String reason;
        private String startTime;
        private String submitTime;
        private int type;
        private List<ListBean> list;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getCountChildren() {
            return countChildren;
        }

        public void setCountChildren(int countChildren) {
            this.countChildren = countChildren;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
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

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getSubmitTime() {
            return submitTime;
        }

        public void setSubmitTime(String submitTime) {
            this.submitTime = submitTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * comment : ************
             * id : 16
             * name : 陈锦伟
             * photoUrl : teacherPhotos/initImage.bmp
             * submitTime : 2018-04-27 17:09:47
             */

            private String comment;
            private int id;
            private String name;
            private String photoUrl;
            private String submitTime;

            public ListBean(String comment, int id, String name, String photoUrl, String submitTime) {
                this.comment = comment;
                this.id = id;
                this.name = name;
                this.photoUrl = photoUrl;
                this.submitTime = submitTime;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
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

            public String getPhotoUrl() {
                return photoUrl;
            }

            public void setPhotoUrl(String photoUrl) {
                this.photoUrl = photoUrl;
            }

            public String getSubmitTime() {
                return submitTime;
            }

            public void setSubmitTime(String submitTime) {
                this.submitTime = submitTime;
            }
        }
    }
}