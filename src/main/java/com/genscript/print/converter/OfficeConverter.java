package com.genscript.print.converter;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Office文档转换器
 * 基于JODConverter和LibreOffice实现Word、Excel、PPT等格式转PDF
 * 
 * @author Magic_yuan
 * @version 2.1.0
 */
public class OfficeConverter {

    private static final Logger logger = LoggerFactory.getLogger(OfficeConverter.class);

    private OfficeManager officeManager;
    private DocumentConverter converter;
    private boolean isAvailable = false;

    /**
     * 构造函数,自动检测LibreOffice并初始化
     */
    public OfficeConverter() {
        try {
            String libreOfficePath = detectLibreOfficePath();
            
            if (libreOfficePath != null) {
                initializeConverter(libreOfficePath);
            } else {
                logger.warn("未检测到LibreOffice安装,Office格式转换功能不可用");
                logger.warn("如需支持Word/Excel/PPT格式,请安装LibreOffice: https://www.libreoffice.org/");
            }
        } catch (Exception e) {
            logger.error("初始化Office转换器失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 初始化转换器
     */
    private void initializeConverter(String libreOfficePath) throws OfficeException {
        logger.info("初始化Office转换器,LibreOffice路径: {}", libreOfficePath);

        // 创建OfficeManager
        // JODConverter 4.4.6 简化配置
        officeManager = LocalOfficeManager.builder()
            .officeHome(libreOfficePath)
            .taskExecutionTimeout(120_000L)  // 任务超时2分钟
            .maxTasksPerProcess(50)  // 每个实例处理50个任务后重启,避免内存泄漏
            .build();

        // 启动OfficeManager
        officeManager.start();

        // 创建转换器
        converter = LocalConverter.make(officeManager);

        isAvailable = true;
        logger.info("Office转换器初始化成功");
    }

    /**
     * 检测LibreOffice安装路径
     */
    private String detectLibreOfficePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] paths;

        if (os.contains("win")) {
            // Windows路径
            paths = new String[] {
                "C:\\Program Files\\LibreOffice",
                "C:\\Program Files (x86)\\LibreOffice",
                System.getenv("ProgramFiles") + "\\LibreOffice",
                System.getenv("ProgramFiles(x86)") + "\\LibreOffice"
            };
        } else if (os.contains("mac")) {
            // macOS路径
            paths = new String[] {
                "/Applications/LibreOffice.app/Contents"
            };
        } else {
            // Linux路径
            paths = new String[] {
                "/usr/lib/libreoffice",
                "/usr/lib64/libreoffice",
                "/opt/libreoffice",
                "/snap/libreoffice/current"
            };
        }

        for (String path : paths) {
            if (new File(path).exists()) {
                logger.info("检测到LibreOffice: {}", path);
                return path;
            }
        }

        return null;
    }

    /**
     * 从URL下载并转换Office文档为PDF
     * 
     * @param fileUrl 文档URL
     * @return 转换后的PDF文件
     */
    public File convertFromUrl(String fileUrl) throws Exception {
        if (!isAvailable) {
            throw new IllegalStateException("Office转换器不可用,请安装LibreOffice");
        }

        logger.info("开始转换Office文档: {}", fileUrl);

        // 下载文件到临时目录
        Path tempInput = Files.createTempFile("printbridge_input_", getExtensionFromUrl(fileUrl));
        
        try {
            // 下载文件
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            
            logger.info("文件下载完成: {}", tempInput.getFileName());

            // 创建临时PDF输出文件
            Path tempOutput = Files.createTempFile("printbridge_output_", ".pdf");

            // 执行转换
            logger.info("正在转换为PDF...");
            converter.convert(tempInput.toFile())
                    .to(tempOutput.toFile())
                    .execute();

            logger.info("转换完成: {}", tempOutput.getFileName());
            
            return tempOutput.toFile();
            
        } finally {
            // 清理输入临时文件
            try {
                Files.deleteIfExists(tempInput);
            } catch (Exception e) {
                logger.warn("清理临时文件失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 转换本地文件
     */
    public File convert(File inputFile) throws Exception {
        if (!isAvailable) {
            throw new IllegalStateException("Office转换器不可用,请安装LibreOffice");
        }

        Path outputPath = Files.createTempFile("printbridge_output_", ".pdf");
        File outputFile = outputPath.toFile();

        logger.info("转换文档: {} -> {}", inputFile.getName(), outputFile.getName());

        converter.convert(inputFile).to(outputFile).execute();

        logger.info("转换完成: {}", outputFile.getAbsolutePath());
        return outputFile;
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * 检查是否支持该格式
     */
    public boolean isSupportedFormat(String fileExtension) {
        if (!isAvailable) {
            return false;
        }

        String ext = fileExtension.toLowerCase().replace(".", "");
        
        // 支持的Office格式
        return ext.equals("doc") || ext.equals("docx") ||
               ext.equals("xls") || ext.equals("xlsx") ||
               ext.equals("ppt") || ext.equals("pptx") ||
               ext.equals("odt") || ext.equals("ods") || ext.equals("odp");
    }

    /**
     * 关闭转换器
     */
    public void shutdown() {
        if (officeManager != null && officeManager.isRunning()) {
            try {
                logger.info("关闭Office转换器...");
                officeManager.stop();
                logger.info("Office转换器已关闭");
            } catch (OfficeException e) {
                logger.error("关闭Office转换器失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 从URL获取文件扩展名
     */
    private String getExtensionFromUrl(String url) {
        String path = url.substring(url.lastIndexOf('/') + 1);
        
        // 移除查询参数
        int queryIndex = path.indexOf('?');
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }
        
        int lastDot = path.lastIndexOf('.');
        return (lastDot > 0) ? path.substring(lastDot) : ".tmp";
    }
}

