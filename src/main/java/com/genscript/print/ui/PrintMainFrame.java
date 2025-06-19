package com.genscript.print.ui;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import com.genscript.print.config.AppConfig;
import com.genscript.print.controller.PrintController;
import com.genscript.print.dto.PrintDTO;
import com.genscript.print.model.PrintQueueModel;
import com.genscript.print.service.PrintService;
import com.genscript.print.service.ServerStatusService;

/**
 * 打印应用程序主窗口
 * 重构后的UI类，遵循MVC架构模式
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class PrintMainFrame extends JFrame implements ServerStatusService.StatusChangeListener {

    // 数据模型
    private final PrintQueueModel printQueueModel;

    // 服务层
    private final PrintService printService;

    private final ServerStatusService serverStatusService;

    // 控制器
    private final PrintController printController;

    // UI组件
    private JLabel statusTextLabel;

    private JLabel statusIndicator;

    private JList<PrintDTO> fileList;

    private JButton printButton;

    /**
     * 构造函数
     */
    public PrintMainFrame() {
        // 初始化数据模型
        this.printQueueModel = new PrintQueueModel();

        // 初始化服务
        this.printService = new PrintService(printQueueModel);
        this.serverStatusService = new ServerStatusService();

        // 初始化UI组件
        initializeComponents();

        // 初始化控制器
        this.printController = new PrintController(printQueueModel, printService, serverStatusService, this, fileList);

        // 设置事件监听器
        setupEventListeners();

        // 设置状态变化监听器
        serverStatusService.addStatusChangeListener(this);

        // 配置窗口
        configureWindow();

        // 设置兼容性引用
        com.genscript.print.PrintServerUI.setMainFrame(this);
    }

    /**
     * 初始化UI组件
     */
    private void initializeComponents() {
        // 设置窗口布局
        setLayout(new BorderLayout());

        // 创建主面板
        JPanel mainPanel = UIComponentFactory.createMainPanel();

        // 创建并配置顶部面板
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 创建并配置文件列表面板
        JPanel listPanel = createListPanel();
        mainPanel.add(listPanel, BorderLayout.CENTER);

        // 添加到主窗口
        add(mainPanel, BorderLayout.CENTER);

        // 创建菜单栏
        JMenuBar menuBar = UIComponentFactory.createMenuBar(this);
        setJMenuBar(menuBar);
    }

    /**
     * 创建顶部面板
     */
    private JPanel createTopPanel() {
        JPanel topPanel = UIComponentFactory.createTopPanel();

        // 左侧信息面板
        JPanel leftInfoPanel = UIComponentFactory.createLeftInfoPanel();
        topPanel.add(leftInfoPanel, BorderLayout.WEST);

        // 中间状态面板
        JPanel statusPanel = UIComponentFactory.createStatusPanel();
        statusIndicator = UIComponentFactory.createStatusIndicator();
        statusTextLabel = UIComponentFactory.createStatusTextLabel();
        statusPanel.add(statusIndicator);
        statusPanel.add(statusTextLabel);
        topPanel.add(statusPanel, BorderLayout.CENTER);

        // 右侧操作面板
        JPanel rightPanel = UIComponentFactory.createRightPanel();
        printButton = UIComponentFactory.createPrintButton();
        rightPanel.add(printButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    /**
     * 创建文件列表面板
     */
    private JPanel createListPanel() {
        JPanel listPanel = UIComponentFactory.createListPanel();

        // 列表标题
        JPanel listHeaderPanel = UIComponentFactory.createListHeaderPanel();
        listPanel.add(listHeaderPanel, BorderLayout.NORTH);

        // 文件列表
        fileList = UIComponentFactory.createFileList(printQueueModel);
        JScrollPane scrollPane = UIComponentFactory.createScrollPane(fileList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    /**
     * 设置事件监听器
     */
    private void setupEventListeners() {
        // 打印按钮事件
        printButton.addActionListener(printController.createPrintButtonListener());

        // 窗口事件
        addWindowListener(printController.createWindowListener());
    }

    /**
     * 配置窗口属性
     */
    private void configureWindow() {
        setTitle(AppConfig.APP_NAME);
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setLocation(AppConfig.WINDOW_X, AppConfig.WINDOW_Y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置窗口图标（如果有的话）
        // setIconImage(...);

        // 设置最小窗口大小
        setMinimumSize(new Dimension(600, 400));
    }

    /**
     * 服务器状态变化监听器实现
     */
    @Override
    public void onStatusChanged(ServerStatusService.ServerStatus status) {
        // 在事件调度线程中更新UI
        SwingUtilities.invokeLater(() -> {
            statusTextLabel.setText(status.getMessage());
            UIComponentFactory.updateStatusIndicator(statusIndicator, status);
        });
    }

    /**
     * 公共方法：添加打印任务
     * 供外部调用（如HTTP服务器）
     */
    public void addPrintTask(PrintDTO printDTO) {
        printController.addPrintTask(printDTO);
    }

    /**
     * 公共方法：批量添加打印任务
     */
    public void addPrintTasks(List<PrintDTO> printDTOs) {
        printController.addPrintTasks(printDTOs);
    }

    /**
     * 公共方法：清空打印队列
     */
    public void clearPrintQueue() {
        printController.clearPrintQueue();
    }

    /**
     * 公共方法：获取错误任务
     */
    public List<PrintDTO> getErrorTasks() {
        return printController.getErrorTasks();
    }

    /**
     * 公共方法：获取可用打印机
     */
    public String[] getAvailablePrinters() {
        return printController.getAvailablePrinters();
    }

    /**
     * 获取打印队列模型（供外部使用）
     */
    public PrintQueueModel getPrintQueueModel() {
        return printQueueModel;
    }
}