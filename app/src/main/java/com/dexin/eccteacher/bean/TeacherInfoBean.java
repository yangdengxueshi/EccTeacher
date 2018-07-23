package com.dexin.eccteacher.bean;

/**
 *
 */
public class TeacherInfoBean {
    /**
     * code : 0
     * data : {"dataType":1,"id":1210,"name":"dx杨灯","number":"511326598502513252","photoPath":"teacherPhotos\\initImage.bmp","sex":1,"telephone":"17078020229"}
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
         * dataType : 1
         * id : 1210
         * name : dx杨灯
         * number : 511326598502513252
         * photoPath : teacherPhotos\initImage.bmp
         * sex : 1
         * telephone : 17078020229
         */

        private int dataType;
        private int id;
        private String name;
        private String number;
        private String photoPath;
        private int sex;
        private String telephone;

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

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPhotoPath() {
            return photoPath;
        }

        public void setPhotoPath(String photoPath) {
            this.photoPath = photoPath;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }
    }
}
