package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 年级实体
 */
public class GradeBean {
    /**
     * code : 0
     * data : [{"id":163,"name":"小学一年级"},{"id":162,"name":"小学二年级"},{"id":161,"name":"小学三年级"},{"id":160,"name":"小学四年级"},{"id":159,"name":"小学五年级"},{"id":158,"name":"小学六年级"},{"id":169,"name":"初中一年级"},{"id":170,"name":"初中二年级"},{"id":171,"name":"初中三年级"},{"graduationYear":"","id":134,"name":"高中一年级"},{"id":135,"name":"高中二年级"},{"graduationYear":"NaN","id":136,"name":"高中三年级"}]
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
         * id : 163
         * name : 小学一年级
         * graduationYear :
         */

        private int id;
        private String name;
        private String graduationYear;

        public DataBean(int id, String name) {
            this.id = id;
            this.name = name;
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

        public String getGraduationYear() {
            return graduationYear;
        }

        public void setGraduationYear(String graduationYear) {
            this.graduationYear = graduationYear;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
