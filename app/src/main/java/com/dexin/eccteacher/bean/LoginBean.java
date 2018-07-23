package com.dexin.eccteacher.bean;

/**
 * 登录实体
 */
public class LoginBean {
    /**
     * code : 0
     * data : {"id":6734,"rongToken":"snVOpU8Rm/r6XqUOYCj17srPKv6cmN6qxiy5gErKNhHgD8qY6l0yVta2iMYReGZxeM67UE5fbde/1qNdCGOeSQ==","rongUserId":"T_6734","schoolId":7,"sex":1,"token":"15287743884596734","userId":"511326598502513252","userName":"杨灯888"}
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
         * id : 6734
         * rongToken : snVOpU8Rm/r6XqUOYCj17srPKv6cmN6qxiy5gErKNhHgD8qY6l0yVta2iMYReGZxeM67UE5fbde/1qNdCGOeSQ==
         * rongUserId : T_6734
         * schoolId : 7
         * sex : 1
         * token : 15287743884596734
         * userId : 511326598502513252
         * userName : 杨灯888
         */

        private int id;
        private String rongToken;
        private String rongUserId;
        private int schoolId;
        private int sex;
        private String token;
        private String userId;
        private String userName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRongToken() {
            return rongToken;
        }

        public void setRongToken(String rongToken) {
            this.rongToken = rongToken;
        }

        public String getRongUserId() {
            return rongUserId;
        }

        public void setRongUserId(String rongUserId) {
            this.rongUserId = rongUserId;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
