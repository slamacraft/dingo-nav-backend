//package com.dingdo.config.configuration
//
//import org.springframework.beans.factory.annotation.{Configurable, Value}
//import org.springframework.context.annotation.Bean
//import slick.jdbc.MySQLProfile.api._
//import slick.jdbc.MySQLProfile.backend.DatabaseDef
//
//import javax.sql.DataSource
//
//@Configurable
//class SlickConfig {
//  @Value("${spring.datasource.url}")
//  var url: String = _
//  @Value("${spring.datasource.driver-class-name}")
//  var driver: String = _
//  @Value("${spring.datasource.username}")
//  var user: String = _
//  @Value("${spring.datasource.password}")
//  var password: String = _
//  @Value("spring.datasource.druid.max-active")
//  var druidMaxActive: Int = _
//
//  @Bean
//  def db(dataSource: DataSource): DatabaseDef = {
//    val db = Database.forDataSource(dataSource, Some(druidMaxActive))
//    SlickConfig.DB = db
//    db
//  }
//}
//
//object SlickConfig {
//  var DB:DatabaseDef = _
//}