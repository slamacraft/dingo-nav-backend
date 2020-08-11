package com.dingdo.model.MsgFromSiZhi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/11 9:49
 * @since JDK 1.8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMsg {
    /**
     * success表示请求正确，error表示请求错误
     */
    private String message;
    /**
     * 返回的数据
     */
    private ChatMsg.Data data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Data{
        /**
         * 返回的数据类型，5000表示正确返回文本类型的答复
         */
        private Integer type;
        /**
         * 返回的信息体
         */
        private ChatMsg.Data.Info info;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Info{
            private List<String> heuristic;
            /**
             * 返回的答案
             */
            private String text;

            public List<String> getHeuristic() {
                return heuristic;
            }

            public void setHeuristic(List<String> heuristic) {
                this.heuristic = heuristic;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public ChatMsg.Data.Info getInfo() {
            return info;
        }

        public void setInfo(ChatMsg.Data.Info info) {
            this.info = info;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatMsg.Data getData() {
        return data;
    }

    public void setData(ChatMsg.Data data) {
        this.data = data;
    }
}
