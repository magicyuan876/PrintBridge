package com.magicyuan.print.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import com.magicyuan.print.dto.PrintDTO;
import com.magicyuan.print.ui.PrintMainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicyuan.print.model.PrintQueueModel;
import com.magicyuan.print.service.PrintService;
import com.magicyuan.print.service.ServerStatusService;
import com.magicyuan.print.ui.SystemTrayManager;

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

    private final PrintMainFrame parentFrame;

    private final JList<PrintDTO> fileList;

    public PrintController(PrintQueueModel printQueueModel, PrintService printService, ServerStatusService serverStatusService, PrintMainFrame parentFrame, JList<PrintDTO> fileList) {
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

        // 创建自定义对话框
        Object[] options = {"最小化到后台", "完全退出", "取消"};
        int choice = JOptionPane.showOptionDialog(
            parentFrame,
            "选择操作:\n\n• 最小化到后台: 隐藏窗口,继续监听浏览器打印\n• 完全退出: 停止服务并关闭程序",
            "PrintBridge - 关闭确认",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        switch (choice) {
            case 0: // 最小化到后台
                logger.info("用户选择最小化到后台");
                minimizeToTray();
                break;
            case 1: // 完全退出
                logger.info("用户选择完全退出");
                cleanup();
                System.exit(0);
                break;
            case 2: // 取消
            default:
                logger.info("用户取消关闭操作");
                // 什么都不做,窗口保持打开状态
                break;
        }
    }
    
    /**
     * 最小化到系统托盘
     */
    private void minimizeToTray() {
        SystemTrayManager trayManager = parentFrame.getSystemTrayManager();
        
        // 隐藏窗口
        parentFrame.setVisible(false);
        
        // 显示托盘图标
        trayManager.showTrayIcon();
        
        logger.info("窗口已最小化到系统托盘");
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