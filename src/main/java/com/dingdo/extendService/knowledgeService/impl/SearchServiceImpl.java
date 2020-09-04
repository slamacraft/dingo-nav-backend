//package com.dingdo.extendService.knowledgeService.impl;
//
//import com.dingdo.enums.UrlEnum;
//import com.dingdo.extendService.knowledgeService.SearchService;
//import com.dingdo.extendService.model.msgFromCQ.SearchMsg;
//import com.dingdo.msgHandler.model.ReqMsg;
//import com.dingdo.util.NLPUtils;
//import com.hankcs.hanlp.seg.common.Term;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.URLEncoder;
//import java.util.List;
//
//@Service
//public class SearchServiceImpl implements SearchService {
//
//    private Logger logger = Logger.getLogger(SearchServiceImpl.class);
//
//    @Autowired
//    StringRedisTemplate redisTemplate;
//
//    @Override
//    public String sendReply(ReqMsg reqMsg) {
//        List<Term> keywordList = NLPUtils.getNER(reqMsg.getRawMessage());
//        String String = new String();
//        if (CollectionUtils.isEmpty(keywordList)) {
//            return "请问你要查询什么";
//        }
//
//        StringBuffer reply = new StringBuffer();
//        for (Term keyword : keywordList) {
//            reply.append(keyword + ":" + searchHandler(keyword.word) + "\n");
//        }
//
//        return reply.toString();
//    }
//
//
//    @Override
//    public String getReply(ReqMsg reqMsg) {
//        return null;
//    }
//
//
//    @Override
//    public String stdSearch(ReqMsg reqMsg) {
//        String String = new String();
//        String keyword = reqMsg.getRawMessage().split("搜索")[1].trim();
//
//        if (StringUtils.isBlank(keyword)) { // 关键词为空
//            return "请问你要查询什么";
//        }
//
//        return keyword + ":" + searchHandler(keyword);
//    }
//
//
//    /**
//     * 搜索处理器
//     *
//     * @param keyword
//     * @return
//     */
//    private String searchHandler(String keyword) {
//        SearchMsg searchInfo = searchFromAPI(keyword);
//
//        if(searchInfo == null || StringUtils.isBlank(searchInfo.getMessage())){
//            return "很遗憾，并没有找到" + keyword + "\n" + "(。﹏。*)";
//        }
//
//        return searchInfo.getMessage();
//    }
//
//    /**
//     * 将搜索到的名词写入字典
//     *
//     * @param keyword 关键词
//     */
//    @Deprecated
//    private void addKeywordToDict(String keyword) {
//        FileWriter fw = null;
//        try {
//            //如果文件存在，则追加内容；如果文件不存在，则创建文件
//            File f = new File(this.getClass().getResource("/python/CQPython/static/dict/dict.txt").getPath().replaceAll("!", ""));
//            fw = new FileWriter(f, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        PrintWriter pw = new PrintWriter(fw);
//
//        String newWord = keyword + " " + "10" + " " + "n";
//
//        pw.println(newWord);
//        pw.flush();
//        try {
//            fw.flush();
//            pw.close();
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public SearchMsg searchFromAPI(String keyword) {
//        SearchMsg searchMsg = new SearchMsg();
//        searchMsg.setKeyword(keyword);
//
//        logger.warn("正在搜索关键字:" + keyword);
//
//        try {
//            keyword = URLEncoder.encode(keyword, "UTF-8");
//
//            String url = UrlEnum.BAI_KE + keyword;
//
//            Document document = Jsoup.connect(url).get();
//
//            Element element = document.selectFirst("div.body-wrapper");
//            Elements select = element.select("div.content-wrapper")
//                    .select("div.content")
//                    .select("div.main-content")
//                    .select("div.lemma-summary");
//
//            String msg = select.text();
//
//            if (msg == null || msg.equals("")) {
//                Element element_1 = document.selectFirst("div.body-wrapper feature feature_small movieSmall");
//                Elements select_1 = element.select("div.feature_poster")
//                        .select("div.poster")
//                        .select("dl.con")
//                        .select("dd.desc")
//                        .select("div.lemma-summary");
//                msg = select_1.text();
//            }
//
//            if (msg != null) {
//                System.out.println(msg);
//                searchMsg.setMessage(msg);
//                return searchMsg;
//            }
//
//        } catch (Exception e) {
//            logger.error("未找到词条" + keyword, e);
//        }
//        return null;
//    }
//}
