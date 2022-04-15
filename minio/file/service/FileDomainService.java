package com.alibaba.asp.file.service;

import com.alibaba.asp.file.entity.AspFile;

/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
public interface FileDomainService {

    /**
     * 获取文件
     * @param fileId 文件ID
     */
    AspFile getFileById(String fileId);

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    boolean removeFileById(String fileId);

    String addFile(AspFile aspFile);
}
