# PrintBridge 打印桥 🖨️

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![Jetty](https://img.shields.io/badge/Jetty-9.4-green.svg)](https://www.eclipse.org/jetty/)

[![GitHub stars](https://img.shields.io/github/stars/magicyuan876/PrintBridge?style=social)](https://github.com/magicyuan876/PrintBridge/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/magicyuan876/PrintBridge?style=social)](https://github.com/magicyuan876/PrintBridge/network/members)
[![Gitee stars](https://gitee.com/magic_yuan_admin/PrintBridge/badge/star.svg?theme=dark)](https://gitee.com/magic_yuan_admin/PrintBridge/stargazers)
[![Gitee forks](https://gitee.com/magic_yuan_admin/PrintBridge/badge/fork.svg?theme=dark)](https://gitee.com/magic_yuan_admin/PrintBridge/members)

> **连接Web与打印机的智能桥梁** - 一个企业级Web打印中间件,让浏览器应用轻松调用本地打印服务。

基于Java Swing + Jetty构建,为Web应用提供高性能、跨浏览器的本地打印解决方案。

## 为什么选择PrintBridge? 💭

PrintBridge诞生于真实业务场景中的痛点:当Web应用需要调用本地打印机时,传统方案往往复杂、不稳定。PrintBridge提供了一个轻量、可靠的解决方案。

### 项目演进
- **2020**: v1.0 基于Socket的原型系统
- **2025**: v2.0 企业级重构
  - 🔄 从原始Socket实现升级到企业级Jetty服务器
  - 📊 集成SLF4J + Logback日志系统
  - 🎨 使用FlatLaf现代化界面主题
  - 🧹 遵循现代Java开发规范

## 核心特性 🚀

- 🌐 **即插即用**: 一行API调用,任何浏览器都能打印
- 📄 **多格式支持**: PDF、Word、Excel、PPT、图片等格式
- ⚡ **高性能架构**: Jetty服务器 + 多线程队列,轻松应对高并发
- 🔄 **智能转换**: 基于JODConverter + LibreOffice,完美还原格式
- 🎯 **智能队列**: 可视化任务管理,批量打印不混乱
- 🎨 **极简界面**: FlatLaf现代主题,操作直观不费力
- 📝 **生产级日志**: SLF4J + Logback,问题排查有据可依
- 🔒 **安全可控**: 本地服务,数据不出本机

## 快速开始 ⚡

### 前置条件
- ☕ Java 8+ 运行环境
- 🖨️ 至少一台可用的打印机
- 📦 LibreOffice (可选,用于支持Office格式)

### 启动服务

#### 方式一: Maven 快速启动(推荐)
```bash
# 从GitHub克隆
git clone https://github.com/magicyuan876/PrintBridge.git

# 或从Gitee克隆(国内更快)
git clone https://gitee.com/magic_yuan_admin/PrintBridge.git

# 启动服务
cd PrintBridge
mvn clean compile exec:java -Dexec.mainClass="com.genscript.print.PrintApplication"
```

#### 方式二: 直接运行
```bash
javac -cp "lib/*" -d target/classes src/main/java/**/*.java
java -cp "target/classes:lib/*" com.genscript.print.PrintApplication
```

### 验证安装
启动后访问 `http://localhost:8281/health` ,看到 `{"status":"ok"}` 即表示服务正常运行。

### LibreOffice配置(可选)

如需打印Word/Excel/PPT等Office格式,请安装LibreOffice:

**Windows:**
1. 访问 https://www.libreoffice.org/download/download/
2. 下载并安装
3. 重启PrintBridge服务

**Linux:**
```bash
# Ubuntu/Debian
sudo apt-get install libreoffice

# CentOS/RHEL
sudo yum install libreoffice
```

**macOS:**
```bash
brew install --cask libreoffice
```

启动时日志会显示:
```
✓ Office格式: Word, Excel, PowerPoint
  LibreOffice已检测到,多格式支持已启用
```

## 使用指南 📖

### API接口

#### 打印文档
**POST** `http://localhost:8281/print`

**请求参数:**
```json
[
  {
    "fileName": "文档名称",           // 文档名称
    "fileUrl": "http://example.com/file.xxx",  // 文件URL
    "landscape": false               // false=竖向, true=横向
  }
]
```

**支持的文件格式:**
- PDF: `http://example.com/document.pdf`
- Word: `http://example.com/report.docx`
- Excel: `http://example.com/table.xlsx`
- PowerPoint: `http://example.com/slides.pptx`
- 图片: `http://example.com/image.png`

**响应示例:**
```json
{
  "code": 200,
  "message": "打印任务已加入队列"
}
```

### 前端集成示例

#### 原生JavaScript
```javascript
function printDocument(fileUrl, fileName = 'document.pdf') {
    fetch('http://localhost:8281/print', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify([{
            fileName: fileName,
            fileUrl: fileUrl,
            landscape: false  // 横向打印设为true
        }])
    })
    .then(res => res.json())
    .then(data => console.log('✓ 打印任务已提交:', data))
    .catch(err => console.error('✗ 打印失败:', err));
}

// 打印PDF
printDocument('https://example.com/invoice.pdf', '发票.pdf');

// 打印Word文档
printDocument('https://example.com/report.docx', '报告.docx');

// 打印Excel表格
printDocument('https://example.com/data.xlsx', '数据.xlsx');

// 打印图片
printDocument('https://example.com/photo.png', '照片.png');
```

#### jQuery方式
```javascript
$.ajax({
    url: 'http://localhost:8281/print',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify([{
        fileName: '合同.pdf',
        fileUrl: 'https://example.com/contract.pdf',
        landscape: false
    }]),
    success: (res) => alert('打印任务已提交'),
    error: (err) => alert('打印失败: ' + err.statusText)
});
```

#### React/Vue 示例
```javascript
// React组件中
const handlePrint = async (pdfUrl) => {
    try {
        const response = await fetch('http://localhost:8281/print', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify([{
                fileName: '报表.pdf',
                fileUrl: pdfUrl,
                landscape: false
            }])
        });
        const result = await response.json();
        console.log('打印成功', result);
    } catch (error) {
        console.error('打印失败', error);
    }
}
```

## 配置说明 ⚙️

### 默认配置
| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| 监听端口 | 8281 | HTTP服务端口 |
| 工作线程 | 10-100 | 动态线程池 |
| 支持格式 | PDF | 当前版本支持 |
| 日志目录 | `logs/` | 日志文件位置 |
| 日志保留 | 30天 | 自动清理旧日志 |

### 自定义配置
修改 `src/main/resources/logback.xml` 可调整日志级别和输出策略。

## 常见问题 💡

<details>
<summary><b>Q: 服务启动失败怎么办?</b></summary>

**解决步骤:**
1. 检查8281端口是否被占用: `netstat -ano | findstr 8281`
2. 查看错误日志: `logs/print-server-error.log`
3. 确认Java版本 >= 8: `java -version`
</details>

<details>
<summary><b>Q: 打印任务没有反应?</b></summary>

**排查清单:**
- ✓ PDF文件URL是否可访问(浏览器测试)
- ✓ 打印机是否在线且驱动正常
- ✓ 查看界面打印队列状态
- ✓ 检查日志文件 `logs/print-server.log`
</details>

<details>
<summary><b>Q: 浏览器提示跨域错误?</b></summary>

**解决方案:**
- PrintBridge已内置CORS支持,无需额外配置
- 确认服务已启动: 访问 `http://localhost:8281/health`
- 如在HTTPS页面调用,需配置SSL证书或使用代理
</details>

<details>
<summary><b>Q: 支持哪些文件格式?</b></summary>

**完全支持:**
- ✅ PDF文档

**图片格式**(自动转换为PDF):
- ✅ PNG, JPG, JPEG, GIF, BMP

**Office格式**(需要安装LibreOffice):
- ✅ Word (.docx, .doc)
- ✅ Excel (.xlsx, .xls)
- ✅ PowerPoint (.pptx, .ppt)

如未安装LibreOffice,Office格式将无法打印。
</details>

<details>
<summary><b>Q: 如何启用Office格式支持?</b></summary>

**步骤:**
1. 下载并安装 [LibreOffice](https://www.libreoffice.org/download/download/)
2. 重启PrintBridge服务
3. 启动时会自动检测LibreOffice并启用Office格式支持

**验证**: 查看启动日志,会显示"LibreOffice已检测到,多格式支持已启用"
</details>

## 架构设计 🏗️

```
PrintBridge
├── controller/         # API接口层
├── service/           # 核心业务逻辑
│   ├── JettyPrintService     # HTTP服务
│   ├── PrintService          # 打印引擎
│   └── ServerStatusService   # 状态管理
├── ui/                # Swing界面
├── model/             # 数据模型
└── config/            # 配置中心
```

**技术栈:**
- 🚀 Web服务: Jetty 9.4
- 🖨️ 打印引擎: Apache PDFBox 2.0
- 🔄 格式转换: JODConverter 4.4 + LibreOffice
- 🎨 界面框架: Swing + FlatLaf
- 📝 日志系统: SLF4J + Logback
- 📦 依赖管理: Maven

## 版本历史 📋

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| **v2.1.0** | 2025-01 | 📄 多格式支持: Word/Excel/PPT/图片 + JODConverter集成 |
| **v2.0.0** | 2025-01 | 🚀 企业级重构: Jetty服务器 + SLF4J日志 + FlatLaf主题 |
| **v1.0.0** | 2020-08 | 🎉 初始版本: Socket HTTP服务器 + PDF打印 |

## 产品路线图 🗺️

### ✅ v2.1 (当前版本)
- [x] 📄 **多格式支持**: Word、Excel、PPT、图片等格式
- [x] 🔄 **JODConverter集成**: 基于LibreOffice的格式转换
- [x] 🖼️ **图片打印**: PNG、JPG、GIF、BMP自动转PDF

### 🎯 v2.2 (计划中)
- [ ] 🖨️ **打印预设**: 保存常用打印配置
- [ ] 📋 **打印历史**: 查看和重新打印历史任务
- [ ] 📊 **打印统计**: 任务数量、成功率等统计

### 🚀 v3.0 (规划中)
- [ ] 🎨 **水印功能**: 文字/图片水印添加
- [ ] ⚙️ **高级设置**: 双面打印、页面范围、份数控制
- [ ] 🖥️ **网络共享**: 局域网内多台电脑共享使用

### 🌟 长期愿景
- [ ] 🔍 **打印预览**: 打印前本地预览
- [ ] ☁️ **云存储集成**: 支持OSS/S3等云存储直接打印
- [ ] 🔐 **安全加固**: IP白名单、API Token认证

> 💡 有好的想法?欢迎提Issue或加入讨论!

## 参与贡献 🤝

欢迎各种形式的贡献!无论是新功能、Bug修复还是文档改进。

### 贡献流程
1. 🍴 Fork本项目
2. 🌿 创建特性分支 `git checkout -b feature/AmazingFeature`
3. 💾 提交更改 `git commit -m 'Add some AmazingFeature'`
4. 📤 推送到分支 `git push origin feature/AmazingFeature`
5. 🎉 提交Pull Request

### 开发规范
- 遵循现有代码风格
- 添加必要的单元测试
- 更新相关文档
- 提交前运行 `mvn clean test`

## 开源协议 📄

本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议发布。

## 社区与支持 💬

- 🐛 [报告Bug](https://github.com/magicyuan876/PrintBridge/issues)
- 💡 [功能建议](https://github.com/magicyuan876/PrintBridge/issues)
- 💬 [加入讨论](https://github.com/magicyuan876/PrintBridge/discussions)

## 致谢 🙏

感谢所有使用和贡献PrintBridge的开发者!

**核心依赖:**
- [Apache PDFBox](https://pdfbox.apache.org/) - PDF处理引擎
- [JODConverter](https://github.com/jodconverter/jodconverter) - 文档格式转换
- [LibreOffice](https://www.libreoffice.org/) - Office文档渲染
- [Eclipse Jetty](https://www.eclipse.org/jetty/) - HTTP服务器
- [FlatLaf](https://www.formdev.com/flatlaf/) - 现代UI主题

---

<div align="center">

**⭐ 如果PrintBridge对您有帮助,请给个Star支持! ⭐**

Made with ❤️ by developers, for developers

[GitHub](https://github.com/magicyuan876/PrintBridge) · [Gitee](https://gitee.com/magic_yuan_admin/PrintBridge)

</div>
