package com.alibaba.asp.service;

import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
public interface FileService {
    /**
     * 上传文件
     */
    void uploadFiles(MultipartFile[] files);

    /**
     * 下载文件
     */
    void downloadFile(HttpServletResponse response, String fileName);

    /**
     * 删除文件
     */
    boolean deleteFile(String fileId);
}
