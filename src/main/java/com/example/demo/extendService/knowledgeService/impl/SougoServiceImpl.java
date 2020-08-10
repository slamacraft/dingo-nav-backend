package com.example.demo.extendService.knowledgeService.impl;

import com.example.demo.Component.WebClientComponent;
import com.example.demo.extendService.knowledgeService.SougoService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.gargoylesoftware.htmlunit.WebClient;
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
public class SougoServiceImpl implements SougoService {

    @Autowired
    private WebClientComponent webClientComponent;

    @Override
    public ReplyMsg sendReply(ReceiveMsg receiveMsg) {
        ReplyMsg replyMsg = new ReplyMsg();

        String rawMsg = receiveMsg.getRaw_message();
        String resultMsg = getReplyFromSougo(rawMsg);
        if (resultMsg != null) {
            replyMsg.setReply(resultMsg);
            return replyMsg;
        }
        replyMsg.setReply("好像什么也没找到");
        return replyMsg;
    }

    @Override
    public String getReply(ReceiveMsg receiveMsg) {
        return null;
    }

    @Override
    public String getReplyFromSougo(String words) {
        String resultMsg = null;

        try {   //对提问语句进行GBK编码
            String wordsGBK = java.net.URLEncoder.encode(words, "UTF-8");
            String url = "https://www.sogou.com/sogou?query=" + wordsGBK + "&ie=utf8&insite=wenwen.sogou.com&pid=sogou-wsse-a9e18cb5dd9d3ab4&rcer=";

            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

            System.out.println("Loading page now-----------------------------------------------:\n " + url);

            // HtmlUnit 模拟浏览器
            WebClient webClient = webClientComponent.getWebClient();
            HtmlPage page = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(0 * 100);     // 等待js后台执行3秒

            String pageAsXml = page.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, url);
            Element result = doc.getElementById("sogou_snapshot_0");
            String resultUrl = result.attr("href");

            //爬取结果
            Document reusltDoc = Jsoup.connect(resultUrl).get();
            Element resultElement = reusltDoc.selectFirst("pre.replay-info-txt");

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
