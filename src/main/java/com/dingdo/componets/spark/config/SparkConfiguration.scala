package com.dingdo.componets.spark.config

import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.{Bean, Configuration}


//@Configuration
class SparkConfiguration {

//  @Bean
  def sparkSession(): SparkSession =
    SparkSession.builder
      .appName("Robot")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file///:G:/Projects/Java/Spark/spark-warehouse")
      .getOrCreate()
}
