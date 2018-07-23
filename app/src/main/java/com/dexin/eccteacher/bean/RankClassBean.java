package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 评分类别实体
 */
public class RankClassBean {
    /**
     * code : 0
     * data : [{"id":1,"name":"卫生","totalScore":10,"totalStar":5},{"id":2,"name":"礼仪","totalScore":10,"totalStar":5},{"id":3,"name":"眼保健操","totalScore":10,"totalStar":5},{"id":4,"name":"课间操","totalScore":10,"totalStar":5}]
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
         * id : 1
         * name : 卫生
         * totalScore : 10
         * totalStar : 5
         */

        private int id;
        private String name;
        private float totalScore;
        private int totalStar;

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

        public float getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
        }

        public int getTotalStar() {
            return totalStar;
        }

        public void setTotalStar(int totalStar) {
            this.totalStar = totalStar;
        }
    }
}
