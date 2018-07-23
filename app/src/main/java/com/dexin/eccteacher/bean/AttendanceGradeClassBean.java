package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 考勤 年级-班级 实体
 */
public class AttendanceGradeClassBean {
    /**
     * code : 0
     * data : [{"gradeId":134,"id":58,"name":"高中一年级一班"},{"gradeId":134,"id":59,"name":"高中一年级二班"},{"gradeId":134,"id":60,"name":"高中一年级三班"},{"gradeId":134,"id":61,"name":"高中一年级四班"},{"gradeId":134,"id":62,"name":"高中一年级五班"},{"gradeId":134,"id":63,"name":"高中一年级六班"},{"gradeId":134,"id":64,"name":"高中一年级七班"},{"gradeId":134,"id":65,"name":"高中一年级八班"},{"gradeId":134,"id":66,"name":"高中一年级九班"},{"gradeId":134,"id":68,"name":"高中一年级十班"},{"gradeId":134,"id":69,"name":"高中一年级十一班"},{"gradeId":134,"id":70,"name":"高中一年级十二班"},{"gradeId":134,"id":71,"name":"高中一年级十三班"},{"gradeId":134,"id":72,"name":"高中一年级十四班"},{"gradeId":134,"id":73,"name":"高中一年级十五班"},{"gradeId":134,"id":74,"name":"高中一年级十六班"},{"gradeId":134,"id":75,"name":"高中一年级十七班"},{"gradeId":134,"id":76,"name":"高中一年级十八班"},{"gradeId":134,"id":77,"name":"高中一年级十九班"},{"gradeId":134,"id":78,"name":"高中一年级二十班"},{"gradeId":134,"id":79,"name":"高中一年级二十一班"},{"gradeId":134,"id":80,"name":"高中一年级二十二班"},{"gradeId":134,"id":89,"name":"高中一年级二十三班"},{"gradeId":134,"id":413,"name":"高中一年级测试1"},{"gradeId":134,"id":414,"name":"高中一年级测试2"},{"gradeId":169,"id":562,"name":"初中一年级一班"},{"gradeId":169,"id":563,"name":"初中一年级二班"},{"gradeId":169,"id":564,"name":"初中一年级三班"},{"gradeId":169,"id":565,"name":"初中一年级四班"},{"gradeId":169,"id":566,"name":"初中一年级五班"},{"gradeId":169,"id":567,"name":"初中一年级六班"},{"gradeId":169,"id":568,"name":"初中一年级七班"},{"gradeId":169,"id":569,"name":"初中一年级八班"},{"gradeId":169,"id":570,"name":"初中一年级九班"},{"gradeId":169,"id":571,"name":"初中一年级十班"},{"gradeId":170,"id":573,"name":"初中二年级一班"},{"gradeId":170,"id":574,"name":"初中二年级二班"}]
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
         * gradeId : 134
         * id : 58
         * name : 高中一年级一班
         */

        private int gradeId;
        private int id;
        private String name;

        public DataBean(int gradeId, int id, String name) {
            this.gradeId = gradeId;
            this.id = id;
            this.name = name;
        }

        public int getGradeId() {
            return gradeId;
        }

        public void setGradeId(int gradeId) {
            this.gradeId = gradeId;
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

        @Override
        public String toString() {
            return name;
        }
    }
}
