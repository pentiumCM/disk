# disk
Min Chen project 初始版本

基于SpringBoot的网盘存储系统

整体系统采用MVC架构，基于Tomcat web服务器，负责处理用户的请求，B/S模式，后台采用SpringBoot及JPA框架,前端UI层采用html+css+jquery+vue+bootstrap等前端框架。数据库端采用MySQL数据库及数据库管理可视化工具：Navicat premium12,开发工具使用IntelliJ IDEA

本系统的业务层主要分为如下模块：用户管理模块，文件管理模块、其他模块
角色分为：超级管理员，普通用户
一、用户管理模块
1.	用户注册：用户填写用户名与密码，信息验证通过之后成为本系统的用户. (300)
2.	用户登录：用户填写用户名和密码，用户名和密码匹配成功之后可以登陆本系统
3.	用户基本信息设置与修改，修改密码、设置个人头像等。(300)
4.	管理员可禁用用户，删除用户，维护其他用户信息，重置密码。(300)
二、文件管理模块
5.	用户上传文件：支持秒传文件，断点上传，大文件上传. (300)
6.	下载文件,删除文件，搜索文件：下载时支持断点继传，加密传输，更加安全可靠，删除时，都会有权限认证、仅可操作自己的文件，无法删除别人的文件，保证文件的安全可靠。搜索文件，支持文件名进行查询。(500)
7.	文件共享：用户设置某一文件为共享文件(有密码、直接可下载)，可设置失效时间，文件共享后，用户可直接保存至自己的网盘，也可下载。(300)
8.	文件上传后，将对权限进行细化设置，非本人上传的文件，除分享外，其他人无权访问。(300)
三、其他模块
9.	分类展示文件：用户可在不同的类别下建立文件夹，文件可放在不同的文件夹下。
类别分为全部文件，图片，文档，视频，音乐，其他文件。用户上传的文件当自动进行分类。(600)
10.	图片的智能分类：不同的图片上传后，可根据地点，人物，事物等进行分类。(800) 使用的是百度云的图片识别，上传一个图片后，生成对应的标签，权重，关键字，放到AdvancedGeneral表中，文件上传到某一目录后，确认上传，将图片对应的标签放入FileGeneral中。这样，就可以看到图片自动生类别标签内容、。

https://cloud.baidu.com/doc/IMAGERECOGNITION/ImageClassify-API.html#.8E.38.62.36.5E.3A.0A.A2.9C.04.06.2C.AC.EF.F2.FC

通用图像分析——通用物体和场景识别高级版
更新时间：2019-02-28
接口描述
该请求用于通用物体及场景识别，即对于输入的一张图片（可正常解码，且长宽比适宜），输出图片中的多个物体及场景标签。
 

11.	回收站：用户删除文件后，将在一个月内进入回收站，超过一个月将自动清空，清空回收站时需要输入密码。(300)、主要功能分为彻底删除，清空回收站，还原文件，还原文件夹，还原所有文件及文件夹。

全部文件管理：文件夹与文件 之间的跳转，导航文件目录，上一级目录。修改文件夹名。修改文件名。删除文件、

用户名 admin 密码123qwe
普通用户 igeekfan 密码 123qwe
新建一个cloud_disk的数据库，默认配置如下面的图片文件
 
如下配置项，为百度云中的图像识别模块下的app_id,api_key,secret_key
