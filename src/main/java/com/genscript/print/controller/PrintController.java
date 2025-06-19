package com.genscript.print.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.genscript.print.dto.PrintDTO;
import com.genscript.print.model.PrintQueueModel;
import com.genscript.print.service.PrintService;
import com.genscript.print.service.ServerStatusService;

/**
 * 打印控制器类
 * 处理打印相关的事件和业务逻辑协调
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class PrintController {

    private static final Logger logger = LoggerFactory.getLogger(PrintController.class);

    private final PrintQueueModel printQueueModel;

    private final PrintService printService;

    private final ServerStatusService serverStatusService;

    private final JFrame parentFrame;

    private final JList<PrintDTO> fileList;

    public PrintController(PrintQueueModel printQueueModel, PrintService printService, ServerStatusService serverStatusService, JFrame parentFrame, JList<PrintDTO> fileList) {
        this.printQueueModel = printQueueModel;
        this.printService = printService;
        this.serverStatusService = serverStatusService;
        this.parentFrame = parentFrame;
        this.fileList = fileList;
    }

    /**
     * 创建打印按钮事件监听器
     */
    public ActionListener createPrintButtonListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handlePrintButtonClick();
            }
        };
    }

    /**
     * 处理打印按钮点击事件
     */
    private void handlePrintButtonClick() {
        logger.info("用户点击了打印按钮");

        int[] selectedIndices = fileList.getSelectedIndices();

        if (printQueueModel.isEmpty()) {
            showInfoDialog("没有可以打印的文件", "提示");
            return;
        }

        if (selectedIndices == null || selectedIndices.length == 0) {
            // 没有选中任何文件，询问是否打印所有文件
            if (showConfirmDialog("是否打印当前列表中的所有文件?", "提示")) {
                List<PrintDTO> allTasks = printQueueModel.getAllPrintTasks();
                printService.printWithDialogAsync(allTasks);
            }
        } else {
            // 打印选中的文件
            List<PrintDTO> selectedTasks = printQueueModel.getSelectedPrintTasks(selectedIndices);
            printService.printWithDialogAsync(selectedTasks);
        }
    }

    /**
     * 创建窗口事件监听器
     */
    public WindowAdapter createWindowListener() {
        return new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                logger.info("窗口已打开，正在启动服务器...");
                // 异步启动服务器
                serverStatusService.startServerAsync();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        };
    }

    /**
     * 处理窗口关闭事件
     */
    private void handleWindowClosing() {
        logger.info("用户尝试关闭窗口");

        if (showConfirmDialog("退出之后无法监听浏览器打印", "警告")) {
            // 用户确认退出，清理资源
            cleanup();
            System.exit(0);
        } else {
            // 用户取消退出，阻止窗口关闭
            parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    /**
     * 显示信息对话框
     */
    private void showInfoDialog(String message, String title) {
        String[] options = { "确定" };
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION, null, options, options[0]);
        JDialog dialog = pane.createDialog(parentFrame, title);
        dialog.setVisible(true);
    }

    /**
     * 显示确认对话框
     */
    private boolean showConfirmDialog(String message, String title) {
        String[] options = { "确定", "取消" };
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[1]);
        JDialog dialog = pane.createDialog(parentFrame, title);
        dialog.setVisible(true);

        Object selectedValue = pane.getValue();
        return selectedValue != null && selectedValue == options[0];
    }

    /**
     * 添加打印任务到队列
     */
    public void addPrintTask(PrintDTO printDTO) {
        printQueueModel.addPrintTask(printDTO);
    }

    /**
     * 批量添加打印任务到队列
     */
    public void addPrintTasks(List<PrintDTO> printDTOs) {
        printQueueModel.addPrintTasks(printDTOs);
    }

    /**
     * 清空打印队列
     */
    public void clearPrintQueue() {
        printQueueModel.clearPrintQueue();
    }

    /**
     * 获取错误任务列表
     */
    public List<PrintDTO> getErrorTasks() {
        return printQueueModel.getErrorTasks();
    }

    /**
     * 获取可用打印机列表
     */
    public String[] getAvailablePrinters() {
        return printService.getAvailablePrinters();
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        try {
            printService.shutdown();
            serverStatusService.shutdown();
            logger.info("资源清理完成");
        } catch (Exception e) {
            logger.error("资源清理时发生错误: {}", e.getMessage(), e);
        }
    }
}