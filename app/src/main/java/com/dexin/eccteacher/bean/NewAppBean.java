package com.dexin.eccteacher.bean;

/**
 * 新版本App实体
 */
public class NewAppBean {
    /**
     * code : 0
     * data : {"length":15624192,"md5Code":"cca27708ceccbabcd0cfdc746dff516","name":"智慧云-教师端-杨灯","packageName":"com.dexin.eccteacher","time":"2018-05-24 15:35:47","url":"app/20180524153435.apk","versionCode":1,"versionName":"1.0"}
     * message : ok
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * length : 15624192
         * md5Code : cca27708ceccbabcd0cfdc746dff516
         * name : 智慧云-教师端-杨灯
         * packageName : com.dexin.eccteacher
         * time : 2018-05-24 15:35:47
         * url : app/20180524153435.apk
         * versionCode : 1
         * versionName : 1.0
         */

        private int length;
        private String md5Code;
        private String name;
        private String packageName;
        private String time;
        private String url;
        private int versionCode;
        private String versionName;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getMd5Code() {
            return md5Code;
        }

        public void setMd5Code(String md5Code) {
            this.md5Code = md5Code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }
}
