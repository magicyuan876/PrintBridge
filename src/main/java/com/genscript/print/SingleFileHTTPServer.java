package com.genscript.print;

import com.alibaba.fastjson.JSONObject;
import com.genscript.print.dto.PrintDTO;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.List;

public class SingleFileHTTPServer extends Thread {

    private byte[] content;

    private byte[] header;

    private int port = 8281;

    //用户请求的文件的url
    private String requestPath;

    //mltipart/form-data方式提交post的分隔符,
    private String boundary = null;

    //post提交请求的正文的长度
    private int contentLength = 0;

    private SingleFileHTTPServer(String data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
        this(data.getBytes(encoding), encoding, MIMEType, port);
    }

    public SingleFileHTTPServer(byte[] data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
        this.content = data;
        this.port = port;
        String header = "HTTP/1.0 200 OK\r\n" + "Server: OneFile 1.0\r\n" + "Content-length: 22\r\n" + "Content-type: " + MIMEType + "\r\n\r\n";
        this.header = header.getBytes("ASCII");
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            System.out.println("Accepting connections on port " + server.getLocalPort());
            System.out.println("Data to be sent:");

            while (true) {
                Socket connection = null;
                try {
                    connection = server.accept();
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    StringBuffer request = new StringBuffer();

                    DataInputStream reader = new DataInputStream((connection.getInputStream()));
                    String line = reader.readLine();
                    String method = line.substring(0, 4).trim();
                    this.requestPath = line.split(" ")[1];
                    System.out.println(method);
                    if ("GET".equalsIgnoreCase(method)) {
                        System.out.println("do get......");
                        //                        this.doGet(reader, out);
                    } else if ("POST".equalsIgnoreCase(method)) {
                        System.out.println("do post......");
                        this.doPost(reader, out);
                    }else{

                        //发送回浏览器的内容
                        PrintStream writer = new PrintStream(out, true);
                        String response = "";
                        response += "HTTP/1.1 200 OK/n";
                        response += "Server: Sunpache 1.0/n";
                        response += "Content-Type: text/html/n";
                        response += "Access-Control-Allow-Origin: */n";
                        response += "Last-Modified: Mon, 11 Jan 1998 13:23:42 GMT/n";
                        response += "Accept-ranges: bytes";
                        response += "/n";
                        String body = "ok";
                        System.out.println(body);

                        writer.println("HTTP/1.1 202 OK");
                        writer.println("Access-Control-Allow-Origin: *");
                        writer.println("Access-Control-Request-Method: POST,GET,PUT,OPTIONS,DELETE");
                        writer.println("Access-Control-Allow-Headers:x-requested-with,content-type");


                        writer.println();
                        writer.println("ok");
                        writer.flush();
                        reader.close();
                        out.close();
                        System.out.println("request complete.");

                    }

                    //                    int ch;
                    //                    while((ch = reader.read()) != -1){
                    //                        request.append((char) ch);
                    //                    }
                    //                    //如果检测到是HTTP/1.0及以后的协议，按照规范，需要发送一个MIME首部
                    //                    if (request.toString().indexOf("HTTP/") != -1) {
                    //                        out.write(this.header);
                    //                    }
                    //                    System.out.println(request.toString());
                    //                    String[] pdfFlies = request.toString().split("&callback")[0].split("=")[1].split(",");
                    //
                    //                    for (final String filePath : pdfFlies) {
                    //                        Thread t1 = new Thread(new Runnable() {
                    //                            public void run() {
                    //                                PrintUtils.printWithOutDialog(StringUtils.trim(filePath));
                    //                            }
                    //                        });
                    //                        t1.start();
                    //                    }
                    //                    out.write(2);
                    //                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                } finally {
                    if (connection != null) {
                        connection.close();
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Could not start server. Port Occupied");
        }
    }

    //处理post请求
    private void doPost(DataInputStream reader, OutputStream out) throws Exception {
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
            if ("".equals(line)) {
                break;
            } else if (line.indexOf("Content-Length") != -1) {
                this.contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
            }
        }
        //继续读取普通post（没有附件）提交的数据
        System.out.println("begin reading posted data......");
        String dataLine = null;
        //用户发送的post数据正文
        byte[] buf = {};
        int size = 0;
        String requestData = null;
        if (this.contentLength != 0) {
            buf = new byte[this.contentLength];
            while (size < this.contentLength) {
                int c = reader.read();
                buf[size++] = (byte) c;

            }

            requestData = new String(buf, 0, size);
            System.out.println("The data user posted: " + requestData);
        }

        final List<PrintDTO> list = JSONObject.parseArray(requestData, PrintDTO.class);
        Thread t1 = new Thread(new Runnable() {

            public void run() {
                PrintUtils.printWithOutDialog(list);
            }
        });
        t1.start();

        //发送回浏览器的内容
        PrintStream writer = new PrintStream(out, true);

        writer.println("HTTP/1.1 200 OK");
        writer.println("Access-Control-Allow-Origin: *");
        writer.println("Access-Control-Request-Method: POST,GET,PUT,OPTIONS,DELETE");
        writer.println("Access-Control-Allow-Headers:x-requested-with,content-type");
        writer.println(); //打印空行之后表示http的response
        writer.println("ok");
        writer.flush();
        reader.close();
        out.close();
        System.out.println("request complete.");
    }

    public static boolean initServer() {
        try {
            String contentType = "text/plain";
            byte[] data = null;

            //设置监听端口
            int port = 8281;

            String encoding = "UTF-8";

            Thread t = new SingleFileHTTPServer(data, encoding, contentType, port);
            t.start();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage:java SingleFileHTTPServer filename port encoding");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);// TODO: handle exception
            return false;
        }

        return true;
    }
}
