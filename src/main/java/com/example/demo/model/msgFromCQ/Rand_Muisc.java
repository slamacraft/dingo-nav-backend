package com.example.demo.model.msgFromCQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rand_Muisc {
    /**
     *  返回的状态码
     */
    private String code;
    /**
     * 返回歌曲数据
     */
    private Data data;
    /**
     * 返回错误提示信息！
     */
    private String  msg;

    class Data{
        /**
         *歌曲名
         */
        private String name;
        /**
         * 歌曲链接
         */
        private String url;
        /**
         * 歌曲封面
         */
        private String picurl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
