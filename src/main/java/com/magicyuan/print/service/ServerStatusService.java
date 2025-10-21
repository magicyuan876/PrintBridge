package com.magicyuan.print.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器状态管理服务
 * 使用观察者模式通知状态变化
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class ServerStatusService {

    private static final Logger logger = LoggerFactory.getLogger(ServerStatusService.class);
    private final List<StatusChangeListener> listeners;
    private final ExecutorService executor;
    private ServerStatus currentStatus;

    public ServerStatusService() {
        this.listeners = new ArrayList<>();
        this.currentStatus = ServerStatus.STARTING;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * 添加状态变化监听器
     */
    public void addStatusChangeListener(StatusChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除状态变化监听器
     */
    public void removeStatusChangeListener(StatusChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * 设置服务器状态
     */
    public void setStatus(ServerStatus status) {
        if (this.currentStatus != status) {
            this.currentStatus = status;
            notifyStatusChanged(status);
        }
    }

    /**
     * 获取当前状态
     */
    public ServerStatus getCurrentStatus() {
        return currentStatus;
    }

    /**
     * 异步启动服务器
     */
    public CompletableFuture<Boolean> startServerAsync() {
        setStatus(ServerStatus.STARTING);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 这里调用实际的服务器启动逻辑
                boolean success = startServer();
                setStatus(success ? ServerStatus.RUNNING : ServerStatus.ERROR);
                return success;
            } catch (Exception e) {
                setStatus(ServerStatus.ERROR);
                return false;
            }
        }, executor);
    }

    /**
     * 启动服务器（实际实现）
     */
    private boolean startServer() {
        try {
            // 使用基于Jetty的HTTP服务实现
            Class<?> serverClass = Class.forName("com.magicyuan.print.SingleFileHTTPServer");
            java.lang.reflect.Method initMethod = serverClass.getMethod("initServer");
            return (Boolean) initMethod.invoke(null);
        } catch (Exception e) {
            logger.error("服务器启动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 停止服务器
     */
    public void stopServer() {
        setStatus(ServerStatus.STOPPED);
        // 这里可以添加实际的服务器停止逻辑
    }

    /**
     * 通知所有监听器状态变化
     */
    private void notifyStatusChanged(ServerStatus status) {
        for (StatusChangeListener listener : listeners) {
            try {
                listener.onStatusChanged(status);
            } catch (Exception e) {
                logger.error("通知状态变化失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public enum ServerStatus {

        STARTING("正在启动服务..."), RUNNING("服务运行正常"), ERROR("服务启动失败"), STOPPED("服务已停止");

        private final String message;

        ServerStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 状态变化监听器接口
     */
    public interface StatusChangeListener {

        void onStatusChanged(ServerStatus status);
    }
}