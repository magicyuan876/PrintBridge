package com.genscript.print.service;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.genscript.print.dto.PrintDTO;
import com.genscript.print.model.PrintQueueModel;

/**
 * 打印服务类
 * 处理所有打印相关的业务逻辑
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class PrintService {

    private static final Logger logger = LoggerFactory.getLogger(PrintService.class);

    private final PrintQueueModel printQueueModel;

    private final ExecutorService executorService;

    public PrintService(PrintQueueModel printQueueModel) {
        this.printQueueModel = printQueueModel;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * 异步打印文档（带对话框）
     */
    public CompletableFuture<Void> printWithDialogAsync(List<PrintDTO> printDTOList) {
        return CompletableFuture.runAsync(() -> {
            printWithDialog(printDTOList);
        }, executorService);
    }

    /**
     * 异步打印文档（不带对话框）
     */
    public CompletableFuture<Void> printWithoutDialogAsync(List<PrintDTO> printDTOList) {
        return CompletableFuture.runAsync(() -> {
            printWithoutDialog(printDTOList);
        }, executorService);
    }

    /**
     * 打印文档（带对话框）
     */
    public void printWithDialog(List<PrintDTO> printDTOList) {
        for (PrintDTO printDTO : printDTOList) {
            try {
                if (printDocument(printDTO, true)) {
                    // 打印成功，可以添加到历史记录
                    printQueueModel.addPrintTask(printDTO);
                }
            } catch (Exception e) {
                handlePrintError(printDTO, e);
            }
        }
    }

    /**
     * 打印文档（不带对话框）
     */
    public void printWithoutDialog(List<PrintDTO> printDTOList) {
        for (PrintDTO printDTO : printDTOList) {
            try {
                if (printDocument(printDTO, false)) {
                    // 打印成功，添加到队列
                    printQueueModel.addPrintTask(printDTO);
                }
            } catch (Exception e) {
                handlePrintError(printDTO, e);
            }
        }
    }

    /**
     * 打印单个文档
     */
    private boolean printDocument(PrintDTO printDTO, boolean showDialog) throws Exception {
        logger.info("正在打印: {}, 文件名: {}", printDTO.getFileUrl(), printDTO.getFileName());

        // 从URL加载PDF文档
        URL url = new URL(printDTO.getFileUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        PDDocument document = PDDocument.load(connection.getInputStream());

        try {
            PrinterJob job = PrinterJob.getPrinterJob();

            // 设置页面格式
            PageFormat pageFormat = new PageFormat();
            if (printDTO.isLandscape()) {
                pageFormat.setOrientation(PageFormat.LANDSCAPE);
            }

            // 设置作业名称
            if (StringUtils.isNotBlank(printDTO.getFileName())) {
                job.setJobName(printDTO.getFileName());
            }

            // 创建打印书籍
            Book book = new Book();
            book.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT), pageFormat, document.getNumberOfPages());
            job.setPageable(book);

            // 根据参数决定是否显示打印对话框
            if (showDialog) {
                boolean shouldPrint = job.printDialog();
                if (shouldPrint) {
                    job.print();
                    return true;
                }
                return false;
            } else {
                job.print();
                return true;
            }

        } finally {
            document.close();
        }
    }

    /**
     * 处理打印错误
     */
    private void handlePrintError(PrintDTO printDTO, Exception e) {
        logger.error("打印失败: {} - {}", printDTO.getFileName(), e.getMessage(), e);
        printQueueModel.addErrorTask(printDTO);
    }

    /**
     * 验证打印任务
     */
    public boolean validatePrintTasks(List<PrintDTO> printDTOList) {
        if (printDTOList == null || printDTOList.isEmpty()) {
            return false;
        }

        for (PrintDTO printDTO : printDTOList) {
            if (StringUtils.isBlank(printDTO.getFileUrl())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取可用的打印机列表
     */
    public String[] getAvailablePrinters() {
        javax.print.PrintService[] printServices = javax.print.PrintServiceLookup.lookupPrintServices(null, null);

        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }

        return printerNames;
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}