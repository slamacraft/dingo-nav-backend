package com.dingdo.Component;

import com.dingdo.extendService.knowledgeService.impl.SearchServiceImpl;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WebClientComponent {

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SearchServiceImpl.class);

    private WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @PostConstruct
    private void initWebClient(){
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.WARN);
        Logger.getLogger("org.apache.http.client").setLevel(Level.WARN);

        // HtmlUnit 模拟浏览器
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间
    }

    public WebClient getWebClient(){
        return this.webClient;
    }
}
