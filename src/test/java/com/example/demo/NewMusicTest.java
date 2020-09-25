package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.model.musicFrom163.Music163;
import com.dingdo.extendService.model.musicFromQQ.MusicQQ;
import com.dingdo.extendService.model.musicFromQQ.SingerList;
import com.dingdo.extendService.model.musicFromQQ.SongList;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/22 16:53
 * @since JDK 1.8
 */
public class NewMusicTest {

    @Test
    public void test(){
        // HtmlUnit 模拟浏览器
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间

        RestTemplate restTemplate = new RestTemplate();
        String keyword = "好汉歌";
        String url = UrlEnum.QQ_MUSIC_SEARCH
                + keyword + "&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";

        MusicQQ musicQQ = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            musicQQ = JSONObject.parseObject(response.getBody(), MusicQQ.class);
        } catch (Exception e) {
            System.out.println("api请求中断");
            e.printStackTrace();
        }

        SongList firstSong = musicQQ.getData().getSong().getList().get(0);
        List<SingerList> singerList = firstSong.getSinger();
        StringBuilder author = new StringBuilder();

        String mid = firstSong.getMid();
        try {
            // HtmlUnit 模拟浏览器
            String songUrl = UrlEnum.QQ_MUSIC_SONG + mid + ".html";
            HtmlPage page = webClient.getPage(songUrl);
            webClient.waitForBackgroundJavaScript(0 * 100);     // 等待js后台执行3秒

            String pageAsXml = page.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, songUrl);
            Element result = doc.selectFirst("img.data__photo");
            String resultUrl = "http://" + result.attr("src");

            System.out.println(mid);
            System.out.println(resultUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        System.runFinalization();
        stringStringHashMap.put("asd", "123");
        author.append(singerList.get(0).getName());
        for (int i = 1; i < singerList.size(); i++) {
            author.append("," + singerList.get(i).getName());
        }
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = UrlEnum.MUSIC_163_SEARCH + "好汉歌";
        Music163 music = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println(response.getBody());
            music = JSONObject.parseObject(response.getBody(), Music163.class);
        } catch (Exception e) {
            System.out.println("api请求中断");
            e.printStackTrace();
        }
        System.out.println(music);
    }

}
