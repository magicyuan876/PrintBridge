package com.magicyuan.print;

import com.magicyuan.print.service.JettyPrintService;
import com.magicyuan.print.service.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Jetty的HTTP服务器实现
 * 替代原来手工实现的HTTP服务器，提供企业级的稳定性和性能
 * 保持相同的接口，确保无缝升级
 */
public class SingleFileHTTPServer {

    private static final Logger logger = LoggerFactory.getLogger(SingleFileHTTPServer.class);
    private static final Object lock = new Object();
    private static JettyPrintService jettyPrintService;
    private static PrintService printService;

    /**
     * 设置打印服务实例
     * @param service 打印服务
     */
    public static void setPrintService(PrintService service) {
        printService = service;
    }

    /**
     * 初始化服务器 - 保持与原版本相同的接口
     * @return 是否启动成功
     */
    public static boolean initServer() {
        synchronized (lock) {
            try {
                if (jettyPrintService != null && jettyPrintService.isRunning()) {
                    logger.info("HTTP服务已经在运行中");
                    return true;
                }

                if (printService == null) {
                    logger.error("PrintService未设置，无法启动HTTP服务");
                    return false;
                }

                jettyPrintService = new JettyPrintService(printService);
                boolean success = jettyPrintService.start(8281);

                if (success) {
                    logger.info("=== PrintBridge HTTP服务启动成功 ===");
                    logger.info("监听端口: 8281");
                    logger.info("服务端点:");
                    logger.info("  - POST http://localhost:8281/print  : 打印服务");
                    logger.info("  - GET  http://localhost:8281/health : 健康检查");
                    logger.info("=====================================");
                } else {
                    logger.error("HTTP服务启动失败");
                }

                return success;

            } catch (Exception e) {
                logger.error("初始化HTTP服务时发生错误: {}", e.getMessage(), e);
                return false;
            }
        }
    }

    /**
     * 停止服务器
     */
    public static void stopServer() {
        synchronized (lock) {
            if (jettyPrintService != null) {
                jettyPrintService.stop();
                jettyPrintService = null;
                logger.info("HTTP服务已停止");
            }
        }
    }

    /**
     * 检查服务器是否运行中
     * @return 服务器运行状态
     */
    public static boolean isServerRunning() {
        synchronized (lock) {
            return jettyPrintService != null && jettyPrintService.isRunning();
        }
    }

    /**
     * 获取服务器状态信息
     * @return 状态信息
     */
    public static String getServerStatus() {
        synchronized (lock) {
            if (jettyPrintService != null && jettyPrintService.isRunning()) {
                return "Jetty HTTP打印服务正在运行 - 端口: 8281";
            } else {
                return "HTTP打印服务未运行";
            }
        }
    }

    /**
     * 重启服务器
     * @return 重启是否成功
     */
    public static boolean restartServer() {
        synchronized (lock) {
            stopServer();
            // 等待一下确保完全停止
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return initServer();
        }
    }
}