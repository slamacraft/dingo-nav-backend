package com.example.demo;

import com.dingdo.component.classifier.NaiveBayesClassifierComponent;
import com.dingdo.util.SpringContextUtils;
import com.forte.qqrobot.bot.BotManager;
import lombok.Data;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @Test
    public void testKQ(){
        String msg = "[CQ:app,content={ \"app\": \"com.tencent.structmsg\"&#44; \"config\": { \"autosize\": true&#44; \"ctime\": 1600045277&#44; \"forward\": true&#44; \"token\": \"84ff3e04d8f04a446510631fb3afbfca\"&#44; \"type\": \"normal\" }&#44; \"desc\": \"音乐\"&#44; \"meta\": { \"music\": { \"action\": \"\"&#44; \"android_pkg_name\": \"\"&#44; \"app_type\": 1&#44; \"appid\": 100495085&#44; \"desc\": \"RAM WIRE\"&#44; \"jumpUrl\": \"\"&#44; \"musicUrl\": \"http:\\/\\/music.163.com\\/song\\/media\\/outer\\/url?id=32317208&amp;userid=111277112\"&#44; \"preview\": \"http:\\/\\/p2.music.126.net\\/V1o9XDhAnI1ayWW5elJwFQ==\\/109951163338252165.jpg\"&#44; \"sourceMsgId\": \"0\"&#44; \"source_icon\": \"\"&#44; \"source_url\": \"\"&#44; \"tag\": \"网易云音乐\"&#44; \"title\": \"僕らの手には何もないけど、 (尽管我们手中空无一物)\" } }&#44; \"prompt\": \"&#91;分享&#93;僕らの手には何もないけど、 (尽管我们手中空无一物)\"&#44; \"ver\": \"0.0.0.1\"&#44; \"view\": \"music\" }]收到&#91;&#91;分享&#93;僕らの手には何もないけど、 (尽管我们手中空无一物)&#93;消息，请升级QQ版本查看";
        BotManager bean = SpringContextUtils.getBean(BotManager.class);
        bean.getBot("3087687530").getSender().SENDER.sendPrivateMsg("1114951452", msg);
    }


    @Test
    public void test2() {
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        List<String> list2 = new ArrayList<>();
        list2.add("4");
        list2.add("5");
        list2.add("6");
        List<List<String>> list = new ArrayList<>();
        list.add(list1);
        list.add(list2);

        List<String> stringList = list.stream().reduce((item1, item2) -> {
            item1.addAll(item2);
            return item1;
        }).get();

        System.out.println(stringList);
    }

    @Data
    public class TestClazz{
        int a;
        int b;
        List<List<String>> list = new ArrayList<>();
    }

    @Test
    public void test3() {
        SparkSession spark = SparkSession
                .builder()
                .appName("test")
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file///:G:/Projects/Java/Spark/spark-warehouse")
                .getOrCreate();

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        List<String> list2 = new ArrayList<>();
        list2.add("4");
        list2.add("5");
        list2.add("6");
        List<List<String>> list = new ArrayList<>();
        list.add(list1);
        list.add(list2);

        List<TestClazz> testList = new ArrayList<>();
        TestClazz testClazz = new TestClazz();
        testList.add(testClazz);
        testClazz.getList().add(list1);
        testClazz.getList().add(list2);

        Dataset<Row> dataFrame = spark.createDataFrame(testList, TestClazz.class);
        dataFrame.show();

        dataFrame.withColumn("testCol", new Column("list"));

        dataFrame.show();
    }

    @Test
    public void reidsTemplateTest(){
        RedisTemplate redisTemplate = new RedisTemplate();

    }

}
