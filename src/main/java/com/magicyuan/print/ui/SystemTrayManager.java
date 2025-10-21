package com.magicyuan.print.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统托盘管理器
 * 负责创建和管理系统托盘图标
 * 
 * @author Magic_yuan
 * @version 2.1.0
 */
public class SystemTrayManager {

    private static final Logger logger = LoggerFactory.getLogger(SystemTrayManager.class);

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private JFrame mainFrame;
    private boolean isInitialized = false;

    public SystemTrayManager(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * 初始化系统托盘
     */
    public boolean initialize() {
        if (isInitialized) {
            return true;
        }

        // 检查系统是否支持托盘
        if (!SystemTray.isSupported()) {
            logger.warn("当前系统不支持系统托盘功能");
            return false;
        }

        try {
            systemTray = SystemTray.getSystemTray();

            // 创建托盘图标 (使用简单的图标,也可以加载图片)
            Image image = createTrayImage();
            
            // 创建弹出菜单
            PopupMenu popup = createPopupMenu();

            // 创建托盘图标
            trayIcon = new TrayIcon(image, "PrintBridge - 后台运行中", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("PrintBridge - 打印服务运行中\n双击恢复窗口");

            // 双击托盘图标恢复窗口
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        restoreWindow();
                    }
                }
            });

            isInitialized = true;
            logger.info("系统托盘初始化成功");
            return true;

        } catch (Exception e) {
            logger.error("初始化系统托盘失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 显示托盘图标
     */
    public void showTrayIcon() {
        if (!isInitialized && !initialize()) {
            return;
        }

        try {
            systemTray.add(trayIcon);
            logger.info("托盘图标已显示");
            
            // 延迟显示通知,避免被标题覆盖
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    // 显示提示消息
                    trayIcon.displayMessage(
                        "PrintBridge - 打印服务",
                        "已最小化到系统托盘,服务继续运行\n双击托盘图标可恢复窗口",
                        TrayIcon.MessageType.INFO
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } catch (AWTException e) {
            logger.error("显示托盘图标失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 隐藏托盘图标
     */
    public void hideTrayIcon() {
        if (trayIcon != null && systemTray != null) {
            systemTray.remove(trayIcon);
            logger.info("托盘图标已隐藏");
        }
    }

    /**
     * 恢复窗口
     */
    public void restoreWindow() {
        mainFrame.setVisible(true);
        mainFrame.setExtendedState(JFrame.NORMAL);
        mainFrame.toFront();
        mainFrame.requestFocus();
        hideTrayIcon();
        logger.info("窗口已从托盘恢复");
    }

    /**
     * 创建托盘图标图片
     */
    private Image createTrayImage() {
        // 创建一个简单的16x16图标
        int size = 16;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制一个打印机图标
        g.setColor(new Color(52, 152, 219)); // 蓝色
        g.fillRect(2, 2, 12, 8);
        
        g.setColor(Color.WHITE);
        g.fillRect(4, 4, 8, 4);
        
        g.setColor(new Color(52, 152, 219));
        g.fillRect(5, 10, 6, 3);
        
        g.dispose();
        
        return image;
    }

    /**
     * 创建弹出菜单
     */
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();

        // 恢复窗口
        MenuItem restoreItem = new MenuItem("打开窗口");
        restoreItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        restoreItem.addActionListener(e -> restoreWindow());
        popup.add(restoreItem);

        popup.addSeparator();

        // 关于
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                null,
                "PrintBridge 打印桥 v2.1.0\n" +
                "连接Web应用与本地打印机\n\n" +
                "服务地址: http://localhost:8281\n" +
                "支持格式: PDF, Office, 图片\n\n" +
                "作者: Magic_yuan\n" +
                "© 2020-2025 Magic_yuan",
                "关于 PrintBridge",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        popup.add(aboutItem);

        popup.addSeparator();

        // 退出
        MenuItem exitItem = new MenuItem("退出");
        exitItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                null,
                "确定要退出PrintBridge吗?\n退出后将无法接收浏览器打印请求",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                hideTrayIcon();
                System.exit(0);
            }
        });
        popup.add(exitItem);

        return popup;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        hideTrayIcon();
    }
}

