package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 新闻资讯实体
 */
public class NewsAndInfoBean {
    /**
     * code : 0
     * data : [{"createTime":"2018-05-24 11:44:32","creatorName":"高飞","digest":"本报北京4月24日电  （记者林丽鹂）记者从国家市场监管总局获悉：国家市场监管总局、人力资源和社会保障部近日发布关于规范营利性民办技工院校和营利性民办职业技能培训机构名称登记管理有关工作的通知，要求民办技工院校和民办培训机构名称不得冠以\u201c中国\u201d\u201c中华\u201d\u201c全国\u201d\u201c国家\u201d\u201c中央\u201d\u201c国际\u201d\u201c世界\u201d\u201c全球\u201d等字样。在名称中使用上述字样的，应当是行业的限定语并符合国家有关规定。","headline":"民办培训机构不得冠以\u201c中华\u201d字样","id":58,"newsUrl":"student_app_cloud/listDetail.html","photoUrl":"ParentAppPhotos/20180524114437.jpg","readed":17}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":1,"sortFields":[],"totalCount":41,"totalPages":41}
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
         * pageSize : 1
         * sortFields : []
         * totalCount : 41
         * totalPages : 41
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
         * createTime : 2018-05-24 11:44:32
         * creatorName : 高飞
         * digest : 本报北京4月24日电  （记者林丽鹂）记者从国家市场监管总局获悉：国家市场监管总局、人力资源和社会保障部近日发布关于规范营利性民办技工院校和营利性民办职业技能培训机构名称登记管理有关工作的通知，要求民办技工院校和民办培训机构名称不得冠以“中国”“中华”“全国”“国家”“中央”“国际”“世界”“全球”等字样。在名称中使用上述字样的，应当是行业的限定语并符合国家有关规定。
         * headline : 民办培训机构不得冠以“中华”字样
         * id : 58
         * newsUrl : student_app_cloud/listDetail.html
         * photoUrl : ParentAppPhotos/20180524114437.jpg
         * readed : 17
         */

        private String createTime;
        private String creatorName;
        private String digest;
        private String headline;
        private int id;
        private String newsUrl;
        private String photoUrl;
        private int readed;

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

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
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

        public String getNewsUrl() {
            return newsUrl;
        }

        public void setNewsUrl(String newsUrl) {
            this.newsUrl = newsUrl;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public int getReaded() {
            return readed;
        }

        public void setReaded(int readed) {
            this.readed = readed;
        }
    }
}
