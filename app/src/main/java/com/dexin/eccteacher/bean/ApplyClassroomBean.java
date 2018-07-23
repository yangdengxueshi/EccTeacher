package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 申请教室 实体
 */
public class ApplyClassroomBean {
    /**
     * code : 0
     * data : [{"classRoomName":"1-101(数字教室)","createrTime":1528965504000,"endTime":1533657600000,"id":3,"list":[{"createrTime":1527841674000,"id":5,"mark":"1234","name":"杨灯888","photoUrl":"\\67341529380408030Avatar_15293803389856734.JPG"}],"mark":"12345","name":"杨灯666","photoUrl":"teacherPhotos/initImageWoman.bmp","startTime":1528214400000,"type":3}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":10,"sortFields":[],"totalCount":3,"totalPages":1}
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
         * totalCount : 3
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
         * classRoomName : 1-101(数字教室)
         * createrTime : 1528965504000
         * endTime : 1533657600000
         * id : 3
         * list : [{"createrTime":1527841674000,"id":5,"mark":"1234","name":"杨灯888","photoUrl":"\\67341529380408030Avatar_15293803389856734.JPG"}]
         * mark : 12345
         * name : 杨灯666
         * photoUrl : teacherPhotos/initImageWoman.bmp
         * startTime : 1528214400000
         * type : 3
         */

        private String classRoomName;
        private long createrTime;
        private long endTime;
        private int id;
        private String mark;
        private String name;
        private String photoUrl;
        private long startTime;
        private int type;
        private List<ListBean> list;

        public String getClassRoomName() {
            return classRoomName;
        }

        public void setClassRoomName(String classRoomName) {
            this.classRoomName = classRoomName;
        }

        public long getCreaterTime() {
            return createrTime;
        }

        public void setCreaterTime(long createrTime) {
            this.createrTime = createrTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
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

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
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
             * createrTime : 1527841674000
             * id : 5
             * mark : 1234
             * name : 杨灯888
             * photoUrl : \67341529380408030Avatar_15293803389856734.JPG
             */

            private long createrTime;
            private int id;
            private String mark;
            private String name;
            private String photoUrl;

            public ListBean(long createrTime, int id, String mark, String name, String photoUrl) {
                this.createrTime = createrTime;
                this.id = id;
                this.mark = mark;
                this.name = name;
                this.photoUrl = photoUrl;
            }

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
}
