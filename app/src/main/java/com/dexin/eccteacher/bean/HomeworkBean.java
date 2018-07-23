package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 家庭作业实体
 */
public class HomeworkBean {
    /**
     * code : 0
     * data : [{"createName":"dx杨灯","createTime":"2018-5-30 21:00","digest":"1234556","gradeAdminName":"高中一年级一班","h5Url":"student_app_cloud/listDetail.html","headline":"1234","id":0},{"createName":"dx杨灯","createTime":""2018-5-30 21:00"","digest":"发布测试作业数据","gradeAdminName":"高中一年级二班","h5Url":"student_app_cloud/listDetail.html","headline":"测试作业","id":0}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":2,"sortFields":[],"totalCount":3,"totalPages":2}
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
         * totalCount : 3
         * totalPages : 2
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
         * createName : dx杨灯
         * createTime : 2018-5-30 21:00:00
         * digest : 1234556
         * gradeAdminName : 高中一年级一班
         * h5Url : student_app_cloud/listDetail.html
         * headline : 1234
         * id : 0
         */

        private String createName;
        private String createTime;
        private String digest;
        private String gradeAdminName;
        private String h5Url;
        private String headline;
        private int id;

        public String getCreateName() {
            return createName;
        }

        public void setCreateName(String createName) {
            this.createName = createName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
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

        public String getH5Url() {
            return h5Url;
        }

        public void setH5Url(String h5Url) {
            this.h5Url = h5Url;
        }

        public String getHeadline() {
            return headline;
        }

        public void setHeadline(String headline) {
            this.headline = headline;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
