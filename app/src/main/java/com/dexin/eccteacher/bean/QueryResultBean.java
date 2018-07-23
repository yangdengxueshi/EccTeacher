package com.dexin.eccteacher.bean;

/**
 * 查询结果 实体
 */
public class QueryResultBean {
    /**
     * code : 0
     * data : {"normalNum":0,"normalPercentage":"0","notSignInNum":23,"notSignInPercentage":"1","sum":23,"timeOutNum":0,"timeOutPercentage":"0"}
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
         * normalNum : 0
         * normalPercentage : 0
         * notSignInNum : 23
         * notSignInPercentage : 1
         * sum : 23
         * timeOutNum : 0
         * timeOutPercentage : 0
         */

        private int normalNum;
        private String normalPercentage;
        private int notSignInNum;
        private String notSignInPercentage;
        private int sum;
        private int timeOutNum;
        private String timeOutPercentage;

        public int getNormalNum() {
            return normalNum;
        }

        public void setNormalNum(int normalNum) {
            this.normalNum = normalNum;
        }

        public String getNormalPercentage() {
            return normalPercentage;
        }

        public void setNormalPercentage(String normalPercentage) {
            this.normalPercentage = normalPercentage;
        }

        public int getNotSignInNum() {
            return notSignInNum;
        }

        public void setNotSignInNum(int notSignInNum) {
            this.notSignInNum = notSignInNum;
        }

        public String getNotSignInPercentage() {
            return notSignInPercentage;
        }

        public void setNotSignInPercentage(String notSignInPercentage) {
            this.notSignInPercentage = notSignInPercentage;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public int getTimeOutNum() {
            return timeOutNum;
        }

        public void setTimeOutNum(int timeOutNum) {
            this.timeOutNum = timeOutNum;
        }

        public String getTimeOutPercentage() {
            return timeOutPercentage;
        }

        public void setTimeOutPercentage(String timeOutPercentage) {
            this.timeOutPercentage = timeOutPercentage;
        }
    }
}
