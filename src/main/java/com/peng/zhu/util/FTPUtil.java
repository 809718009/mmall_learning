package com.peng.zhu.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip","192.168.2.2");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user","ftpUser");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password","123456");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public FTPUtil(String ip,int port,String user,String password){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.password=password;
    }
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPassword);
        logger.info("开始文件上传到ftp服务器!");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("传到文件到ftp服务器结束!");
        return result;
    }
    private  boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean upload=true;
        FileInputStream fis = null;
        if(connectServer(this.ip,this.user,this.password)){
            try {
                //todo
                //ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem:fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                upload=false;
                logger.error("上传图片到ftp异常!",e);
            }
            finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return upload;
    }
    private boolean connectServer(String ip,String user,String password){
       boolean isSuccess=false;
       ftpClient= new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess=ftpClient.login(user,password);
        } catch (IOException e) {
            logger.error("ftp连接错误!");
        }
        return isSuccess;
    }
}
