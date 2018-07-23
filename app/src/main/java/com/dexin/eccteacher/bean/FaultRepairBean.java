package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 故障报修 实体
 */
public class FaultRepairBean {
    /**
     * code : 0
     * data : [{"createrTime":1529998090000,"id":1,"informUrl":"***","list":[{"createrTime":1529998090000,"id":2,"markInfo":"","name":"敖小念","photoUrl":"teacherPhotos/initImageMan.png"}],"name":"敖小念","photoUrl":"teacherPhotos/initImageMan.png","type":3,"typeName":"桌椅"}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":10,"sortFields":[],"totalCount":4,"totalPages":1}
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
         * totalCount : 4
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
         * createrTime : 1529998090000
         * id : 1
         * informUrl : ***
         * mark: 313321321,
         * list : [{"createrTime":1529998090000,"id":2,"markInfo":"","name":"敖小念","photoUrl":"teacherPhotos/initImageMan.png"}]
         * name : 敖小念
         * photoUrl : teacherPhotos/initImageMan.png
         * type : 3
         * typeName : 桌椅
         */

        private long createrTime;
        private int id;
        private String informUrl;
        private String mark;
        private String name;
        private String photoUrl;
        private int type;
        private String typeName;
        private List<ListBean> list;

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

        public String getInformUrl() {
            return informUrl;
        }

        public void setInformUrl(String informUrl) {
            this.informUrl = informUrl;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * createrTime : 1529998090000
             * id : 2
             * markInfo :
             * name : 敖小念
             * photoUrl : teacherPhotos/initImageMan.png
             */

            private long createrTime;
            private int id;
            private String markInfo;
            private String name;
            private String photoUrl;

            public ListBean(long createrTime, int id, String markInfo, String name, String photoUrl) {
                this.createrTime = createrTime;
                this.id = id;
                this.markInfo = markInfo;
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
}
