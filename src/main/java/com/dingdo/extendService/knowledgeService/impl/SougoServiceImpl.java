package com.dingdo.extendService.knowledgeService.impl;

import com.dingdo.Component.WebClientComponent;
import com.dingdo.extendService.knowledgeService.SougoService;

import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.model.msgFromMirai.ReqMsg;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SougoServiceImpl implements SougoService {

    @Autowired
    private WebClientComponent webClientComponent;

    @Override
    public String sendReply(ReqMsg reqMsg) {
        String rawMsg = reqMsg.getRawMessage();
        String resultMsg = getReplyFromSougo(rawMsg);
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
    public String getReplyFromSougo(String words) {
        String resultMsg = null;

        try {   //对提问语句进行GBK编码
            String wordsGBK = java.net.URLEncoder.encode(words, "UTF-8");
            String url = "https://www.sogou.com/sogou?query=" + wordsGBK
                    + "&ie=utf8&insite=wenwen.sogou.com&pid=sogou-wsse-a9e18cb5dd9d3ab4&rcer=";

            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

            System.out.println("Loading page now-----------------------:\n " + url);

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

        // 去除连续空行后返回
        return resultMsg.replaceAll("(\n)(\n)+", "\n");
    }
}
