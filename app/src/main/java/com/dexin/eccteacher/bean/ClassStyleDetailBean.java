package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 班级风采详情 实体
 */
public class ClassStyleDetailBean {
    /**
     * code : 0
     * data : [{"content":"集体卖萌_山外青山楼外楼，西湖歌舞几时休。暖风熏得游人醉，直把杭州作汴州。\u201d  5．陆游《示儿》：\u201c死去原知万事空，但悲不见九州同。王师北定中原日，家祭无忘告乃翁。\u201d  6．陆游《秋夜将晓出篱门迎凉有感》：\u201c三万里河东人海，五千仍岳上摩天。遗民泪尽胡尘里，南望王师又一年。\u201d   7．\u201e等等。同学们，让我们积极行动起来，尊敬老师，孝敬父母，对待同学要像春天般温暖，对待学习要像夏天般热情，对待自身的缺点要像秋风扫落叶一样毫不留情，把雷锋天祥《过零丁洋》：\u201c辛苦遭逢起一经","id":15,"photoUrl":"ParentAppPhotos/gangq01.jpg"},{"content":"集体卖萌_山外青山楼外楼，西湖歌舞几时休。暖风熏得游人醉，直把杭州作汴州。\u201d  5．陆游《示儿》：\u201c死去原知万事空，但悲不见九州同。王师北定中原日，家祭无忘告乃翁。\u201d  6．陆游《秋夜将晓出篱门迎凉有感》：\u201c三万里河东人海，五千仍岳上摩天。遗民泪尽胡尘里，南望王师又一年。\u201d   7．\u201e等等。同学们，让我们积极行动起来，尊敬老师，孝敬父母，对待同学要像春天般温暖，对待学习要像夏天般热情，对待自身的缺点要像秋风扫落叶一样毫不留情，把雷锋天祥《过零丁洋》：\u201c辛苦遭逢起一经","id":19,"photoUrl":"ParentAppPhotos/v2-c64efa26b295554f886bfcf4033420f4_r.jpg"}]
     * message : ok
     * pageObject : {"firstIsZero":false,"orderByString":"","pageIndex":1,"pageSize":0,"sortFields":[],"totalCount":2,"totalPages":1}
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
         * content : 集体卖萌_山外青山楼外楼，西湖歌舞几时休。暖风熏得游人醉，直把杭州作汴州。”  5．陆游《示儿》：“死去原知万事空，但悲不见九州同。王师北定中原日，家祭无忘告乃翁。”  6．陆游《秋夜将晓出篱门迎凉有感》：“三万里河东人海，五千仍岳上摩天。遗民泪尽胡尘里，南望王师又一年。”   7．„等等。同学们，让我们积极行动起来，尊敬老师，孝敬父母，对待同学要像春天般温暖，对待学习要像夏天般热情，对待自身的缺点要像秋风扫落叶一样毫不留情，把雷锋天祥《过零丁洋》：“辛苦遭逢起一经
         * id : 15
         * photoUrl : ParentAppPhotos/gangq01.jpg
         */

        private String content;
        private int id;
        private String photoUrl;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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
    }
}
