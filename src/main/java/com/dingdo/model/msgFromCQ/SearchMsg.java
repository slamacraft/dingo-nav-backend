package com.dingdo.model.msgFromCQ;

/**
 * 搜索内容的实体类
 */
@Deprecated
public class SearchMsg {
    /**
     * 搜索的关键字
     */
    private String keyword;
    /**
     * 查询到的百科信息
     */
    private String message;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
