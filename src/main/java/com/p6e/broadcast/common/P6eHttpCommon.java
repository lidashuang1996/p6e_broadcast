package com.p6e.broadcast.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求的工具类
 * @version 1.0
 */
public class P6eHttpCommon {

    /**
     * HTTP 发送的 GET 的请求
     * 采用 BIO 阻塞的形式
     * @param httpUrl 请求的 URL 地址
     * @return 请求返回的结果
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            return result(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP 发送的 POST 的请求
     * 采用 BIO 阻塞的形式
     * @param httpUrl 请求的 URL 地址
     * @param param 请求的参数内容
     * @return 请求返回的结果
     */
    public static String doPost(String httpUrl, String param) {
        OutputStream os = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            return result(connection);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(null, os, null, connection);
        }
        return null;
    }

    private static String result(HttpURLConnection connection)  {
        InputStream is = null;
        BufferedReader br = null;
        try {
            if (connection.getResponseCode() == 200) {
                StringBuilder sbf = new StringBuilder();
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String temp;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                return sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(br, null, is, connection);
        }
        return null;
    }

    private static void close(BufferedReader br,
                              OutputStream os,
                              InputStream is,
                              HttpURLConnection connection) {
        try {
            if (br != null) br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (os != null) os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (is != null) is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
    }
}
