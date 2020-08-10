package com.dingdo.extendService.knowledgeService.impl;

import com.dingdo.Component.WebClientComponent;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.knowledgeService.SearchFromWikiService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFromWikiServiceImpl implements SearchFromWikiService {

    @Autowired
    private WebClientComponent webClientComponent;
    @Autowired
    private RestTemplate restTemplate;

    public String getReplyFromWiki(String words) {
        String resultMsg = null;
        List<String> searchResultList = new ArrayList<>();

        try {   //对提问语句进行GBK编码
            String wordsGBK = java.net.URLEncoder.encode(words, "GBK");
            String url = UrlEnum.GBF_WIKI + "?search=" + wordsGBK;

            WebClient webClient = webClientComponent.getWebClient();
            HtmlPage page = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(1 * 1000);     // 等待js后台执行3秒

            String pageAsXml = page.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, url);
            Element result = doc.selectFirst("ul.mw-search-results");
            Elements resultList = result.select("li");

            for(Element item: resultList){
                Element itemInfo = item.selectFirst("a");
                String itemURL = itemInfo.attr("href");
                searchResultList.add(itemURL);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMsg;
    }
}
