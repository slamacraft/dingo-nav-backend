package com.dingdo


//import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

//@MapperScan(basePackages = Array("com.dingdo.**.mapper"))
@EnableScheduling
@SpringBootApplication
class DingDoRobot

object DingDoRobot extends App {
  val springApplication = new SpringApplication(classOf[DingDoRobot])
  springApplication.run(args: _*)
}