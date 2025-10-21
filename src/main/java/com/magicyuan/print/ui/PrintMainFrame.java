package com.magicyuan.print.ui;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import com.magicyuan.print.SingleFileHTTPServer;
import com.magicyuan.print.config.AppConfig;
import com.magicyuan.print.controller.PrintController;
import com.magicyuan.print.dto.PrintDTO;
import com.magicyuan.print.model.PrintQueueModel;
import com.magicyuan.print.service.PrintService;
import com.magicyuan.print.service.ServerStatusService;

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
    
    // 系统托盘管理器
    private final SystemTrayManager systemTrayManager;

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
        this.systemTrayManager = new SystemTrayManager(this);
        
        // 设置PrintService到HTTP服务器
        SingleFileHTTPServer.setPrintService(this.printService);

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
        
        // 检查LibreOffice状态并提示
        checkLibreOfficeStatus();
    }
    
    /**
     * 检查LibreOffice状态并显示引导提示
     */
    private void checkLibreOfficeStatus() {
        SwingUtilities.invokeLater(() -> {
            if (!printService.isOfficeConverterAvailable()) {
                showLibreOfficeGuide();
            }
        });
    }
    
    /**
     * 显示LibreOffice安装引导
     */
    private void showLibreOfficeGuide() {
        // 创建自定义对话框
        JDialog dialog = new JDialog(this, "PrintBridge - 多格式支持", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(new Color(245, 247, 250));
        
        // 主内容面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // 标题
        JLabel titleLabel = new JLabel("<html><span style='font-size: 18px;'>&#128196;</span> 多格式打印支持</html>");  // 📄
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        mainPanel.add(titleLabel, gbc);
        
        // 卡片1: 当前支持的格式 (绿色)
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel supportedCard = createColorCard(
            "<html>&#10003; 当前支持的格式</html>",  // ✓
            new String[]{
                "<html>&nbsp;&nbsp;&#8226; PDF文档 - 完全支持</html>",
                "<html>&nbsp;&nbsp;&#8226; 图片格式 - PNG, JPG, GIF, BMP</html>"
            },
            new Color(232, 245, 233),  // 浅绿背景
            new Color(76, 175, 80)      // 绿色边框
        );
        mainPanel.add(supportedCard, gbc);
        
        // 卡片2: Office格式未启用 (橙色)
        gbc.gridy++;
        JPanel unsupportedCard = createColorCard(
            "<html>&#9888; Office格式未启用</html>",  // ⚠
            new String[]{
                "<html>&nbsp;&nbsp;&#8226; Word - .docx, .doc</html>",
                "<html>&nbsp;&nbsp;&#8226; Excel - .xlsx, .xls</html>",
                "<html>&nbsp;&nbsp;&#8226; PowerPoint - .pptx, .ppt</html>"
            },
            new Color(255, 243, 224),  // 浅橙背景
            new Color(255, 152, 0)      // 橙色边框
        );
        mainPanel.add(unsupportedCard, gbc);
        
        // 卡片3: 如何启用 (蓝色)
        gbc.gridy++;
        JPanel guideCard = createColorCard(
            "<html>&#128161; 如何启用Office格式支持</html>",  // 💡
            new String[]{
                "<html>&nbsp;&nbsp;1. 下载安装 LibreOffice (免费开源)</html>",
                "<html>&nbsp;&nbsp;2. 重启 PrintBridge 服务</html>",
                "<html>&nbsp;&nbsp;3. 即可支持所有格式</html>"
            },
            new Color(227, 242, 253),  // 浅蓝背景
            new Color(33, 150, 243)     // 蓝色边框
        );
        mainPanel.add(guideCard, gbc);
        
        // 提示文字
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JLabel promptLabel = new JLabel("是否现在打开LibreOffice下载页面?");
        promptLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        promptLabel.setForeground(new Color(102, 102, 102));
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(promptLabel, gbc);
        
        // 填充剩余空间
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(Box.createGlue(), gbc);
        
        // 滚动面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        // 立即下载按钮
        JButton downloadButton = new JButton("立即下载");
        downloadButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setBackground(new Color(52, 152, 219));
        downloadButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        downloadButton.setFocusPainted(false);
        downloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        downloadButton.setOpaque(true);
        
        downloadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                downloadButton.setBackground(new Color(41, 128, 185));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                downloadButton.setBackground(new Color(52, 152, 219));
            }
        });
        
        downloadButton.addActionListener(e -> {
            openLibreOfficeDownloadPage();
            dialog.dispose();
        });
        
        // 稍后安装按钮
        JButton laterButton = new JButton("稍后安装");
        laterButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        laterButton.setForeground(new Color(95, 99, 104));
        laterButton.setBackground(Color.WHITE);
        laterButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
            BorderFactory.createEmptyBorder(9, 29, 9, 29)
        ));
        laterButton.setFocusPainted(false);
        laterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        laterButton.setOpaque(true);
        
        laterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                laterButton.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                laterButton.setBackground(Color.WHITE);
            }
        });
        
        laterButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(downloadButton);
        buttonPanel.add(laterButton);
        
        // 组装对话框
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置对话框属性
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * 创建彩色卡片面板
     */
    private JPanel createColorCard(String title, String[] items, Color bgColor, Color borderColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),  // 左侧彩色边框
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // 标题
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        
        // 内容项
        for (String item : items) {
            JLabel itemLabel = new JLabel(item);
            itemLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            itemLabel.setForeground(new Color(68, 68, 68));
            itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(itemLabel);
            card.add(Box.createVerticalStrut(5));
        }
        
        // 添加底部填充
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    /**
     * 打开LibreOffice下载页面
     */
    private void openLibreOfficeDownloadPage() {
        try {
            String url = "https://www.libreoffice.org/download/download/";
            
            // 根据操作系统选择合适的打开方式
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(java.net.URI.create(url));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "无法自动打开浏览器\n请手动访问:\nhttps://www.libreoffice.org/download/download/",
                "提示",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
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
        
        // 设置为不自动退出,由WindowListener处理关闭逻辑
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 设置窗口图标（如果有的话）
        // setIconImage(...);

        // 设置最小窗口大小
        setMinimumSize(new Dimension(600, 400));
        
        // 添加窗口状态监听器 - 支持从任务栏恢复
        addWindowStateListener(e -> {
            if ((e.getNewState() & java.awt.Frame.ICONIFIED) == 0 && (e.getOldState() & java.awt.Frame.ICONIFIED) != 0) {
                // 窗口从最小化恢复
                setVisible(true);
                toFront();
                requestFocus();
            }
        });
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
    
    /**
     * 获取打印服务（供UI使用）
     */
    public PrintService getPrintService() {
        return printService;
    }
    
    /**
     * 获取系统托盘管理器
     */
    public SystemTrayManager getSystemTrayManager() {
        return systemTrayManager;
    }
}