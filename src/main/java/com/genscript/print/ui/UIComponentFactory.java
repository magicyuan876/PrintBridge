package com.genscript.print.ui;

import java.awt.*;

import javax.swing.*;

import com.genscript.print.MyJcheckBox;
import com.genscript.print.config.AppConfig;
import com.genscript.print.dto.PrintDTO;
import com.genscript.print.i18n.I18nManager;
import com.genscript.print.model.PrintQueueModel;
import com.genscript.print.service.PrintService;
import com.genscript.print.service.ServerStatusService;

/**
 * UI组件工厂类
 * 负责创建和配置各种UI组件
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class UIComponentFactory {

    /**
     * 创建主菜单栏
     */
    public static JMenuBar createMenuBar(PrintMainFrame parentFrame) {
        JMenuBar menuBar = new JMenuBar();

        // 添加一个空的占位符，让帮助菜单靠右显示
        menuBar.add(Box.createHorizontalGlue());

        // 帮助菜单
        JMenu helpMenu = new JMenu("ℹ");
        helpMenu.setFont(new Font(AppConfig.Fonts.FONT_FAMILY_SANS, Font.PLAIN, 14));
        helpMenu.setToolTipText("软件信息");

        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog(parentFrame));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * 创建主面板
     */
    public static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppConfig.Colors.BACKGROUND);
        return mainPanel;
    }

    /**
     * 创建顶部信息面板
     */
    public static JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppConfig.Colors.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppConfig.Colors.BORDER, 1), BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        return topPanel;
    }

    /**
     * 创建左侧信息面板
     */
    public static JPanel createLeftInfoPanel() {
        JPanel leftInfoPanel = new JPanel(new BorderLayout());
        leftInfoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(AppConfig.getAppDescription());
        titleLabel.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, AppConfig.Fonts.TITLE_SIZE));
        titleLabel.setForeground(AppConfig.Colors.TEXT_PRIMARY);

        JLabel versionLabel = new JLabel(AppConfig.APP_VERSION);
        versionLabel.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, AppConfig.Fonts.SMALL_SIZE));
        versionLabel.setForeground(AppConfig.Colors.TEXT_SECONDARY);

        leftInfoPanel.add(titleLabel, BorderLayout.NORTH);
        leftInfoPanel.add(versionLabel, BorderLayout.CENTER);

        return leftInfoPanel;
    }

    /**
     * 创建状态面板
     */
    public static JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        return statusPanel;
    }

    /**
     * 创建状态文本标签
     */
    public static JLabel createStatusTextLabel() {
        JLabel statusText = new JLabel(ServerStatusService.ServerStatus.STARTING.getMessage());
        statusText.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, AppConfig.Fonts.NORMAL_SIZE));
        statusText.setForeground(AppConfig.Colors.TEXT_SECONDARY);
        return statusText;
    }

    /**
     * 创建状态指示器
     */
    public static JLabel createStatusIndicator() {
        JLabel statusIndicator = new JLabel("●");
        statusIndicator.setFont(new Font(AppConfig.Fonts.FONT_FAMILY_SANS, Font.BOLD, AppConfig.Fonts.SMALL_SIZE));
        statusIndicator.setForeground(AppConfig.Colors.STATUS_STARTING);
        return statusIndicator;
    }

    /**
     * 创建右侧操作面板
     */
    public static JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        return rightPanel;
    }

    /**
     * 创建打印按钮
     */
    public static JButton createPrintButton() {
        JButton btn = new JButton("开始打印");
        btn.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, AppConfig.Fonts.NORMAL_SIZE));
        btn.setForeground(Color.WHITE);
        btn.setBackground(AppConfig.Colors.BUTTON_PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加鼠标悬停效果
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(AppConfig.Colors.BUTTON_PRIMARY.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(AppConfig.Colors.BUTTON_PRIMARY);
            }
        });

        return btn;
    }

    /**
     * 创建文件列表面板
     */
    public static JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(AppConfig.Colors.WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppConfig.Colors.BORDER, 1), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return listPanel;
    }

    /**
     * 创建列表标题面板
     */
    public static JPanel createListHeaderPanel() {
        JPanel listHeaderPanel = new JPanel(new BorderLayout());
        listHeaderPanel.setOpaque(false);
        listHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel listTitle = new JLabel("打印队列");
        listTitle.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, AppConfig.Fonts.SUBTITLE_SIZE));
        listTitle.setForeground(AppConfig.Colors.TEXT_PRIMARY);

        JLabel listSubtitle = new JLabel("选择要打印的文件");
        listSubtitle.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, AppConfig.Fonts.SMALL_SIZE));
        listSubtitle.setForeground(AppConfig.Colors.TEXT_SECONDARY);

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.add(listTitle, BorderLayout.NORTH);
        titleContainer.add(listSubtitle, BorderLayout.CENTER);

        listHeaderPanel.add(titleContainer, BorderLayout.WEST);

        return listHeaderPanel;
    }

    /**
     * 创建文件列表
     */
    public static JList<PrintDTO> createFileList(PrintQueueModel model) {
        JList<PrintDTO> jList = new JList<>(model);

        // 设置自定义渲染器
        MyJcheckBox cellRenderer = new MyJcheckBox();
        jList.setCellRenderer(cellRenderer);

        // 设置多选模式
        jList.setSelectionModel(new DefaultListSelectionModel() {

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        jList.setBackground(AppConfig.Colors.LIST_BACKGROUND);
        jList.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        jList.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, AppConfig.Fonts.NORMAL_SIZE));

        return jList;
    }

    /**
     * 创建滚动面板
     */
    public static JScrollPane createScrollPane(JList list) {
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.Colors.BORDER, 1));
        scrollPane.getViewport().setBackground(AppConfig.Colors.LIST_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        return scrollPane;
    }

    /**
     * 更新状态指示器
     */
    public static void updateStatusIndicator(JLabel indicator, ServerStatusService.ServerStatus status) {
        Color color;
        switch (status) {
        case STARTING:
            color = AppConfig.Colors.STATUS_STARTING;
            break;
        case RUNNING:
            color = AppConfig.Colors.STATUS_SUCCESS;
            break;
        case ERROR:
            color = AppConfig.Colors.STATUS_ERROR;
            break;
        default:
            color = AppConfig.Colors.TEXT_SECONDARY;
            break;
        }
        indicator.setForeground(color);
    }

    /**
     * 显示关于对话框
     */
    private static void showAboutDialog(PrintMainFrame parentFrame) {
        // 创建自定义对话框
        JDialog aboutDialog = new JDialog(parentFrame, "关于 " + AppConfig.APP_NAME, true);
        aboutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        aboutDialog.setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 顶部面板 - 应用信息
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // 应用图标区域 (可以后续添加图标)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setOpaque(false);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // 创建一个简单的应用图标
        JLabel iconLabel = new JLabel("🖨️");
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        iconPanel.add(iconLabel);
        topPanel.add(iconPanel, BorderLayout.NORTH);

        // 应用标题
        JLabel titleLabel = new JLabel(AppConfig.APP_NAME);
        titleLabel.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 中间面板 - 详细信息
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.fill = GridBagConstraints.NONE;

        // 添加版本信息
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel versionLabelKey = new JLabel(I18nManager.getString("about.version"));
        versionLabelKey.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 14));
        versionLabelKey.setForeground(new Color(102, 102, 102));
        versionLabelKey.setPreferredSize(new Dimension(100, 20));
        centerPanel.add(versionLabelKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel versionLabelValue = new JLabel(AppConfig.APP_VERSION);
        versionLabelValue.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 14));
        versionLabelValue.setForeground(new Color(51, 51, 51));
        centerPanel.add(versionLabelValue, gbc);

        // 添加作者信息
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 8, 15);
        JLabel authorLabelKey = new JLabel(I18nManager.getString("about.author"));
        authorLabelKey.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 14));
        authorLabelKey.setForeground(new Color(102, 102, 102));
        authorLabelKey.setPreferredSize(new Dimension(100, 20));
        centerPanel.add(authorLabelKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel authorLabelValue = new JLabel(AppConfig.APP_AUTHOR);
        authorLabelValue.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 14));
        authorLabelValue.setForeground(new Color(51, 51, 51));
        centerPanel.add(authorLabelValue, gbc);

        // 添加描述信息
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 8, 15);
        JLabel descLabelKey = new JLabel(I18nManager.getString("about.description"));
        descLabelKey.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 14));
        descLabelKey.setForeground(new Color(102, 102, 102));
        descLabelKey.setPreferredSize(new Dimension(100, 20));
        centerPanel.add(descLabelKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel descLabelValue = new JLabel(AppConfig.getAppDescription());
        descLabelValue.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 14));
        descLabelValue.setForeground(new Color(51, 51, 51));
        centerPanel.add(descLabelValue, gbc);

        // 添加功能信息
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 8, 15);
        JLabel funcLabelKey = new JLabel(I18nManager.getString("about.function"));
        funcLabelKey.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 14));
        funcLabelKey.setForeground(new Color(102, 102, 102));
        funcLabelKey.setPreferredSize(new Dimension(100, 20));
        centerPanel.add(funcLabelKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel funcLabelValue = new JLabel(AppConfig.getAppFunction());
        funcLabelValue.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 14));
        funcLabelValue.setForeground(new Color(51, 51, 51));
        centerPanel.add(funcLabelValue, gbc);
        
        // 添加格式支持信息
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 8, 15);
        JLabel formatLabelKey = new JLabel(I18nManager.getString("about.format"));
        formatLabelKey.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 14));
        formatLabelKey.setForeground(new Color(102, 102, 102));
        formatLabelKey.setPreferredSize(new Dimension(100, 20));
        centerPanel.add(formatLabelKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        String formatInfo = I18nManager.isChinese() ? "PDF, 图片" : "PDF, Images";
        if (parentFrame.getPrintService().isOfficeConverterAvailable()) {
            formatInfo += I18nManager.isChinese() ? ", Office文档" : ", Office";
        }
        JLabel formatLabelValue = new JLabel(formatInfo);
        formatLabelValue.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 14));
        formatLabelValue.setForeground(new Color(51, 51, 51));
        formatLabelValue.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        centerPanel.add(formatLabelValue, gbc);

        // 底部面板 - 版权信息和确定按钮
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // 版权信息
        JLabel copyrightLabel = new JLabel(AppConfig.getCopyright());
        copyrightLabel.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(153, 153, 153));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton okButton = new JButton("确定");
        okButton.setFont(new Font(AppConfig.Fonts.FONT_FAMILY, Font.BOLD, 13));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(70, 130, 180));
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 按钮悬停效果
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                okButton.setBackground(new Color(60, 115, 160));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                okButton.setBackground(new Color(70, 130, 180));
            }
        });

        // 确定按钮事件
        okButton.addActionListener(e -> aboutDialog.dispose());

        buttonPanel.add(okButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        // 组装主面板
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 设置对话框属性
        aboutDialog.add(mainPanel);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(parentFrame);

        // 添加边框阴影效果
        aboutDialog.getRootPane().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        aboutDialog.setVisible(true);
    }
}