package com.example.demo;

import com.dingdo.Component.classifier.NaiveBayesClassifierComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class DemoApplicationTests {

	@Test
	public void test() throws Exception {
        NaiveBayesClassifierComponent naiveBayesClassifierComponent = new NaiveBayesClassifierComponent();
        naiveBayesClassifierComponent.test();

        Scanner scanner = new Scanner(System.in);

        naiveBayesClassifierComponent.predict("长沙今天气温多少度");
        naiveBayesClassifierComponent.predict("长沙今天适合钓鱼吗");
        naiveBayesClassifierComponent.predict("长沙适合洗车吗");
        naiveBayesClassifierComponent.predict("长沙今天好热");
        naiveBayesClassifierComponent.predict("长沙今天要防晒吗");
        naiveBayesClassifierComponent.predict("长沙今天要穿什么衣服");

	}

}
