package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 考试类型 实体
 */
public class ExamClassBean {
    /**
     * code : 0
     * data : [{"examName":"(期中考试)考试安排一","id":1},{"examName":"(期中考试)考试安排二","id":2},{"examName":"(测试)考试安排三","id":3},{"examName":"(周考)考试安排四","id":4},{"examName":"(周考)考试安排五","id":18},{"examName":"(期中考试)考试安排六","id":20},{"examName":"(周考)高中考试安排测试","id":29},{"examName":"(周考)新增高中考试安排","id":34},{"examName":"(周考)新增高中考试安排1","id":35},{"examName":"(周考)新增高中考试安排2","id":36},{"examName":"(周考)考试安排4","id":37},{"examName":"(周考)考试安排1","id":42},{"examName":"(周考)考试安排2","id":43},{"examName":"(周考)考试安排3","id":44},{"examName":"(周考)考试安排4","id":45},{"examName":"(周考)考试安排5","id":46},{"examName":"(周考)考试安排6","id":47},{"examName":"(周考)考试安排7","id":48},{"examName":"(周考)考试安排8","id":49},{"examName":"(周考)考试安排9","id":50},{"examName":"(周考)考试安排10","id":51},{"examName":"(周考)考试安排11","id":52},{"examName":"(周考)考试安排12","id":53},{"examName":"(周考)考试安排13","id":54},{"examName":"(周考)考试安排14","id":55},{"examName":"(周考)考试安排15","id":56},{"examName":"(周考)考试安排16","id":57},{"examName":"(周考)考试安排17","id":58},{"examName":"(周考)考试安排18","id":59},{"examName":"(周考)考试安排19","id":60},{"examName":"(周考)考试安排20","id":61},{"examName":"(周考)考试安排21","id":62},{"examName":"(周考)考试安排22","id":63},{"examName":"(周考)考试安排23","id":64},{"examName":"(周考)考试安排24","id":65},{"examName":"(周考)考试安排25","id":66},{"examName":"(周考)考试安排26","id":67},{"examName":"(周考)考试安排27","id":68},{"examName":"(周考)考试安排28","id":69},{"examName":"(周考)考试安排29","id":70},{"examName":"(周考)考试安排30","id":71},{"examName":"(期末考试)2","id":76},{"examName":"(期中考试)2","id":77},{"examName":"(期中考试)3","id":78},{"examName":"(期中考试)4","id":79},{"examName":"(期中考试)5","id":80},{"examName":"(期中考试)6","id":81},{"examName":"(期中考试)7","id":82},{"examName":"(期中考试)8","id":83},{"examName":"(期中考试)9","id":84},{"examName":"(期中考试)10","id":85},{"examName":"(期中考试)11","id":86},{"examName":"(期中考试)12","id":87}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":0,"sortFields":[],"totalCount":53,"totalPages":1}
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
         * pageSize : 0
         * sortFields : []
         * totalCount : 53
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
         * examName : (期中考试)考试安排一
         * id : 1
         */

        private String examName;
        private int id;

        public String getExamName() {
            return examName;
        }

        public void setExamName(String examName) {
            this.examName = examName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
