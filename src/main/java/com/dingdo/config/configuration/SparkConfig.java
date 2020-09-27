package com.dingdo.config.configuration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/26 15:45
 * @since JDK 1.8
 */
@Configuration
public class SparkConfig {

    @Bean
    public SparkSession spark(){
        // 如果还没有初始化spark，创建sparkSession
        SparkSession spark = SparkSession
                .builder()
                .appName("dingdoRobot")
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file///:G:/Projects/Java/Spark/spark-warehouse")
                .getOrCreate();

        // 屏蔽spark的INFO日志
//        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
//        Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR);
//        Logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);
//        Logger.getLogger("org.apache.hive").setLevel(Level.WARN);
        SparkSession.builder().getOrCreate().sparkContext();

        return spark;
    }

}
