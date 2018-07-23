package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 评分详情 实体
 */
public class MarkingDetailBean {
    /**
     * code : 0
     * data : [{"createtorName":"123","gradeSysInfo":{"id":3,"name":"眼保健操","photo":"teacherPhotos/deyustar.png"},"id":3,"mark":"123456","score":9,"sign":1,"star":4,"time":"2018-06-04 13:44:26"},{"createtorName":"123","gradeSysInfo":{"id":1,"name":"卫生","photo":"teacherPhotos/deyustar.png"},"id":1,"mark":"123456","score":8,"sign":1,"star":4,"time":"2018-06-04 13:44:26"}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":2,"sortFields":[],"totalCount":4,"totalPages":2}
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
         * totalCount : 4
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
         * createtorName : 123
         * gradeSysInfo : {"id":3,"name":"眼保健操","photo":"teacherPhotos/deyustar.png","starScore": 0,"totalStar": 5}
         * id : 3
         * mark : 123456
         * score : 9
         * sign : 1
         * star : 4
         * time : 2018-06-04 13:44:26
         */

        private String createtorName;
        private GradeSysInfoBean gradeSysInfo;
        private int id;
        private String mark;
        private float score;
        private int sign;
        private float star;
        private String time;

        public String getCreatetorName() {
            return createtorName;
        }

        public void setCreatetorName(String createtorName) {
            this.createtorName = createtorName;
        }

        public GradeSysInfoBean getGradeSysInfo() {
            return gradeSysInfo;
        }

        public void setGradeSysInfo(GradeSysInfoBean gradeSysInfo) {
            this.gradeSysInfo = gradeSysInfo;
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

        public float getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getSign() {
            return sign;
        }

        public void setSign(int sign) {
            this.sign = sign;
        }

        public float getStar() {
            return star;
        }

        public void setStar(float star) {
            this.star = star;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public static class GradeSysInfoBean {
            /**
             * id : 3
             * name : 眼保健操
             * photo : teacherPhotos/deyustar.png
             * starScore: 0
             * totalStar: 5
             */

            private int id;
            private String name;
            private String photo;
            private float starScore;
            private int totalStar;

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

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public float getStarScore() {
                return starScore;
            }

            public void setStarScore(float starScore) {
                this.starScore = starScore;
            }

            public int getTotalStar() {
                return totalStar;
            }

            public void setTotalStar(int totalStar) {
                this.totalStar = totalStar;
            }
        }
    }
}
