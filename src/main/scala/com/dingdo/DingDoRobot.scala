package com.dingdo


//import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.swagger2.annotations.EnableSwagger2

//@MapperScan(basePackages = Array("com.dingdo.**.mapper"))
@EnableSwagger2
@EnableScheduling
@SpringBootApplication
class DingDoRobot

object DingDoRobot extends App {
  val springApplication = new SpringApplication(classOf[DingDoRobot])
  springApplication.run(args: _*)
}