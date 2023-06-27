# dingo-nav-backend
## 这啥？
* 这是[Moyu-nav](https://github.com/pigLi66/csOfHnuc/tree/private-yh-nav)的后端实现
* 使用<code>typescript</code> + <code>nodejs</code> + <code>express</code>开发
* 数据库使用<code>mongodb</code>

## 这能干啥？
~~这只是个简单的后端，并不能干什么~~

* 其实是个导航页，不过导航页的页面可以一定程度上自定义
* 导航页上可以自定义放置一些套件工具
* 登录后可以自定义套件工具(通过自己编写H5)
* 引入了Live2d(基于[L2Dwidget](https://www.fghrsh.net/post/123.html))
* (Feature-套件)能够进行一些聊天（基于[思知机器人Api](https://console.ownthink.com)，在考虑是不是要和live2d做交互）
* (Feature-套件)内置小番茄钟，能够为动态设定一些定时提醒
* (Feature-还没对接)可以在qq里查询天气（基于[和风天气api](https://dev.heweather.com/docs/api/overview)）
* (Deprecated-没用)能够设定一些迎新消息
* (Deprecated-没用)有一些小巧的复读功能
* (Deprecated-没Spark了)内置非常非常简单的语义分类模型（基于spark的mllib库，可以按需要进行简单的扩展）
* ~~未完待续~~

## 还有啥？
独立于其他JVM平台的分支，侧重点从后端逻辑变为前端交互，后端作为交互的持久化补充，
也为了能减少破机器的运行压力，从原来的JVM平台转而使用Nodejs平台进行开发。

更换平台后，阉割掉了以前基于JVM平台生态所开发的一些功能

## 友情链接
[Moyu-nav前端](https://github.com/pigLi66/csOfHnuc/tree/private-yh-nav)<br>
[simple-bot机器人](https://github.com/ForteScarlet/simple-robot-component-mirai)<br>
[mirai机器人](https://github.com/mamoe/mirai)

## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。
特别感谢 [JetBrains](https://www.jetbrains.com/?from=mirai) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=mirai) 等 IDE 的授权  
[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=mirai)