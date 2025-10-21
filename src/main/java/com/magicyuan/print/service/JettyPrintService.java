package com.magicyuan.print.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.magicyuan.print.dto.PrintDTO;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 基于Jetty的HTTP打印服务实现
 * 提供企业级的HTTP服务能力，支持高并发和完善的错误处理
 */
public class JettyPrintService {

    private static final Logger logger = LoggerFactory.getLogger(JettyPrintService.class);

    private static final int DEFAULT_PORT = 8281;

    private static final int MIN_THREADS = 10;

    private static final int MAX_THREADS = 100;

    private Server server;

    private boolean isRunning = false;
    
    private PrintService printService;

    /**
     * 构造函数
     * @param printService 打印服务实例
     */
    public JettyPrintService(PrintService printService) {
        this.printService = printService;
    }

    /**
     * 启动Jetty服务
     * @param port 监听端口
     * @return 是否启动成功
     */
    public boolean start(int port) {
        try {
            if (isRunning) {
                logger.warn("Jetty服务已经在运行中");
                return true;
            }

            // 配置线程池
            QueuedThreadPool threadPool = new QueuedThreadPool(MAX_THREADS, MIN_THREADS);
            threadPool.setName("jetty-print-service");

            // 创建服务器
            server = new Server(threadPool);

            // 创建HTTP连接器
            org.eclipse.jetty.server.ServerConnector connector = new org.eclipse.jetty.server.ServerConnector(server);
            connector.setPort(port);
            connector.setHost("0.0.0.0");

            server.addConnector(connector);

            // 创建上下文处理器集合
            ContextHandlerCollection contexts = new ContextHandlerCollection();

            // 打印服务上下文
            ContextHandler printContext = new ContextHandler("/print");
            printContext.setHandler(new PrintHandler());

            // 健康检查上下文
            ContextHandler healthContext = new ContextHandler("/health");
            healthContext.setHandler(new HealthHandler());

            // 根路径处理（OPTIONS预检请求）
            ContextHandler rootContext = new ContextHandler("/");
            rootContext.setHandler(new CorsHandler());

            contexts.setHandlers(new org.eclipse.jetty.server.Handler[] { printContext, healthContext, rootContext });

            server.setHandler(contexts);

            // 启动服务器
            server.start();
            isRunning = true;

            logger.info("PrintBridge服务已启动，监听端口: " + port);
            logger.info("服务端点:");
            logger.info("  - POST /print  : 打印服务");
            logger.info("  - GET  /health : 健康检查");

            return true;

        } catch (Exception e) {
            logger.error("启动PrintBridge服务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 启动服务（使用默认端口）
     */
    public boolean start() {
        return start(DEFAULT_PORT);
    }

    /**
     * 停止服务
     */
    public void stop() {
        if (server != null && isRunning) {
            try {
                server.stop();
                server.join();
                isRunning = false;
                logger.info("Jetty打印服务已停止");
            } catch (Exception e) {
                logger.error("停止Jetty服务时发生错误: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 检查服务是否运行中
     */
    public boolean isRunning() {
        return isRunning && server != null && server.isRunning();
    }

    /**
     * 设置CORS头
     */
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");

        String jsonResponse = String.format("{\"success\":true,\"message\":\"%s\",\"timestamp\":%d}", message, System.currentTimeMillis());

        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");

        String errorResponse = String.format("{\"success\":false,\"error\":\"%s\",\"timestamp\":%d}", errorMessage, System.currentTimeMillis());

        PrintWriter writer = response.getWriter();
        writer.write(errorResponse);
        writer.flush();
    }

    /**
     * 打印请求处理器
     */
    private class PrintHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            // 设置CORS头
            setCorsHeaders(response);

            String method = request.getMethod();

            try {
                if ("OPTIONS".equals(method)) {
                    // 处理CORS预检请求
                    handleOptionsRequest(response);
                } else if ("POST".equals(method)) {
                    // 处理打印请求
                    handlePrintRequest(request, response);
                } else {
                    // 不支持的方法
                    sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed: " + method);
                }
            } catch (Exception e) {
                logger.error("处理打印请求时发生错误: {}", e.getMessage(), e);
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
            } finally {
                baseRequest.setHandled(true);
            }
        }

        private void handleOptionsRequest(HttpServletResponse response) throws IOException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("OK");
        }

        private void handlePrintRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

            // 读取请求体
            String requestBody = readRequestBody(request);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
                return;
            }

            try {
                // 解析JSON数据
                List<PrintDTO> printList = JSONObject.parseArray(requestBody, PrintDTO.class);

                if (printList == null || printList.isEmpty()) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid print data");
                    return;
                }

                logger.info("收到打印请求，文件数量: {}", printList.size());
                for (PrintDTO dto : printList) {
                    logger.info("  - 文件: {}, URL: {}", dto.getFileName(), dto.getFileUrl());
                }

                // 异步执行打印任务
                CompletableFuture.runAsync(() -> {
                    try {
                        printService.printWithoutDialog(printList);
                        logger.info("打印任务执行完成");
                    } catch (Exception e) {
                        logger.error("执行打印任务时发生错误: {}", e.getMessage(), e);
                    }
                });

                // 返回成功响应
                sendSuccessResponse(response, "Print job submitted successfully");

            } catch (Exception e) {
                logger.warn("解析打印数据失败: {}", e.getMessage(), e);
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
            }
        }

        private String readRequestBody(HttpServletRequest request) throws IOException {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            return body.toString();
        }
    }

    /**
     * 健康检查处理器
     */
    private class HealthHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            setCorsHeaders(response);

            if ("GET".equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json; charset=UTF-8");

                String healthResponse = String.format("{\"status\":\"OK\",\"service\":\"Jetty Print Service\",\"timestamp\":%d,\"version\":\"1.0\"}", System.currentTimeMillis());

                response.getWriter().write(healthResponse);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
            }

            baseRequest.setHandled(true);
        }
    }

    /**
     * CORS处理器（处理根路径的OPTIONS请求）
     */
    private class CorsHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            setCorsHeaders(response);

            if ("OPTIONS".equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("OK");
                baseRequest.setHandled(true);
            }
            // 对于非OPTIONS请求，不设置handled，让其他处理器处理
        }
    }
}