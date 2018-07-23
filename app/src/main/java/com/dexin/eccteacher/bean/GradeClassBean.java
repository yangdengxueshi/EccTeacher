package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 年级-班级 实体
 */
public class GradeClassBean {
    /**
     * code : 0
     * data : [{"dataType":1,"id":573,"name":"初中二年级一班"},{"dataType":1,"id":574,"name":"初中二年级二班"},{"dataType":1,"id":562,"name":"初中一年级一班"},{"dataType":1,"id":563,"name":"初中一年级二班"},{"dataType":1,"id":564,"name":"初中一年级三班"},{"dataType":1,"id":565,"name":"初中一年级四班"},{"dataType":1,"id":566,"name":"初中一年级五班"},{"dataType":1,"id":567,"name":"初中一年级六班"},{"dataType":1,"id":568,"name":"初中一年级七班"},{"dataType":1,"id":569,"name":"初中一年级八班"},{"dataType":1,"id":570,"name":"初中一年级九班"},{"dataType":1,"id":571,"name":"初中一年级十班"},{"dataType":1,"id":58,"name":"高中一年级一班"},{"dataType":1,"id":59,"name":"高中一年级二班"},{"dataType":1,"id":60,"name":"高中一年级三班"},{"dataType":1,"id":61,"name":"高中一年级四班"},{"dataType":1,"id":62,"name":"高中一年级五班"},{"dataType":1,"id":63,"name":"高中一年级六班"},{"dataType":1,"id":64,"name":"高中一年级七班"},{"dataType":1,"id":65,"name":"高中一年级八班"},{"dataType":1,"id":66,"name":"高中一年级九班"},{"dataType":1,"id":68,"name":"高中一年级十班"},{"dataType":1,"id":69,"name":"高中一年级十一班"},{"dataType":1,"id":70,"name":"高中一年级十二班"},{"dataType":1,"id":71,"name":"高中一年级十三班"},{"dataType":1,"id":72,"name":"高中一年级十四班"},{"dataType":1,"id":73,"name":"高中一年级十五班"},{"dataType":1,"id":74,"name":"高中一年级十六班"},{"dataType":1,"id":75,"name":"高中一年级十七班"},{"dataType":1,"id":76,"name":"高中一年级十八班"},{"dataType":1,"id":77,"name":"高中一年级十九班"},{"dataType":1,"id":78,"name":"高中一年级二十班"},{"dataType":1,"id":79,"name":"高中一年级二十一班"},{"dataType":1,"id":80,"name":"高中一年级二十二班"},{"dataType":1,"id":89,"name":"高中一年级二十三班"},{"dataType":1,"id":413,"name":"高中一年级测试1"},{"dataType":1,"id":414,"name":"高中一年级测试2"}]
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
         * dataType : 1
         * id : 573
         * name : 初中二年级一班
         */

        private int dataType;
        private int id;
        private String name;

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
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
    }
}
