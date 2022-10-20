package com.dingdo.extendService.knowledgeService.impl;

import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.knowledgeService.ZhidaoService;
import com.dingdo.msgHandler.model.ReqMsg;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ZhidaoServiceImpl implements ZhidaoService {

    @Autowired
    private WebClient webClient;

    @Override
    public String sendReply(ReqMsg reqMsg) {
        String String = new String();

        String rawMsg = reqMsg.getRawMessage();
        String resultMsg = getReplyFromBaidu(rawMsg);
        if (resultMsg != null) {
            return resultMsg;
        }
        return "好像什么也没找到";
    }

    @Override
    public String getReply(ReqMsg reqMsg) {
        return null;
    }

    @Override
    public String stdReplyFromBaidu(ReqMsg reqMsg) {
        String String = new String();

        String rawMsg = reqMsg.getRawMessage();
        String words = rawMsg.split("百度一下")[1].trim();
        String resultMsg = getReplyFromBaidu(words);
        if (resultMsg != null) {
            return resultMsg;
        }
        return "好像什么也没找到";
    }

    @Override
    public String getReplyFromBaidu(String words) {
        String resultMsg = null;

        try {   //对提问语句进行GBK编码
            String wordsGBK = java.net.URLEncoder.encode(words, "GBK");
            String url = UrlEnum.BAIDU_ZHIDAO + wordsGBK;

            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

            System.out.println("Loading page now-----------------------------------------------:\n " + url);

            // HtmlUnit 模拟浏览器
            Page page = webClient.getPage(url);
            HtmlPage tmpPage = HTMLParser.parseHtml(page.getWebResponse(), webClient.getCurrentWindow());
            // use tmpPage here
            webClient.waitForBackgroundJavaScript(3 * 1000);     // 等待js后台执行3秒

            String pageAsXml = tmpPage.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, url);
            Element result = doc.getElementById("wgt-list");
            String resultUrl = result.selectFirst("a.ti").attr("href");

            //爬取结果
            Document reusltDoc = Jsoup.connect(resultUrl).get();
            ;
            Element resultElement = reusltDoc.selectFirst("[accuse=aContent]");

            resultMsg = resultElement.text();
            if (resultMsg.contains("展开全部")) {
                String[] resultSplit = resultMsg.split("展开全部");
                if (resultSplit.length > 0) {
                    resultMsg = resultSplit[0].trim();
                } else {
                    resultMsg = "我也不知道";
                }
            }
//             获取当页所有的查询结果
//            Elements resultMsg = result.select("dl.dl");
//            Iterator<Element> iterator = resultMsg.iterator();
//            while (iterator.hasNext()){
//                Element element = resultMsg.select("a.ti").first();
//            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(resultMsg);

        return resultMsg;
    }
}
