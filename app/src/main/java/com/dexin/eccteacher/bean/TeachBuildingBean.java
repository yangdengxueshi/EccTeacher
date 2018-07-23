package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 教学楼 实体
 */
public class TeachBuildingBean {
    /**
     * code : 0
     * data : [{"fullName":"明智楼","id":1},{"fullName":"德行楼","id":2},{"fullName":"笃行楼","id":3},{"fullName":"实验楼","id":37},{"fullName":"操场","id":48}]
     * message : ok
     */

    private int code;
    private String message;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * fullName : 明智楼
         * id : 1
         */

        private String fullName;
        private int id;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }
}
