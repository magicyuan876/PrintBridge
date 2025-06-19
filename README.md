# 跨浏览器打印解决方案 🖨️

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![Jetty](https://img.shields.io/badge/Jetty-9.4-green.svg)](https://www.eclipse.org/jetty/)

一个基于Java Swing + Jetty的跨浏览器打印解决方案，为Web应用提供本地PDF打印支持。

## 项目背景 💭

这个项目最初是为了解决内部系统的跨浏览器PDF打印问题而创建的。时间快进到2025年，发现还有朋友在使用这个项目，于是对代码进行了全面重构：

- 🔄 从原始Socket实现升级到企业级Jetty服务器
- 📊 集成SLF4J + Logback日志系统
- 🎨 使用FlatLaf现代化界面主题
- 🧹 遵循现代Java开发规范

## 核心特性 🚀

- 🌐 **跨浏览器支持**: Chrome、Firefox、Safari、Edge等主流浏览器
- ⚡ **高性能**: 基于Jetty服务器，支持高并发处理
- 📄 **PDF打印**: 基于Apache PDFBox的专业PDF打印
- 🔄 **任务队列**: 打印任务管理，支持批量打印
- 🎨 **现代界面**: FlatLaf主题的简洁用户界面
- 📝 **企业级日志**: 完善的日志记录和监控

## 快速开始 🚀

### 环境要求
- ☕ Java 8 或更高版本
- 🖨️ 系统已配置打印机

### 运行方式

#### 使用Maven
```bash
git clone https://github.com/your-repo/crossbrowser_printing_scheme.git
cd crossbrowser_printing_scheme
mvn clean compile exec:java -Dexec.mainClass="com.genscript.print.PrintApplication"
```

#### 直接运行
```bash
javac -cp "lib/*" -d target/classes src/main/java/**/*.java
java -cp "target/classes:lib/*" com.genscript.print.PrintApplication
```

启动后访问 `http://localhost:8281/health` 验证服务正常。

## API使用 📡

### 打印接口

**POST** `http://localhost:8281/print`

```json
[
  {
    "fileName": "文档.pdf",
    "fileUrl": "http://example.com/document.pdf",
    "landscape": false
  }
]
```

### 前端调用示例

```javascript
// JavaScript调用
function printPDF(fileUrl, fileName) {
    fetch('http://localhost:8281/print', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify([{
            fileName: fileName,
            fileUrl: fileUrl,
            landscape: false
        }])
    }).then(response => response.json())
      .then(data => console.log('打印成功:', data))
      .catch(error => console.error('打印失败:', error));
}
```

```javascript
// jQuery调用
$.ajax({
    url: 'http://localhost:8281/print',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify([{
        fileName: '报告.pdf',
        fileUrl: 'http://example.com/report.pdf',
        landscape: false
    }]),
    success: function(response) { console.log('打印成功'); },
    error: function(error) { console.error('打印失败'); }
});
```

## 配置说明 ⚙️

### 服务器配置
- **监听端口**: 8281 (可在代码中修改)
- **线程池**: 10-100个工作线程
- **支持格式**: PDF文档

### 日志配置
- **日志目录**: `logs/`
- **配置文件**: `src/main/resources/logback.xml`
- **滚动策略**: 按天滚动，保留30天

## 常见问题 🔧

### 服务启动失败
- 检查8281端口是否被占用
- 查看 `logs/print-server-error.log` 错误日志

### 打印无响应
- 确认PDF文件URL可访问
- 检查打印机状态和驱动
- 查看打印队列任务状态

### 跨域访问问题
- 确认服务器已启动在8281端口
- 检查浏览器是否阻止HTTP请求

## 项目结构

```
src/main/java/com/genscript/print/
├── PrintApplication.java              # 应用入口
├── controller/                        # 控制器层
├── service/                          # 服务层  
├── ui/                              # 界面层
├── model/                           # 数据模型
└── config/                         # 配置管理
```

## 版本历史

### v2.0.0 (2025-01-01)
- 🔄 架构重构：从Socket升级到Jetty服务器
- 📝 集成企业级日志系统
- 🎨 现代化界面主题
- 🧹 代码规范化

### v1.0.0 (2020-08-19)  
- 🎉 初始版本：基于Socket的HTTP服务器
- 🖨️ PDF打印功能
- 🌐 跨域访问支持

## 未来计划 🚀

### 📄 文档格式扩展
- **Office文档支持**: Word (.docx)、Excel (.xlsx)、PowerPoint (.pptx) 直接打印
- **图片格式**: PNG、JPG、TIFF、BMP等图片文件打印
- **HTML转PDF**: 网页内容转换为PDF后打印
- **纯文本**: TXT、CSV等文本文件格式化打印

### 🖨️ 打印功能增强
- **打印预设**: 常用打印设置的保存和快速应用
- **批量处理**: 文件夹批量打印，支持文件筛选
- **双面打印**: 智能双面打印控制和页面排序
- **水印支持**: 文字水印、图片水印、时间戳等
- **页面设置**: 边距、缩放、页面方向等精细控制

### ⚡ 性能优化
- **内存优化**: 大文件处理时的内存使用优化
- **打印缓存**: 重复文件的智能缓存机制
- **多线程**: 并行下载和处理多个打印任务
- **断点续传**: 大文件下载中断后可继续

### 🎯 用户体验
- **拖拽支持**: 直接拖拽文件到窗口进行打印
- **快捷键**: 常用操作的键盘快捷键支持
- **打印预览**: 本地预览功能，确认后再打印
- **历史记录**: 打印历史管理和重新打印
- **多语言**: 中文、英文界面切换

## 贡献指南 🤝

欢迎提交Issue和Pull Request！

1. Fork项目
2. 创建特性分支
3. 提交更改
4. 推送分支
5. 创建Pull Request

## 开源协议 📄

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 协议。

## 联系支持 📧

- 📖 查看项目文档
- 📋 检查 `logs/` 目录日志
- 🆕 提交GitHub Issue
- 💬 参与GitHub Discussions

---

**🌟 如果项目对您有帮助，请给个Star支持！**
