package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 选课 实体
 */
public class ChooseCourseBean {
    /**
     * code : 0
     * data : [{"name":"张书慧","subjectName":"","typeName":"未选"},{"name":"刘志超","subjectName":"","typeName":"未选"},{"name":"李琳婕","subjectName":"","typeName":"未选"},{"name":"李明珠","subjectName":"","typeName":"未选"},{"name":"谢孜屿","subjectName":"","typeName":"未选"},{"name":"张荀知","subjectName":"","typeName":"未选"},{"name":"伍海","subjectName":"","typeName":"未选"},{"name":"万曦雅","subjectName":"","typeName":"未选"},{"name":"万婧娴","subjectName":"","typeName":"未选"},{"name":"马涵睿","subjectName":"","typeName":"未选"},{"name":"孙加雨","subjectName":"","typeName":"未选"},{"name":"程柯喻","subjectName":"","typeName":"未选"},{"name":"帅蕴珈","subjectName":"","typeName":"未选"},{"name":"李阳熠","subjectName":"","typeName":"未选"},{"name":"张可欣","subjectName":"","typeName":"未选"},{"name":"林文静","subjectName":"","typeName":"未选"},{"name":"龙靖宇","subjectName":"","typeName":"未选"},{"name":"肖天昊","subjectName":"","typeName":"未选"},{"name":"徐紫茵","subjectName":"","typeName":"未选"},{"name":"刘嘉涛","subjectName":"","typeName":"未选"},{"name":"高努男","subjectName":"","typeName":"未选"},{"name":"许家辉","subjectName":"","typeName":"未选"},{"name":"王灵芮","subjectName":"","typeName":"未选"}]
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
         * name : 张书慧
         * subjectName :
         * typeName : 未选
         */

        private String name;
        private String subjectName;
        private String typeName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}
