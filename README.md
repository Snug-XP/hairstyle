### 项目说明


1．使用Springboot框架快速生成后端项目结构
2．架构前后端分离，使用Restful设计风格的接口进行交互，使用swagger工具生成接口测试页面（https://127.0.0.1:8080/swagger-ui.html#/ )
3．使用showdoc在线接口文档工具编写接口信息文档（https://www.showdoc.cc/jianzhao 访问密码123456）
4. 使用JPA数据持久化框架实现对MySQL数据库的操作
5. 使用阿里云OSS存储小程序内用户提交的文件。
6. 使用了webhook+jenkins实现自动化部署。


- 运行jar包（后台运行）：nohup java -jar hairstyle.jar >log.txt &
- 运行jar包（后台运行）：nohup java -jar hairstyle-0.0.1-SNAPSHOT.jar >log.txt &
- 查看某端口占用的线程pid：netstat -nlp |grep :8080
- 查看当前已运行的进程： ps -x
- 根据pid终止进程：kill pid
- 查看是否有tomcat运行：ps -ef|grep tomcat
- 如果tomcat服务没有运行，结果如下 root      8522  6570  0 22:06 pts/1    00:00:00 grep --color=auto tomcat
- 命令kill -9 （pid号）  可关闭对应pid的服务
- 执行一下命令实时查看运行日志 tail -f catalina.out

windows中:
查看指定端口的占用情况 C:\>netstat -aon|findstr "9050"

查看服务：services.msc

