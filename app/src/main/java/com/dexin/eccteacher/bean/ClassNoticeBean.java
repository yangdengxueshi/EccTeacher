package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 班级通知实体
 */
public class ClassNoticeBean {
    /**
     * code : 0
     * data : [{"createTime":1527663118000,"creatorName":"陈锦伟","digest":"班级通知（一）","gradeAdminName":"高中一年级测试1","id":106,"informUrl":"student_app_cloud/listDetail.html;","photoUrl":"teacherPhotos/initImage.bmp","theme":"班级通知（一）"},{"createTime":1527651495000,"creatorName":"陈锦伟","digest":"班级通知（一）","gradeAdminName":"高中一年级测试1","id":105,"informUrl":"student_app_cloud/listDetail.html;","photoUrl":"teacherPhotos/initImage.bmp","theme":"班级通知（一）"}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":2,"sortFields":[],"totalCount":93,"totalPages":47}
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
         * pageSize : 2
         * sortFields : []
         * totalCount : 93
         * totalPages : 47
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
         * createTime : 1527663118000
         * creatorName : 陈锦伟
         * digest : 班级通知（一）
         * gradeAdminName : 高中一年级测试1
         * id : 106
         * informUrl : student_app_cloud/listDetail.html;
         * photoUrl : teacherPhotos/initImage.bmp
         * theme : 班级通知（一）
         */

        private long createTime;
        private String creatorName;
        private String digest;
        private String gradeAdminName;
        private int id;
        private String informUrl;
        private String photoUrl;
        private String theme;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public String getGradeAdminName() {
            return gradeAdminName;
        }

        public void setGradeAdminName(String gradeAdminName) {
            this.gradeAdminName = gradeAdminName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getInformUrl() {
            return informUrl;
        }

        public void setInformUrl(String informUrl) {
            this.informUrl = informUrl;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }
    }
}
