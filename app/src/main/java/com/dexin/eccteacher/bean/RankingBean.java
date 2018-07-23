package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 德育评分排名 实体
 */
public class RankingBean {
    /**
     * code : 0
     * data : [{"adminClassId":59,"adminClassName":"高中一年级二班","ranking":1,"scoreSum":29},{"adminClassId":58,"adminClassName":"高中一年级一班","ranking":2,"scoreSum":29}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":10,"sortFields":[],"totalCount":2,"totalPages":1}
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
         * totalCount : 2
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
         * adminClassId : 59
         * adminClassName : 高中一年级二班
         * ranking : 1
         * scoreSum : 29
         */

        private int adminClassId;
        private String adminClassName;
        private int ranking;
        private float scoreSum;

        public int getAdminClassId() {
            return adminClassId;
        }

        public void setAdminClassId(int adminClassId) {
            this.adminClassId = adminClassId;
        }

        public String getAdminClassName() {
            return adminClassName;
        }

        public void setAdminClassName(String adminClassName) {
            this.adminClassName = adminClassName;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public float getScoreSum() {
            return scoreSum;
        }

        public void setScoreSum(float scoreSum) {
            this.scoreSum = scoreSum;
        }
    }
}
