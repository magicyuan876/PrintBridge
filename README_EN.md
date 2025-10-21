# PrintBridge 🖨️

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![Jetty](https://img.shields.io/badge/Jetty-9.4-green.svg)](https://www.eclipse.org/jetty/)

[![GitHub stars](https://img.shields.io/github/stars/magicyuan876/PrintBridge?style=social)](https://github.com/magicyuan876/PrintBridge/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/magicyuan876/PrintBridge?style=social)](https://github.com/magicyuan876/PrintBridge/network/members)

> **The Smart Bridge Between Web and Printers** - An enterprise-grade web printing middleware that enables browser applications to seamlessly invoke local print services.

Built on Java Swing + Jetty, providing high-performance, cross-browser local printing solutions for web applications.

[简体中文](./README.md) | English

---

## ⭐ Star Us!

**If you find PrintBridge helpful, please give it a star!** ⭐

Your support motivates us to keep improving PrintBridge and deliver more features. A star helps other developers discover this project and benefits the entire community!

**Quick Star:** Click the ⭐ button at the top right of this page → Takes only 2 seconds!

---

## Why PrintBridge? 💭

PrintBridge was born from real-world business pain points: when web applications need to invoke local printers, traditional solutions are often complex and unstable. PrintBridge provides a lightweight, reliable solution.

### Project Evolution
- **2020**: v1.0 Socket-based prototype system
- **2025**: v2.0 Enterprise-grade refactoring
  - 🔄 Upgraded from raw Socket implementation to enterprise-grade Jetty server
  - 📊 Integrated SLF4J + Logback logging system
  - 🎨 Modernized UI with FlatLaf theme
  - 🧹 Follows modern Java development standards

## Core Features 🚀

- 🌐 **Plug & Play**: One-line API call, print from any browser
- 📄 **Multi-Format Support**: PDF, Word, Excel, PPT, images and more
- ⚡ **High-Performance Architecture**: Jetty server + multi-threaded queue, handles high concurrency with ease
- 🔄 **Smart Conversion**: Based on JODConverter + LibreOffice, perfect format preservation
- 🎯 **Smart Queue**: Visual task management, batch printing without chaos
- 🎨 **Minimalist UI**: Modern FlatLaf theme, intuitive and effortless
- 📝 **Production-Grade Logging**: SLF4J + Logback, troubleshooting with evidence
- 🔒 **Secure & Controlled**: Local service, data stays on your machine

## Quick Start ⚡

### Prerequisites
- ☕ Java 8+ runtime environment
- 🖨️ At least one available printer
- 📦 LibreOffice (optional, for Office format support)

### Launch Service

#### Method 1: Maven Quick Start (Recommended)
```bash
# Clone from GitHub
git clone https://github.com/magicyuan876/PrintBridge.git

# Enter directory
cd PrintBridge

# Start service
mvn clean compile exec:java -Dexec.mainClass="com.magicyuan.print.PrintApplication"
```

#### Method 2: Direct Run
```bash
javac -cp "lib/*" -d target/classes src/main/java/**/*.java
java -cp "target/classes:lib/*" com.magicyuan.print.PrintApplication
```

### Verify Installation
After startup, visit `http://localhost:8281/health`. Seeing `{"status":"ok"}` indicates the service is running normally.

### LibreOffice Configuration (Optional)

To print Word/Excel/PPT and other Office formats, install LibreOffice:

**Windows:**
1. Visit https://www.libreoffice.org/download/download/
2. Download and install
3. Restart PrintBridge service

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

On startup, the log will display:
```
✓ Office formats: Word, Excel, PowerPoint
  LibreOffice detected, multi-format support enabled
```

## Usage Guide 📖

### API Interface

#### Print Document
**POST** `http://localhost:8281/print`

**Request Parameters:**
```json
[
  {
    "fileName": "document name",           // Document name
    "fileUrl": "http://example.com/file.xxx",  // File URL
    "landscape": false               // false=portrait, true=landscape
  }
]
```

**Supported File Formats:**
- PDF: `http://example.com/document.pdf`
- Word: `http://example.com/report.docx`
- Excel: `http://example.com/table.xlsx`
- PowerPoint: `http://example.com/slides.pptx`
- Images: `http://example.com/image.png`

**Response Example:**
```json
{
  "code": 200,
  "message": "Print task added to queue"
}
```

### Frontend Integration Examples

#### Vanilla JavaScript
```javascript
function printDocument(fileUrl, fileName = 'document.pdf') {
    fetch('http://localhost:8281/print', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify([{
            fileName: fileName,
            fileUrl: fileUrl,
            landscape: false  // Set to true for landscape
        }])
    })
    .then(res => res.json())
    .then(data => console.log('✓ Print task submitted:', data))
    .catch(err => console.error('✗ Print failed:', err));
}

// Print PDF
printDocument('https://example.com/invoice.pdf', 'invoice.pdf');

// Print Word document
printDocument('https://example.com/report.docx', 'report.docx');

// Print Excel spreadsheet
printDocument('https://example.com/data.xlsx', 'data.xlsx');

// Print image
printDocument('https://example.com/photo.png', 'photo.png');
```

#### jQuery Method
```javascript
$.ajax({
    url: 'http://localhost:8281/print',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify([{
        fileName: 'contract.pdf',
        fileUrl: 'https://example.com/contract.pdf',
        landscape: false
    }]),
    success: (res) => alert('Print task submitted'),
    error: (err) => alert('Print failed: ' + err.statusText)
});
```

#### React/Vue Example
```javascript
// In React component
const handlePrint = async (pdfUrl) => {
    try {
        const response = await fetch('http://localhost:8281/print', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify([{
                fileName: 'report.pdf',
                fileUrl: pdfUrl,
                landscape: false
            }])
        });
        const result = await response.json();
        console.log('Print success', result);
    } catch (error) {
        console.error('Print failed', error);
    }
}
```

## Configuration ⚙️

### Default Configuration
| Config Item | Default Value | Description |
|------------|---------------|-------------|
| Listen Port | 8281 | HTTP service port |
| Worker Threads | 10-100 | Dynamic thread pool |
| Supported Formats | PDF | Current version support |
| Log Directory | `logs/` | Log file location |
| Log Retention | 30 days | Auto-cleanup old logs |

### Custom Configuration
Modify `src/main/resources/logback.xml` to adjust log levels and output strategies.

## FAQ 💡

<details>
<summary><b>Q: Service fails to start?</b></summary>

**Solution Steps:**
1. Check if port 8281 is occupied: `netstat -ano | findstr 8281`
2. Check error logs: `logs/print-server-error.log`
3. Confirm Java version >= 8: `java -version`
</details>

<details>
<summary><b>Q: Print task has no response?</b></summary>

**Troubleshooting Checklist:**
- ✓ Is the PDF file URL accessible (test in browser)
- ✓ Is the printer online with proper drivers
- ✓ Check print queue status in UI
- ✓ Check log file `logs/print-server.log`
</details>

<details>
<summary><b>Q: Browser shows CORS error?</b></summary>

**Solution:**
- PrintBridge has built-in CORS support, no additional configuration needed
- Confirm service is started: visit `http://localhost:8281/health`
- If calling from HTTPS pages, need to configure SSL certificate or use proxy
</details>

<details>
<summary><b>Q: Which file formats are supported?</b></summary>

**Full Support:**
- ✅ PDF documents

**Image Formats** (auto-convert to PDF):
- ✅ PNG, JPG, JPEG, GIF, BMP

**Office Formats** (requires LibreOffice installation):
- ✅ Word (.docx, .doc)
- ✅ Excel (.xlsx, .xls)
- ✅ PowerPoint (.pptx, .ppt)

Without LibreOffice, Office formats cannot be printed.
</details>

<details>
<summary><b>Q: How to enable Office format support?</b></summary>

**Steps:**
1. Download and install [LibreOffice](https://www.libreoffice.org/download/download/)
2. Restart PrintBridge service
3. Service will auto-detect LibreOffice and enable Office format support

**Verification**: Check startup logs, will display "LibreOffice detected, multi-format support enabled"
</details>

## Architecture Design 🏗️

```
PrintBridge
├── controller/         # API layer
├── service/           # Core business logic
│   ├── JettyPrintService     # HTTP service
│   ├── PrintService          # Print engine
│   └── ServerStatusService   # Status management
├── ui/                # Swing UI
├── model/             # Data models
└── config/            # Configuration center
```

**Tech Stack:**
- 🚀 Web Service: Jetty 9.4
- 🖨️ Print Engine: Apache PDFBox 2.0
- 🔄 Format Conversion: JODConverter 4.4 + LibreOffice
- 🎨 UI Framework: Swing + FlatLaf
- 📝 Logging System: SLF4J + Logback
- 📦 Dependency Management: Maven

## Version History 📋

| Version | Date | Updates |
|---------|------|---------|
| **v2.1.0** | 2025-10 | 📄 Multi-format support: Word/Excel/PPT/Images + JODConverter integration |
| **v2.0.0** | 2025-01 | 🚀 Enterprise refactoring: Jetty server + SLF4J logging + FlatLaf theme |
| **v1.0.0** | 2020-08 | 🎉 Initial release: Socket HTTP server + PDF printing |

## Roadmap 🗺️

### ✅ v2.1 (Current Version)
- [x] 📄 **Multi-Format Support**: Word, Excel, PPT, images and more
- [x] 🔄 **JODConverter Integration**: LibreOffice-based format conversion
- [x] 🖼️ **Image Printing**: PNG, JPG, GIF, BMP auto-convert to PDF

### 🎯 v2.2 (Planned)
- [ ] 🖨️ **Print Presets**: Save commonly used print configurations
- [ ] 📋 **Print History**: View and reprint historical tasks
- [ ] 📊 **Print Statistics**: Task counts, success rates and more

### 🚀 v3.0 (Future)
- [ ] 🎨 **Watermark Feature**: Add text/image watermarks
- [ ] ⚙️ **Advanced Settings**: Duplex printing, page range, copy control
- [ ] 🖥️ **Network Sharing**: Share across multiple computers in LAN

### 🌟 Long-term Vision
- [ ] 🔍 **Print Preview**: Local preview before printing
- [ ] ☁️ **Cloud Storage Integration**: Direct printing from OSS/S3
- [ ] 🔐 **Security Hardening**: IP whitelist, API token authentication

> 💡 Have great ideas? Welcome to submit Issues or join discussions!

## Contributing 🤝

All forms of contribution are welcome! Whether new features, bug fixes, or documentation improvements.

### Contribution Process
1. 🍴 Fork this project
2. 🌿 Create feature branch `git checkout -b feature/AmazingFeature`
3. 💾 Commit changes `git commit -m 'Add some AmazingFeature'`
4. 📤 Push to branch `git push origin feature/AmazingFeature`
5. 🎉 Submit Pull Request

### Development Standards
- Follow existing code style
- Add necessary unit tests
- Update relevant documentation
- Run `mvn clean test` before committing

## License 📄

This project is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## Community & Support 💬

- 🐛 [Report Bugs](https://github.com/magicyuan876/PrintBridge/issues)
- 💡 [Feature Requests](https://github.com/magicyuan876/PrintBridge/issues)
- 💬 [Join Discussions](https://github.com/magicyuan876/PrintBridge/discussions)

## Acknowledgments 🙏

Thanks to all developers who use and contribute to PrintBridge!

**Core Dependencies:**
- [Apache PDFBox](https://pdfbox.apache.org/) - PDF processing engine
- [JODConverter](https://github.com/jodconverter/jodconverter) - Document format conversion
- [LibreOffice](https://www.libreoffice.org/) - Office document rendering
- [Eclipse Jetty](https://www.eclipse.org/jetty/) - HTTP server
- [FlatLaf](https://www.formdev.com/flatlaf/) - Modern UI theme

---

<div align="center">

**⭐ If PrintBridge helps you, please give it a Star! ⭐**

Your star is our greatest motivation! It takes only 2 seconds but means the world to us!

Made with ❤️ by developers, for developers

[⭐ Star on GitHub](https://github.com/magicyuan876/PrintBridge) · [Report Issues](https://github.com/magicyuan876/PrintBridge/issues) · [Discussions](https://github.com/magicyuan876/PrintBridge/discussions)

</div>

