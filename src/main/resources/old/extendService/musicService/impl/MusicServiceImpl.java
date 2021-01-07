//package com.dingdo.extendService.musicService.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.dingdo.component.otherComponent.WebClientComponent;
//import com.dingdo.enums.UrlEnum;
//import com.dingdo.extendService.musicService.MusicService;
//import com.dingdo.extendService.model.msgFromCQ.SearchMsg;
//import com.dingdo.msgHandler.model.ReqMsg;
//import com.dingdo.extendService.model.musicFromQQ.MusicQQ;
//import com.dingdo.extendService.model.musicFromQQ.SongQQ;
//import com.dingdo.util.PinyinUtil;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.hankcs.hanlp.HanLP;
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 音乐服务接口的实现类
// */
//@Deprecated
//public class MusicServiceImpl implements MusicService {
//
//    // 使用log4j打印日志
//    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MusicServiceImpl.class);
//
//    @Autowired
//    private RestTemplate restTemplate;
//    @Autowired
//    private WebClientComponent webClientComponent;
//
//    @Override
//    public String sendReply(ReqMsg reqMsg) {
//        String keyword = this.getKeyword(null);
//        return this.getMusic(keyword);
//    }
//
//    @Override
//    public String getReply(ReqMsg reqMsg) {
//        return null;
//    }
//
//    /**
//     * 从自然语言中获取歌曲关键字
//     *
//     * @param reqMsg
//     * @return
//     */
//    @Override
//    public String getKeyword(ReqMsg reqMsg) {
//        // 暂时不知道怎么处理
//        String keyword = "";
//        return reqMsg.getRawMessage();
//    }
//
//    /**
//     * 随机点歌的处理方法
//     *
//     * @return
//     */
//    private String randMusic() {
//        String String = new String();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        JSONObject json = new JSONObject();
//
//        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);
//
//        String id = null;
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(UrlEnum.WANGYI_RAND_MUISC.toString(), request, String.class);
//            System.out.println("response:" + response.getHeaders().getLocation());
//            id = response.getHeaders().getLocation().toString()
//                    .split("id=")[1]
//                    .split("\\.mp3")[0]
//                    .trim();
//            System.out.println("id=" + id);
//        } catch (Exception e) {
//            System.out.println("-----------------请求随机音乐接口失败----------------------");
//            e.printStackTrace();
//        }
//
//        return "[CQ:music,type=163,id=" + id + "]";
//    }
//
//    /**
//     * 从qq音乐api地址直接搜索歌曲
//     *
//     * @return
//     */
//    private MusicEntity getMusicFromMusicQQApi(String keyword) {
//        String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&searchid=46307684634141409&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=10&w="
//                + keyword + "&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";
//
//        MusicQQ musicQQ = null;
//        try {
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            musicQQ = JSONObject.parseObject(response.getBody(), MusicQQ.class);
//            logger.info("从api查询到歌曲：" + musicQQ.getData().getSong().getList().get(0).getName());
//        } catch (Exception e) {
//            System.out.println("api请求中断");
//            e.printStackTrace();
//        }
//
//        musicEntity.setMusicMid(musicQQ.getData().getSong().getList().get(0).getId());
//        musicEntity.setMusicType("qq");
//        musicEntity.setMusicName(musicQQ.getData().getSong().getList().get(0).getName());
//        List<SongQQ.SingerList> singerList = musicQQ.getData().getSong().getList().get(0).getSinger();
//        StringBuffer author = new StringBuffer();
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        System.runFinalization();
//        stringStringHashMap.put("asd", "123");
//        author.append(singerList.get(0).getName());
//        for (int i = 1; i < singerList.size(); i++) {
//            author.append("," + singerList.get(i).getName());
//        }
//        musicEntity.setMusicAuthor(author.toString());
//
//        try {
//            if (musicDao.selectList(new QueryWrapper<MusicEntity>()
//                    .eq("music_mid", musicEntity.getMusicMid())
//                    .eq("music_type", "qq")).size() == 0) {
//                musicDao.insert(musicEntity);
//            }
//        } finally {
//            return musicEntity;
//        }
//    }
//
//    /**
//     * 使用爬虫爬取qq音乐（效率较低，不推荐使用）
//     *
//     * @param keyword
//     * @return
//     */
//    private MusicEntity getMusicFromEnternet(String keyword) {
//        SearchMsg searchMsg = new SearchMsg();
//        searchMsg.setKeyword(keyword);
//
//        MusicEntity music = null;
//
//        try {
//            String musicName = URLEncoder.encode(keyword, "UTF-8");
//            String url = UrlEnum.QQ_MUSIC.getUrl() + musicName;
//
//            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
//            Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
//
//            System.out.println("Loading page now---------------------------------: " + url);
//
//            // HtmlUnit 模拟浏览器
//            WebClient webClient = webClientComponent.getWebClient();
//            HtmlPage page = webClient.getPage(url);
//            webClient.waitForBackgroundJavaScript(7 * 1000);     // 等待js后台执行3秒
//
//            String pageAsXml = page.asXml();
//
//            // Jsoup解析处理
//            Document doc = Jsoup.parse(pageAsXml, url);
//            Elements songList = doc.select("div.songlist__item");
//
//            //获取歌曲列表后进行遍历
//            Iterator<Element> iterable = songList.iterator();
//            int minEditCount = 2147483647;
//            String songURL = "";
//            String name = "";
//            String author = "";
//            while (iterable.hasNext()) {
//                Element element = iterable.next();
//                Element songElement = element.selectFirst("a.js_song");
//                if (null == songElement) {
//                    continue;
//                }
//                String songName = songElement.attr("title");
//                songName = songName.replaceAll("[^\u4E00-\u9FA5]", "");
//
//                String reg = "[\u4e00-\u9fa5]";
//                Pattern pattern = Pattern.compile(reg);
//
//                String s1CN = keyword.replaceAll("[^\u4E00-\u9FA5]", "");
//                Matcher s1Matcher = pattern.matcher(keyword);
//                String s1EN = s1Matcher.replaceAll("");
//
//                String s2CN = songName.replaceAll("[^\u4E00-\u9FA5]", "");
//                Matcher s2Matcher = pattern.matcher(songName);
//                String s2EN = s2Matcher.replaceAll("");
//
//                int editCount = getCNMinEditCount(s1CN, s2CN) + getENMinEditCount(s1EN, s2EN);
//                if (minEditCount > editCount) {
//                    minEditCount = editCount;
//                    songURL = element.selectFirst("a.js_song").attr("href");
//                    name = songName;
//                    author = element.selectFirst("a.singer_name").attr("title");
//                }
//            }
//
//            if (StringUtils.isBlank(songURL)) {
//                return null;
//            }
//            Document document_page = Jsoup.connect(songURL).get();
//            String id = document_page.selectFirst("[data-stat=y_new.song.header.more]").attr("data-id");  //获取音乐真正的id
//            System.out.println("id:" + id + "\n"
//                    + "name:" + name + "\n"
//                    + "author:" + author);
//
//            music = new MusicEntity();
//            music.setMusicMid(id);
//            music.setMusicType("qq");
//            music.setMusicName(name);
//            music.setMusicAuthor(author);
//
//            musicDao.insert(music);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return music;
//        }
//    }
//
//    /**
//     * 计算字符串最小编辑距离
//     *
//     * @param s1
//     * @param s2
//     * @return
//     */
//    private int getENMinEditCount(String s1, String s2) {
//        int leng1 = s1.length();
//        int leng2 = s2.length();
//        if (leng1 == 0 || leng2 == 0) {
//            return leng1 > leng2 ? leng1 * 4 : leng2 * 4;
//        }
//        int[][] lens = new int[leng1 + 1][leng2 + 1];
//        //首先初始化数组的第一行和第一列
//        for (int i = 0; i <= leng1; i++) {
//            lens[i][0] = i;
//        }
//        for (int i = 0; i <= leng2; i++) {
//            lens[0][i] = i;
//        }
//        //计算数组其他位置的大小
//        for (int i = 1; i <= leng1; i++) {
//            for (int j = 1; j <= leng2; j++) {
//                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
//                    lens[i][j] = lens[i - 1][j - 1];
//                } else {
//                    int tmp = Math.min(lens[i][j - 1], lens[i - 1][j]);
//                    lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 1;
//                }
//            }
//        }
//        return lens[leng1 - 1][leng2 - 1];
//    }
//
//    /**
//     * 基于拼音优化的中文最短编辑距离
//     *
//     * @param s1
//     * @param s2
//     * @return 引用：曹犟, 邬晓钧, 夏云庆,等. 基于拼音索引的中文模糊匹配算法[J]. 清华大学学报(自然科学版), 2009(S1):1328-1332.
//     */
//    private int getCNMinEditCount(String s1, String s2) {
//        int leng1 = s1.length();
//        int leng2 = s2.length();
//        int[][] lens = new int[leng1 + 1][leng2 + 1];
//        //首先初始化数组的第一行和第一列
//        for (int i = 0; i <= leng1; i++) {
//            lens[i][0] = i * 4;
//        }
//        for (int i = 0; i <= leng2; i++) {
//            lens[0][i] = i * 4;
//        }
//
//        //计算数组其他位置的大小
//        for (int i = 1; i <= leng1; i++) {
//            for (int j = 1; j <= leng2; j++) {
//                if (s1.charAt(i - 1) == s2.charAt(j - 1))    // 如果汉字相同
//                {
//                    lens[i][j] = lens[i - 1][j - 1];
//                } else {
//                    int tmp = Math.min(lens[i][j - 1], lens[i - 1][j]);
//                    String s1Pinyin = HanLP.convertToPinyinString(s1.charAt(i - 1) + "", "", false);
//                    String s2Pinyin = HanLP.convertToPinyinString(s2.charAt(j - 1) + "", "", false);
//
//                    String s1Shenmu = "";
//                    String s1Yunmu = "";
//                    if (PinyinUtil.isShenmu("" + s1Pinyin.charAt(0))) {   // 如果有声母
//                        if (s1Pinyin.charAt(1) == 'h') {
//                            s1Shenmu = s1Pinyin.substring(0, 2);
//                            s1Yunmu = s1Pinyin.substring(2, s1Pinyin.length());
//                        } else {
//                            s1Shenmu = s1Pinyin.substring(0, 1);
//                            s1Yunmu = s1Pinyin.substring(1, s1Pinyin.length());
//                        }
//                    } else {
//                        s1Yunmu = s1Pinyin;
//                    }
//
//                    String s2Shenmu = "";
//                    String s2Yunmu = "";
//                    if (PinyinUtil.isShenmu("" + s2Pinyin.charAt(0))) {   // 如果有声母
//                        if (s2Pinyin.charAt(1) == 'h') {
//                            s2Shenmu = s2Pinyin.substring(0, 2);
//                            s2Yunmu = s2Pinyin.substring(2, s2Pinyin.length());
//                        } else {
//                            s2Shenmu = s2Pinyin.substring(0, 1);
//                            s2Yunmu = s2Pinyin.substring(1, s2Pinyin.length());
//                        }
//                    } else {
//                        s2Yunmu = s2Pinyin;
//                    }
//
//
//                    if (s1Shenmu.equals(s2Shenmu) && s1Yunmu.equals(s2Yunmu)) {  // 音调不同，编辑距离+1
//                        lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 1;
//                    } else if (s1Yunmu.equals(s2Yunmu)) {       // 声母不同
//                        if (((s1Shenmu.equals("n") && s2Shenmu.equals("l")) || (s1Shenmu.equals("l") && s2Shenmu.equals("n")))
//                                || ((s1Shenmu.equals("z") && s2Shenmu.equals("zh")) || (s1Shenmu.equals("zh") && s2Shenmu.equals("z")))
//                                || ((s1Shenmu.equals("s") && s2Shenmu.equals("sh")) || (s1Shenmu.equals("sh") && s2Shenmu.equals("s")))) {
//                            lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 1;
//                        } else {
//                            lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 2;
//                        }
//                    } else if (s1Shenmu.equals(s2Shenmu)) {       // 韵母不同
//                        if (((s1Yunmu.equals("an") && s2Yunmu.equals("ang")) || (s1Yunmu.equals("ang") && s2Yunmu.equals("an")))
//                                || ((s1Yunmu.equals("en") && s2Yunmu.equals("eng")) || (s1Yunmu.equals("eng") && s2Yunmu.equals("en")))
//                                || ((s1Yunmu.equals("in") && s2Yunmu.equals("ing")) || (s1Yunmu.equals("ing") && s2Yunmu.equals("in")))
//                                || ((s1Yunmu.equals("in") && s2Yunmu.equals("ing")) || (s1Yunmu.equals("ing") && s2Yunmu.equals("in")))) {
//                            lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 1;
//                        } else {
//                            lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 2;
//                        }
//                    } else {    // 声母和韵母都不同
//                        lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 4;
//                    }
//                }
//            }
//        }
//
//        return lens[leng1][leng2];
//    }
//}
