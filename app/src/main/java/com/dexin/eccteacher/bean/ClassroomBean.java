package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 教室 实体
 */
public class ClassroomBean {
    /**
     * code : 0
     * data : [{"classroomType":{"id":1,"name":"数字教室"},"id":1,"name":"1-101(com.scm.acs.beans.ClassroomType@11335853)"},{"classroomType":{"id":1,"name":"数字教室"},"id":294,"name":"1-507(com.scm.acs.beans.ClassroomType@11335853)"}]
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
         * classroomType : {"id":1,"name":"数字教室"}
         * id : 1
         * name : 1-101(com.scm.acs.beans.ClassroomType@11335853)
         */

        private ClassroomTypeBean classroomType;
        private int id;
        private String name;

        public ClassroomTypeBean getClassroomType() {
            return classroomType;
        }

        public void setClassroomType(ClassroomTypeBean classroomType) {
            this.classroomType = classroomType;
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

        public static class ClassroomTypeBean {
            /**
             * id : 1
             * name : 数字教室
             */

            private int id;
            private String name;

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
}
