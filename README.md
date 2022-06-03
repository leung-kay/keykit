# KeyKit
## 目标
&emsp;&emsp;起初只是一段方便“半自动”读写Excel的工具方法，后面又加入了工作学习中需要用到的其他工具方法，比如时间转换和日志记录工具类。<p/>
&emsp;&emsp;项目尽量保持简洁，最小化依赖其他项目。
## 内容
1. workbook<p/>
    封装了POI读写Excel的功能，简化了部分概念，整合了使用流程：<p/>
    * 使用`WorkbookKit.read("文件全路径", 内容区域左上角单元格横坐标, 内容区域左上角单元格纵坐标, 内容列数);`就可以返回用`List`嵌套的文件内容。
    * 使用`WorkbookKit.build("导出模板全路径").header(表头左下单元格横坐标, 表头左下单元格纵坐标, 表头列数).content(一行内容).content(另一行内容).export("导出文件全路径");`就可以写出内容到Excel模板中。
    * 对使用者屏蔽Sheet（表单）、Region（合并单元格）、Row（行）、Cell（单元格）、CellType（单元格类型）、CellStyle（单元格样式）这些概念。
2. date<p/>
    针对Java8的时间对象封装了各种类型互转的方法：<p/>
    * 使用方法`DateKit.from(一个类型的值).to(另一个类型);`
    * 支持互转的类型（在from和to中可以声明的）：`Long`、`String`、`LocalDateTime`、`LocalDate`。
3. log<p/>
    日志记录，主要针对需要记录方法执行时长，尤其是for循环执行时长的场景：<p/>
    * 在需要打印日志的对象中声明变量`PingPong pingPong = new PingPong(Logger实例);`
    * 在需要开始计时的地方打点`pingPong.ping("打印的内容");`在需要结束计时的地方打点`pingPong.pong("打印的内容");`
    * 需要***注意***的是在`ping`方法处打印的内容必须与`pong`处打印的内容一致，否则可能造成内存溢出。
## 计划
&emsp;&emsp;在有issue没有完成的情况下至少保持一月一更。<p/>
&emsp;&emsp;六月份计划：
1. [完成日志记录工具类的单元测试](https://github.com/leung-kay/keykit/issues/1)
2. [去除commons-io包的依赖](https://github.com/leung-kay/keykit/issues/2)