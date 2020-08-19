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

```
    //部分JS代码  支持多文件批量打印
    var srcFiles = [];
    var printFileDTO = {};
    printFileDTO["fileUrl"] = realPath;  //需要打印的pdf文件url地址
    printFileDTO["fileName"] = fileName; //需要打印的pdf文件名【可选】
    printFileDTO["landscape"] = true;    //是否需要横打【可选】
    srcFiles.push(printFileDTO);

     $.post("http://127.0.0.1:8281/print", JSON.stringify(srcFiles), function(data) {
                console.log(data)
            }, 'json')
```
#### 运行界面
 通过浏览器发起的打印任务会保存到如下界面列表中，方便本地重复打印
![输入图片说明](https://images.gitee.com/uploads/images/2020/0819/183126_163dd4c0_108150.png "批注 2020-08-19 183107.png")
