package com.example.mytodolist.scrollview;

import java.util.List;

public class GetConfigReq {
    /**
     * ret : 0
     * msg : succes,
     * datas : [{"ID":"  0","categoryName":"社团","state":"1"},{"ID":"1","categoryName":"原创","state":"1"},{"ID":"2","categoryName":"现货","state":"1"}]
     */

    private int ret;
    private String msg;
    private List<DatasBean> datas;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * categoryName : days
         * state : 1
         */

        private String categoryName;
        private String state;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}

