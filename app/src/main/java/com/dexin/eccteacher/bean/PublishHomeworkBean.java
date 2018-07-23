package com.dexin.eccteacher.bean;

import java.util.List;

/**
 * 发布作业 实体
 */
public class PublishHomeworkBean {
    /**
     * data : [{"id":1,"name":"语文"},{"id":2,"name":"数学"},{"id":3,"name":"英语"},{"id":4,"name":"物理"},{"id":5,"name":"化学"},{"id":6,"name":"生物"},{"id":7,"name":"政治"},{"id":8,"name":"历史"},{"id":9,"name":"地理"},{"id":13,"name":"Fun Fun Show"},{"id":14,"name":"附小金主播社团"},{"id":15,"name":"画故事"},{"id":16,"name":"趣味艺术坊"},{"id":17,"name":"雏鹰筝乐坊"},{"id":18,"name":"雏鹰竖笛社"},{"id":19,"name":"篮球小将"},{"id":20,"name":"动感啦啦操"},{"id":23,"name":"篮球部落"},{"id":24,"name":"足球"},{"id":25,"name":"篮球女将"},{"id":26,"name":"男子篮球"},{"id":52,"name":"舞蹈"},{"id":89,"name":"体育"},{"id":90,"name":"音乐"},{"id":91,"name":"美术"},{"id":92,"name":"班会"},{"id":101,"name":"popping"},{"id":102,"name":"民族舞"},{"id":120,"name":"计算机科学"},{"id":121,"name":"ces"},{"id":238,"name":"pp"},{"id":239,"name":"ss"},{"id":516,"name":"12"},{"id":517,"name":"12"},{"id":559,"name":"Java"},{"id":560,"name":"3"},{"id":561,"name":"2"},{"id":562,"name":"123"},{"id":563,"name":"动画制作"},{"id":564,"name":"摄影"},{"id":565,"name":"演讲"},{"id":566,"name":"实时之声"},{"id":567,"name":"艺术钢琴"},{"id":568,"name":"体育篮球"},{"id":569,"name":"游戏竞技"},{"id":570,"name":"1221"},{"id":571,"name":"体育足球"},{"id":573,"name":"JAVA编程"},{"id":575,"name":"fb f"},{"id":576,"name":"1"},{"id":577,"name":"i"},{"id":578,"name":"6"}]
     * message : ok
     */

    private String message;
    private List<DataBean> data;

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
         * name : 语文
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

        @Override
        public String toString() {
            return name;
        }
    }
}
