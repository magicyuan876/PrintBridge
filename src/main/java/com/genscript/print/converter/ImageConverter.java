package com.genscript.print.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片转换器
 * 将图片格式(PNG/JPG/GIF/BMP)转换为PDF
 * 
 * @author Magic_yuan
 * @version 2.1.0
 */
public class ImageConverter {

    private static final Logger logger = LoggerFactory.getLogger(ImageConverter.class);

    /**
     * 从URL下载并转换图片为PDF
     * 
     * @param imageUrl 图片URL
     * @return 转换后的PDF文件
     */
    public File convertFromUrl(String imageUrl) throws Exception {
        logger.info("开始转换图片: {}", imageUrl);

        // 下载图片到临时文件
        Path tempInput = Files.createTempFile("printbridge_img_", getExtensionFromUrl(imageUrl));
        
        try {
            // 下载图片
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            
            try (InputStream in = connection.getInputStream()) {
                BufferedImage image = ImageIO.read(in);
                if (image == null) {
                    throw new IllegalArgumentException("无法读取图片,可能格式不支持");
                }
                
                // 保存到临时文件
                String format = getImageFormat(imageUrl);
                ImageIO.write(image, format, tempInput.toFile());
            }
            
            logger.info("图片下载完成,开始转换为PDF...");

            // 转换为PDF
            File pdfFile = imageToPdf(tempInput.toFile());
            
            logger.info("图片转换完成");
            
            return pdfFile;
            
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
     * 将图片转换为PDF
     */
    private File imageToPdf(File imageFile) throws Exception {
        // 读取图片
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IllegalArgumentException("无法读取图片文件");
        }

        // 创建PDF文档
        PDDocument document = new PDDocument();
        
        try {
            // 根据图片尺寸确定页面大小
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();
            
            // 如果图片太大,按A4纸比例缩放
            PDRectangle pageSize;
            float maxWidth = PDRectangle.A4.getWidth();
            float maxHeight = PDRectangle.A4.getHeight();
            
            if (imageWidth > imageHeight) {
                // 横向图片
                pageSize = new PDRectangle(
                    Math.min(imageWidth, maxHeight),
                    Math.min(imageHeight, maxWidth)
                );
            } else {
                // 纵向图片
                pageSize = new PDRectangle(
                    Math.min(imageWidth, maxWidth),
                    Math.min(imageHeight, maxHeight)
                );
            }
            
            // 创建页面
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            // 将图片添加到PDF
            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, document);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // 计算图片在页面上的位置和大小(保持宽高比,居中)
                float scale = Math.min(
                    pageSize.getWidth() / imageWidth,
                    pageSize.getHeight() / imageHeight
                );
                
                float scaledWidth = imageWidth * scale;
                float scaledHeight = imageHeight * scale;
                
                float x = (pageSize.getWidth() - scaledWidth) / 2;
                float y = (pageSize.getHeight() - scaledHeight) / 2;
                
                contentStream.drawImage(pdImage, x, y, scaledWidth, scaledHeight);
            }

            // 保存为临时PDF文件
            Path tempOutput = Files.createTempFile("printbridge_pdf_", ".pdf");
            File pdfFile = tempOutput.toFile();
            document.save(pdfFile);
            
            return pdfFile;
            
        } finally {
            document.close();
        }
    }

    /**
     * 检查是否支持该图片格式
     */
    public boolean isSupportedFormat(String fileExtension) {
        String ext = fileExtension.toLowerCase().replace(".", "");
        return ext.equals("jpg") || ext.equals("jpeg") ||
               ext.equals("png") || ext.equals("gif") ||
               ext.equals("bmp") || ext.equals("tif") || ext.equals("tiff");
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
        return (lastDot > 0) ? path.substring(lastDot) : ".jpg";
    }

    /**
     * 获取图片格式
     */
    private String getImageFormat(String url) {
        String ext = getExtensionFromUrl(url).toLowerCase().replace(".", "");
        
        // ImageIO支持的格式
        if (ext.equals("jpg") || ext.equals("jpeg")) {
            return "jpg";
        } else if (ext.equals("png")) {
            return "png";
        } else if (ext.equals("gif")) {
            return "gif";
        } else if (ext.equals("bmp")) {
            return "bmp";
        } else {
            return "jpg";  // 默认
        }
    }
}

