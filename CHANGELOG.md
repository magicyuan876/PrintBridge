# PrintBridge 版本更新日志

本文档记录 PrintBridge 的所有版本更新历史。

---

## [v2.1.0] - 2025-10-21

### 🎉 重大更新

#### 📄 多格式打印支持
PrintBridge v2.1 现已支持多种文档格式打印,不再局限于PDF!

**✅ 开箱即用**
- PDF文档 - 完全支持
- 图片格式 - PNG, JPG, GIF, BMP (自动转PDF打印)

**📦 可选支持 (需安装 LibreOffice)**
- Word文档 - .docx, .doc
- Excel表格 - .xlsx, .xls
- PowerPoint - .pptx, .ppt

#### 🌍 国际化支持
- 自动检测系统语言,支持中文和英文界面
- 可手动切换语言
- 所有UI文字和提示信息均已国际化

#### 🎨 用户体验优化

**1. 智能引导对话框**
- 首次启动时检测LibreOffice安装状态
- 美观的彩色卡片设计,清晰展示支持的格式
- 一键跳转LibreOffice下载页面

**2. 优化的窗口关闭体验**
关闭窗口时提供三个选项:
- **最小化到后台** - 隐藏窗口,继续监听浏览器打印请求
- **完全退出** - 停止服务并关闭程序
- **取消** - 保持窗口打开

**3. 系统托盘支持**
- 最小化后在系统托盘显示图标
- 双击托盘图标恢复窗口
- 右键菜单:打开窗口、关于、退出
- 友好的后台运行通知

**4. UI界面完善**
- 修复"关于"对话框标签显示不全的问题
- 优化对话框布局,确保文字完整显示
- 统一应用名称为"PrintBridge"

### 🔧 技术实现

#### 新增核心组件
- **OfficeConverter** - Office文档转PDF (基于JODConverter + LibreOffice)
- **ImageConverter** - 图片转PDF (基于Apache PDFBox)
- **SystemTrayManager** - 系统托盘管理
- **I18nManager** - 国际化管理

#### 增强的PrintService
- 自动格式检测 (根据文件扩展名)
- 统一的格式转换流程
- 智能的LibreOffice可用性检查

#### 新增依赖
```xml
<dependency>
    <groupId>org.jodconverter</groupId>
    <artifactId>jodconverter-local</artifactId>
    <version>4.4.6</version>
</dependency>
```

### 📊 打印工作流程

```
浏览器请求 → PrintBridge HTTP服务
    ↓
文件下载 & 格式检测
    ↓
    ├─ PDF ──────────────→ 直接打印
    ├─ 图片 ──→ 转PDF ──→ 打印
    └─ Office → 转PDF ──→ 打印
                  ↑
            LibreOffice
```

### 🚀 API使用示例

```javascript
// PDF打印 (无需转换)
fetch('http://localhost:8281/print', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify([{
        fileName: 'document.pdf',
        fileUrl: 'https://example.com/document.pdf'
    }])
});

// 图片打印 (自动转PDF, 开箱即用)
fetch('http://localhost:8281/print', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify([{
        fileName: 'photo.png',
        fileUrl: 'https://example.com/photo.png'
    }])
});

// Office文档打印 (需LibreOffice)
fetch('http://localhost:8281/print', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify([{
        fileName: 'report.docx',
        fileUrl: 'https://example.com/report.docx'
    }])
});
```

### ⚙️ LibreOffice配置指南

#### Windows
1. 访问: https://www.libreoffice.org/download/download/
2. 下载并安装到默认路径 (PrintBridge会自动检测)
3. 重启PrintBridge应用

#### Linux
```bash
# Ubuntu/Debian
sudo apt-get install libreoffice

# CentOS/RHEL
sudo yum install libreoffice

# Fedora
sudo dnf install libreoffice
```

#### macOS
```bash
brew install --cask libreoffice
```

### 📈 性能参考

| 格式类型 | 典型文件大小 | 转换时间 | 说明 |
|---------|-------------|---------|------|
| PDF | 任意 | 0秒 | 直接打印,无需转换 |
| 图片 | <5MB | <1秒 | 快速转换 |
| Word | <10页 | 1-3秒 | 首次启动LibreOffice较慢(3-5秒) |
| Excel | <100行 | 1-2秒 | 取决于表格复杂度 |
| PowerPoint | <20页 | 2-5秒 | 取决于幻灯片数量和内容 |

### 🐛 已知问题与限制

1. **首次转换慢** - LibreOffice首次启动需要3-5秒,后续转换会更快
2. **复杂格式** - 含特殊字体或复杂排版的文档转换后可能略有差异
3. **大文件转换** - 超大文件(>50MB)转换时间较长,建议预先转PDF
4. **Windows通知标题** - 使用`javaw.exe`运行时通知标题显示为"Java(TM) Platform SE binary",需打包成原生应用才能自定义

### 💡 最佳实践建议

1. **格式选择**: 重要文档建议在服务端预先转换为PDF,确保格式一致
2. **生产环境**: 建议在服务器上安装LibreOffice以支持完整的Office格式
3. **错误处理**: 前端应实现重试机制,优雅处理格式转换失败的情况
4. **文件大小**: 建议限制单个文件大小(如20MB以内)以保证转换速度

### 🎯 版本功能对比

| 功能特性 | v2.0.0 | v2.1.0 |
|---------|--------|--------|
| PDF打印 | ✅ | ✅ |
| 图片打印 | ❌ | ✅ |
| Office文档打印 | ❌ | ✅ (可选) |
| 格式自动检测 | ❌ | ✅ |
| 引导界面 | ❌ | ✅ |
| 系统托盘 | ❌ | ✅ |
| 后台运行 | ❌ | ✅ |
| 国际化 | ❌ | ✅ |

### 🙏 致谢

感谢以下开源项目的支持:
- [JODConverter](https://github.com/jodconverter/jodconverter) - 文档格式转换核心
- [LibreOffice](https://www.libreoffice.org/) - Office文档渲染引擎
- [Apache PDFBox](https://pdfbox.apache.org/) - PDF处理库
- [Eclipse Jetty](https://www.eclipse.org/jetty/) - HTTP服务器
- [FlatLaf](https://www.formdev.com/flatlaf/) - 现代化Swing界面

---

## [v2.0.0] - 2024-12-15

### 🎉 首次发布

#### 核心功能
- ✅ PDF文档打印支持
- ✅ 基于Jetty的HTTP服务 (端口8281)
- ✅ RESTful API接口
- ✅ 健康检查接口
- ✅ 现代化Swing UI界面
- ✅ 打印队列管理

#### 技术栈
- Java 8+
- Apache PDFBox 2.0.27
- Eclipse Jetty 9.4.44
- FlatLaf UI

#### API端点
- `POST /print` - 打印服务
- `GET /health` - 健康检查

### 📝 初始功能说明

**支持的打印参数**:
```javascript
{
    fileName: "文件名",
    fileUrl: "文件URL地址"
}
```

**基础架构**:
- 单机版本运行
- 本地打印机直连
- HTTP + CORS支持跨域

---

## 📋 版本兼容性

| 版本 | 最低Java版本 | 操作系统 |
|------|------------|----------|
| v2.1.0 | Java 8+ | Windows 7+, macOS 10.12+, Linux (任意发行版) |
| v2.0.0 | Java 8+ | Windows 7+, macOS 10.12+, Linux (任意发行版) |

---

## 🔮 未来计划

### v2.2 (计划中)
- [ ] 打印预设配置 (保存常用打印设置)
- [ ] 打印历史记录 (查看和重新打印)
- [ ] 打印任务统计 (成功率、数量统计)
- [ ] 批量打印优化

### v3.0 (规划中)
- [ ] 水印功能 (文字/图片水印)
- [ ] 高级打印设置 (双面打印、页面范围、份数控制)
- [ ] 网络共享模式 (局域网内多台电脑共享)
- [ ] 打印预览功能

### 长期愿景
- [ ] 云存储集成 (OSS/S3直接打印)
- [ ] 安全加固 (IP白名单、API Token认证)
- [ ] 打印机管理面板
- [ ] 插件系统

