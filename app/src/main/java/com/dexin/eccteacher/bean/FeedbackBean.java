package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 意见反馈 实体
 */
public class FeedbackBean {
    /**
     * code : 0
     * data : [{"children":[{"children":[],"content":"111112233","countChildren":0,"createTime":"2018-05-30 18:23:49","creatorName":"13017714697","id":42,"photoUrl":"ParentAppPhotos/001.bmp","status":1}],"content":"111111","countChildren":1,"createTime":"2018-05-30 18:23:32","creatorName":"13017714697","id":41,"photoUrl":"ParentAppPhotos/001.bmp","status":2},{"children":[{"children":[],"content":"试个传传","countChildren":0,"createTime":"2018-05-29 15:31:52","creatorName":"我是你二大爷","id":40,"photoUrl":"ParentAppPhotos/001.bmp","status":1}],"content":"我就试一下","countChildren":1,"createTime":"2018-05-29 15:31:33","creatorName":"我是你二大爷","id":39,"photoUrl":"ParentAppPhotos/001.bmp","status":2}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":0,"sortFields":[],"totalCount":6,"totalPages":1}
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
         * totalCount : 6
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
         * children : [{"children":[],"content":"111112233","countChildren":0,"createTime":"2018-05-30 18:23:49","creatorName":"13017714697","id":42,"photoUrl":"ParentAppPhotos/001.bmp","status":1}]
         * content : 111111
         * countChildren : 1
         * createTime : 2018-05-30 18:23:32
         * creatorName : 13017714697
         * id : 41
         * photoUrl : ParentAppPhotos/001.bmp
         * status : 2
         */

        private String content;
        private int countChildren;
        private String createTime;
        private String creatorName;
        private int id;
        private String photoUrl;
        private int status;
        private List<ChildrenBean> children;

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

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ChildrenBean {
            /**
             * children : []
             * content : 111112233
             * countChildren : 0
             * createTime : 2018-05-30 18:23:49
             * creatorName : 13017714697
             * id : 42
             * photoUrl : ParentAppPhotos/001.bmp
             * status : 1
             */

            private String content;
            private int countChildren;
            private String createTime;
            private String creatorName;
            private int id;
            private String photoUrl;
            private int status;
            private List<?> children;

            public ChildrenBean(String content, int countChildren, String createTime, String creatorName, int id, String photoUrl, int status, List<?> children) {
                this.content = content;
                this.countChildren = countChildren;
                this.createTime = createTime;
                this.creatorName = creatorName;
                this.id = id;
                this.photoUrl = photoUrl;
                this.status = status;
                this.children = children;
            }

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

            public String getPhotoUrl() {
                return photoUrl;
            }

            public void setPhotoUrl(String photoUrl) {
                this.photoUrl = photoUrl;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public List<?> getChildren() {
                return children;
            }

            public void setChildren(List<?> children) {
                this.children = children;
            }
        }
    }
}
