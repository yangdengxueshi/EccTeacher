package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 个人分数 实体
 */
public class PersonScoreBean {
    /**
     * code : 0
     * data : [{"rankingAdminClass":7,"rankingGrade":33,"score":122,"subjectName":"语文"},{"rankingAdminClass":1,"rankingGrade":2,"score":143,"subjectName":"数学"},{"rankingAdminClass":26,"rankingGrade":409,"score":135,"subjectName":"英语"},{"rankingAdminClass":14,"rankingGrade":149,"score":79,"subjectName":"物理"},{"rankingAdminClass":22,"rankingGrade":203,"score":67,"subjectName":"化学"},{"rankingAdminClass":34,"rankingGrade":624,"score":28,"subjectName":"生物"},{"rankingAdminClass":16,"rankingGrade":118,"score":42,"subjectName":"政治"},{"rankingAdminClass":23,"rankingGrade":133,"score":46,"subjectName":"历史"},{"rankingAdminClass":18,"rankingGrade":162,"score":29,"subjectName":"地理"}]
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
         * rankingAdminClass : 7
         * rankingGrade : 33
         * score : 122
         * subjectName : 语文
         */

        private int rankingAdminClass;
        private int rankingGrade;
        private int score;
        private String subjectName;

        public int getRankingAdminClass() {
            return rankingAdminClass;
        }

        public void setRankingAdminClass(int rankingAdminClass) {
            this.rankingAdminClass = rankingAdminClass;
        }

        public int getRankingGrade() {
            return rankingGrade;
        }

        public void setRankingGrade(int rankingGrade) {
            this.rankingGrade = rankingGrade;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }
    }
}
