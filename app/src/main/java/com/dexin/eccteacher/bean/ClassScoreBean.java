package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 班级分数 实体
 */
public class ClassScoreBean {
    /**
     * code : 0
     * data : [{"id":3,"rankingAdminClass":1,"rankingGrade":1,"rankingTeachClass":41,"scoreSum":712,"student":{"code":"abc123","dataType":1,"id":3,"name":"李莉"}},{"id":10,"rankingAdminClass":2,"rankingGrade":2,"rankingTeachClass":40,"scoreSum":697,"student":{"code":"1","dataType":1,"id":10,"name":"丁柯月"}},{"id":5,"rankingAdminClass":3,"rankingGrade":3,"rankingTeachClass":39,"scoreSum":696,"student":{"code":"1","dataType":1,"id":5,"name":"张丽丽"}}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":3,"sortFields":[],"totalCount":41,"totalPages":14}
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
         * pageSize : 3
         * sortFields : []
         * totalCount : 41
         * totalPages : 14
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
         * id : 3
         * rankingAdminClass : 1
         * rankingGrade : 1
         * rankingTeachClass : 41
         * scoreSum : 712
         * student : {"code":"abc123","dataType":1,"id":3,"name":"李莉"}
         */

        private int id;
        private int rankingAdminClass;
        private int rankingGrade;
        private int rankingTeachClass;
        private int scoreSum;
        private StudentBean student;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRankingAdminClass() {
            return rankingAdminClass;
        }

        public void setRankingAdminClass(int rankingAdminClass) {
            this.rankingAdminClass = rankingAdminClass;
        }

        public int getRankingGrade() {
            return rankingGrade;
        }

        public void setRankingGrade(int rankingGrade) {
            this.rankingGrade = rankingGrade;
        }

        public int getRankingTeachClass() {
            return rankingTeachClass;
        }

        public void setRankingTeachClass(int rankingTeachClass) {
            this.rankingTeachClass = rankingTeachClass;
        }

        public int getScoreSum() {
            return scoreSum;
        }

        public void setScoreSum(int scoreSum) {
            this.scoreSum = scoreSum;
        }

        public StudentBean getStudent() {
            return student;
        }

        public void setStudent(StudentBean student) {
            this.student = student;
        }

        public static class StudentBean {
            /**
             * code : abc123
             * dataType : 1
             * id : 3
             * name : 李莉
             */

            private String code;
            private int dataType;
            private int id;
            private String name;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public int getDataType() {
                return dataType;
            }

            public void setDataType(int dataType) {
                this.dataType = dataType;
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
        }
    }
}
