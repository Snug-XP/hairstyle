### 项目说明

使用Springboot框架、持久层使用JPA生成，架构前后端分离，使用Restful设计风格的接口进行交互，接口文档方面使用swagger生成。

### 进度说明
目前完成项目基本的crud，微信小程序方面功能也已经完成，下一步主要是写业务

### 其他
oss配置可能过期，需要重新设置

1. 接口文档路径 https://127.0.0.1:8080/swagger-ui.html#/
2. 连接池路径 https://localhost:8080/druid/login.html (账号root密码toor)


- 运行jar包（后台运行）：nohup java -jar hairstyle.jar >log.txt &
- 查看某端口占用的线程pid：netstat -nlp |grep :8080
- 查看当前已运行的进程： ps -x
- 根据pid终止进程：kill pid
- 查看是否有tomcat运行：ps -ef|grep tomcat
- 如果tomcat服务没有运行，结果如下 root      8522  6570  0 22:06 pts/1    00:00:00 grep --color=auto tomcat
- 命令kill -9 （pid号）  可关闭对应pid的服务
- 执行一下命令实时查看运行日志 tail -f catalina.out

windows中查看指定端口的占用情况
C:\>netstat -aon|findstr "9050"

