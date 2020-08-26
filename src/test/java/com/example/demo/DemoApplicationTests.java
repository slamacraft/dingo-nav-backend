package com.example.demo;

import com.dingdo.Component.classifier.NaiveBayesClassifierComponent;
import lombok.Data;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class DemoApplicationTests {

    @Test
    public void test() throws Exception {
        NaiveBayesClassifierComponent naiveBayesClassifierComponent = new NaiveBayesClassifierComponent();
        naiveBayesClassifierComponent.test();

        naiveBayesClassifierComponent.predict("长沙今天气温多少度");
        naiveBayesClassifierComponent.predict("长沙今天适合钓鱼吗");
        naiveBayesClassifierComponent.predict("长沙适合洗车吗");
        naiveBayesClassifierComponent.predict("长沙今天好热");
        naiveBayesClassifierComponent.predict("长沙今天要防晒吗");
        naiveBayesClassifierComponent.predict("长沙今天要穿什么衣服");

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
    }

}
