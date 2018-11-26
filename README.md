# 跨浏览器打印方案

#### 项目介绍
跨浏览器打印的控件网上很多，露肚皮不支持PDF打印，jatools授权费太高。。。。
所以那么自己动手吧，反正也是内部使用，可以要求每台电脑都要安装jre，实在不想把jre打到可执行包里去，那样略大。
经过测试，这个套路是行得通的，现在放出来给用得着的朋友分享下。

#### 软件架构
 通过Java模拟http服务器，默认监听8281端口，用Java的方式调用本地打印机，而web页面通过ajax的跨域请求传递需要打印的信息和参数


#### 安装教程

1. 编译为可执行jar包
2. 写个bat脚本加入到系统计划任务中，保证开机启动即可。

#### 使用说明

1. 一个ajax请求搞定
    var srcFiles = [];
    var printFileDTO = {};
    printFileDTO["fileUrl"] = realPath;  //需要打印的pdf文件url地址
    printFileDTO["fileName"] = fileName; //需要打印的pdf文件名【可选】
    printFileDTO["landscape"] = true;    //是否需要横打【可选】
    srcFiles.push(printFileDTO);

     $.post("http://127.0.0.1:8281/print", JSON.stringify(srcFiles), function(data) {
                console.log(data)
            }, 'json')


#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)