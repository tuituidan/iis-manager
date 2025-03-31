> 本服务停止更新，有新的优化扩展见【windows-manager】 [github](https://github.com/tuituidan/windows-manager)、[gitee](https://gitee.com/tuituidan/windows-manager)，其包含了本服务的所有功能



# IIS管理

## 背景

近来帮.NET组的人维护一些.NET的老项目，项目使用IIS部署，上面部署了很多个应用，由不同的人进行维护，每次部署总是出现争抢远程登录的情况，增加Windows登录账号也无法解决，毕竟你并不知道你将要登录的账号是否有人正在使用。

替换发布文件使用FTP即可解决，但是有些文件替换IIS无法检测，还是需要手动在IIS上重启应用，于是我就想IIS是否有像Tomcat那样的基于web的管理页面，搜索一番只搜到一个[Servant for IIS](https://www.iis.net/downloads/community/2013/05/servant-for-iis)，看起来就是解决web上访问IIS的，然而很遗憾的是下载链接对应的github仓库已经被删除了，没能找到可以下载的地方，似乎是已经被放弃的项目，也没找到其他可以替代的产品，于是考虑自己简单实现一个。

## 实现原理

IIS7开始提供的`C:\Windows\System32\inetsrv\appcmd.exe`这个工具可以通过cmd命令来管理IIS

- `appcmd list site`命令：获取IIS应用列表，其中有各个网站的名称，绑定域名，状态等
- `appcmd start site "XX网站"`：启动IIS下的XX网站
- `appcmd stop site "XX网站"`：停止IIS下的XX网站

## 部署

直接从[Release](https://github.com/tuituidan/iis-manager/releases)中下载jar包，在Windows下，执行`java -jar iis-manager.jar`即可。

> 注意：执行appcmd命令需要Windows管理员权限，idea进行开发调试时也需要使用管理员权限启动。

## license

100%开源，MIT协议，可自由修改

## 功能简介

- 无数据库
- 可通过`spring.security.enabled=true`开启简单的登录控制，在`springboot`的`yml`配置中配置账号密码（默认账号密码：admin  / admin123）。
- 在线启动、停止、重启网站。
- 在线启动、停止、回收应用程序池。
- 在线显示网站部署目录文件，可上传替换文件，在线查看文本文件，下载文件，对于大量文件可上传压缩包自动解压。

## 演示图

![首页](https://github.com/tuituidan/iis-manager/assets/20398244/a8ea2704-c611-4ff5-9f0e-b8b446e657df)

![文件管理](https://github.com/tuituidan/iis-manager/assets/20398244/3ba333fe-9d33-4db0-b58e-aa82f91fbfd8)
