package com.dingdo.extendService.musicService.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.model.musicFrom163.Music163;
import com.dingdo.extendService.model.musicFrom163.Music163Random;
import com.dingdo.extendService.model.musicFrom163.Music163Song;
import com.dingdo.extendService.model.musicFromQQ.MusicQQ;
import com.dingdo.extendService.model.musicFromQQ.SingerList;
import com.dingdo.extendService.model.musicFromQQ.SongList;
import com.dingdo.extendService.model.specialReply.MusicReplyCard;
import com.dingdo.extendService.musicService.MusicService;
import com.dingdo.extendService.musicService.enums.MusicDescEnums;
import com.dingdo.extendService.musicService.enums.MusicPreviewUrlEnums;
import com.dingdo.msgHandler.model.ReqMsg;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 音乐服务接口的实现类
 */
@Service
public class MusicServiceImpl implements MusicService {

    // 使用log4j打印日志
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MusicServiceImpl.class);

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Autowired
    public MusicServiceImpl(RestTemplate restTemplate, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    @Override
    public String sendReply(ReqMsg reqMsg) {
        return getMusicCQCodeFrom163(reqMsg.getRawMessage());
    }


    /**
     * 获取发送音乐卡片的CQ码
     *
     * @param reqMsg 请求消息
     * @return 请求结果
     */
    @Override
    public String getMusicCQCode(ReqMsg reqMsg) {
        return getMusicCQCodeFrom163(reqMsg.getRawMessage());
    }


    /**
     * 从网抑云随机播放歌曲的指令方法
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    @Instruction(description = "网抑云")
    private String getRandMusicFrom163(ReqMsg reqMsg, Map<String, String> params) {
        ResponseEntity<String> response = restTemplate.getForEntity(UrlEnum.MUSIC_163_RAND_MUSIC.toString(), String.class);
        Music163Random music163Random = JSON.parseObject(response.getBody(), Music163Random.class);
        return createMusicReplyCardByMusic(Objects.requireNonNull(music163Random)).toString();
    }


    /**
     * 从网抑云点歌的指令方法
     * <p>
     * 从网抑云的搜索api查询歌曲，将查询到的歌曲变成cq码字符串后返回。
     * 默认以{@code params}的第一个参数为歌曲关键词
     * 如果{@code params}为空，则返回字符串"你要点哪首歌"
     * </p>
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     * @see #getMusicCQCodeFrom163(String)
     */
    @Instruction(description = "点歌")
    private String getMusicCQCodeFrom163(ReqMsg reqMsg, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return "你要点哪首歌?";
        }
        return getMusicCQCodeFrom163((String) params.keySet().toArray()[0]);
    }


    /**
     * 使用关键词从网抑云点歌
     * <p>
     * 从网抑云的api接口点歌
     * 如果搜索的关键词为空白字符，则返回"你要点哪首歌"
     * </p>
     *
     * @param keyword 关键词
     * @return 音乐卡片的cq码
     */
    private String getMusicCQCodeFrom163(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return "你要点哪首歌?";
        }
        Music163 music163 = getMusicFrom163Api(keyword);
        Music163Song firstSong = music163.getData().getSongs().get(0);

        return createMusicReplyCardByMusic(firstSong).toString();
    }


    /**
     * 请求网易云音乐的搜索api
     * <p>
     * 使用网抑云音乐的搜索api获取歌曲列表，并将其转换为{@link Music163}对象
     * </p>
     *
     * @param keyword 关键词
     * @return {@link Music163}对象
     */
    private Music163 getMusicFrom163Api(String keyword) {
        String url = UrlEnum.MUSIC_163_SEARCH + keyword;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return JSONObject.parseObject(response.getBody(), Music163.class);
    }


    /**
     * 通过{@link Music163}对象创建音乐卡片的cq码
     *
     * @param song {@link Music163}对象
     * @return 音乐卡片cq码
     */
    private MusicReplyCard createMusicReplyCardByMusic(Music163Song song) {
        String title = song.getName();
        String musicUrl = UrlEnum.MUSIC_163_SONG + song.getId() + "&amp";
        String jumpUrl = "";
        String preview = MusicPreviewUrlEnums.getRandomUrl();
        String desc = MusicDescEnums.Companion.getRandomDesc();
        String tag = "网抑云";

        return new MusicReplyCard(desc, jumpUrl, musicUrl, preview, tag, title);
    }


    /**
     * 通过{@link Music163Random}对象创建音乐卡片cq码
     *
     * @param song {@link Music163Random}
     * @return 音乐卡片cq码
     */
    private MusicReplyCard createMusicReplyCardByMusic(Music163Random song) {
        String title = song.getData().getName();
        String musicUrl = song.getData().getUrl();
        String jumpUrl = "";
        String preview = MusicPreviewUrlEnums.getRandomUrl();
        String desc = MusicDescEnums.Companion.getRandomDesc();
        String tag = "网抑云";

        return new MusicReplyCard(desc, jumpUrl, musicUrl, preview, tag, title);
    }


    @Deprecated
    private void getMusicFromMusicQQApi(String keyword) {
        String url = UrlEnum.QQ_MUSIC_SEARCH
                + keyword + "&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";

        MusicQQ musicQQ = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            musicQQ = JSONObject.parseObject(response.getBody(), MusicQQ.class);
        } catch (Exception e) {
            logger.error("api请求中断", e);
        }

        SongList firstSong = Objects.requireNonNull(musicQQ).getData().getSong().getList().get(0);
        List<SingerList> singerList = firstSong.getSinger();


        String mid = firstSong.getMid();
        String songUrl = UrlEnum.QQ_MUSIC_SONG + mid + ".html";


        try {
            // HtmlUnit 模拟浏览器
            HtmlPage page = webClient.getPage(songUrl);

            String pageAsXml = page.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, songUrl);
            Element result = doc.selectFirst("img.data__photo");
            String resultUrl = "http://" + result.attr("src");

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder author = new StringBuilder();
        author.append(singerList.get(0).getName());
        for (int i = 1; i < singerList.size(); i++) {
            author.append(",").append(singerList.get(i).getName());
        }
    }


    @Deprecated
    private void getMusicFromEnternet(String keyword) {

        try {
            String musicName = URLEncoder.encode(keyword, "UTF-8");
            String url = UrlEnum.QQ_MUSIC_SEARCH.getUrl() + musicName;

            // HtmlUnit 模拟浏览器
            HtmlPage page = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(7 * 1000);     // 等待js后台执行3秒

            String pageAsXml = page.asXml();

            // Jsoup解析处理
            Document doc = Jsoup.parse(pageAsXml, url);
            Elements songList = doc.select("div.songlist__item");

            //获取歌曲列表后进行遍历
            Iterator<Element> iterable = songList.iterator();
            int minEditCount = 2147483647;
            String songURL = "";
            String name = "";
            String author = "";
            while (iterable.hasNext()) {
                Element element = iterable.next();
                Element songElement = element.selectFirst("a.js_song");
                if (null == songElement) {
                    continue;
                }
                String songName = songElement.attr("title");
                songName = songName.replaceAll("[^\u4E00-\u9FA5]", "");

                String reg = "[\u4e00-\u9fa5]";
                Pattern pattern = Pattern.compile(reg);

                String s1CN = keyword.replaceAll("[^\u4E00-\u9FA5]", "");
                Matcher s1Matcher = pattern.matcher(keyword);
                String s1EN = s1Matcher.replaceAll("");

                String s2CN = songName.replaceAll("[^\u4E00-\u9FA5]", "");
                Matcher s2Matcher = pattern.matcher(songName);
                String s2EN = s2Matcher.replaceAll("");

//                int editCount = getCNMinEditCount(s1CN, s2CN) + getENMinEditCount(s1EN, s2EN);
//                if (minEditCount > editCount) {
//                    minEditCount = editCount;
//                    songURL = element.selectFirst("a.js_song").attr("href");
//                    name = songName;
//                    author = element.selectFirst("a.singer_name").attr("title");
//                }
            }

            if (StringUtils.isBlank(songURL)) {
                return;
            }
            Document document_page = Jsoup.connect(songURL).get();
            String id = document_page.selectFirst("[data-stat=y_new.song.header.more]").attr("data-id");  //获取音乐真正的id

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算字符串最小编辑距离
     *
     * @param s1
     * @param s2
     * @return
     */
    private int getENMinEditCount(String s1, String s2) {
        int leng1 = s1.length();
        int leng2 = s2.length();
        if (leng1 == 0 || leng2 == 0) {
            return leng1 > leng2 ? leng1 * 4 : leng2 * 4;
        }
        int[][] lens = new int[leng1 + 1][leng2 + 1];
        //首先初始化数组的第一行和第一列
        for (int i = 0; i <= leng1; i++) {
            lens[i][0] = i;
        }
        for (int i = 0; i <= leng2; i++) {
            lens[0][i] = i;
        }
        //计算数组其他位置的大小
        for (int i = 1; i <= leng1; i++) {
            for (int j = 1; j <= leng2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    lens[i][j] = lens[i - 1][j - 1];
                } else {
                    int tmp = Math.min(lens[i][j - 1], lens[i - 1][j]);
                    lens[i][j] = Math.min(tmp, lens[i - 1][j - 1]) + 1;
                }
            }
        }
        return lens[leng1 - 1][leng2 - 1];
    }

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
}
