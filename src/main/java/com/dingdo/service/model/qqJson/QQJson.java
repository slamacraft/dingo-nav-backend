package com.dingdo.service.model.qqJson;


import com.dingdo.util.SpringContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * QQ卡片对象
 * <p>
 * <pre class="code">
 * QQJson.QQAll qqAll = new QQJson.QQAll();
 * qqAll.setPreview("http://gchat.qpic.cn/gchatpic_new/3584906133/956021029-2885039703-7B5004A5ED0FCF042BF5AF737EA1762B/0?term=2");
 * qqAll.setSummary("测试");
 * qqAll.setJumpUrl("https://www.baidu.com/");
 * QQJson.QQButton button = new QQJson.QQButton("你好", "你好");
 * List<QQJson.QQButton> buttons = new ArrayList<>();
 * buttons.add(button);
 * qqAll.setButtons(buttons);
 * LightApp lightApp = new LightApp(QQJson.create(qqAll).toJson());
 * event.getGroup().sendMessage(lightApp);
 *</pre>
 * </p>
 */
public class QQJson {

    private static final ObjectMapper mapper = SpringContextUtil.getBean(ObjectMapper.class);

    private String app = "com.tencent.miniapp";
    private String desc = "";
    private String ver = "0.0.0.1";
    /**
     * 在聊天栏外面看到的提示信息，例如[QQ红包]恭喜发财
     */
    private String prompt = "";
    /**
     * 视图类型，与下面meta的类型对应
     */
    private String view;
    /**
     * 数据承载对象
     */
    private QQMeta meta;

    ///////////////////////// 工厂方法 ///////////////////////
    public static QQJson create(MeatData meatData) {
        QQJson json = new QQJson();
        json.meta = new QQMeta(meatData);
        json.view = meatData.getDataName();
        return json;
    }

    /**
     * 将QQJson转换为Json字符串
     *
     * @return json
     */
    public String toJson() {
        try {
            return mapper.writeValueAsString(this).replaceFirst("metaData", this.meta.getMetaData().getDataName());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "QQJson序列化失败";
    }


    /**
     * 数据承载对象
     */
    static class QQMeta {
        private MeatData metaData;

        public QQMeta() {
        }

        public QQMeta(MeatData metaData) {
            this.metaData = metaData;
        }

        public MeatData getMetaData() {
            return metaData;
        }

        public void setMetaData(MeatData metaData) {
            this.metaData = metaData;
        }
    }


    /**
     * QQ卡片数据实体接口，所有的数据实体都需要实现这个接口
     */
    interface MeatData {
        String getDataName();
    }


    /**
     * QQ卡片的一种数据实体。表示带按钮的小程序
     */
    public static class QQAll implements MeatData {

        private String preview = "";
        private String title = "";
        private String jumpUrl = "";
        private String summary = "";
        private List<QQButton> buttons;

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<QQButton> getButtons() {
            return buttons;
        }

        public void setButtons(List<QQButton> buttons) {
            this.buttons = buttons;
        }

        @Override
        public String getDataName() {
            return "all";
        }
    }

    /**
     * QQ卡片上的按钮
     * <p>
     * 在{@link QQAll}类型中：无论有多少个按钮，始终只会显示第一个
     * </p>
     */
    public static class QQButton {
        private String name;
        private String action;

        public QQButton() {
        }

        public QQButton(String name, String action) {
            this.name = name;
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public String getAction() {
            return action;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }


    ///////////////////// getter & setter //////////////////
    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public QQMeta getMeta() {
        return meta;
    }

    public void setMeta(QQMeta meta) {
        this.meta = meta;
    }
}

