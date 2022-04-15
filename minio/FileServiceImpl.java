package com.alibaba.asp.service.impl;

import com.alibaba.asp.common.constant.enums.ResultCode;
import com.alibaba.asp.common.error.AspErrorCode;
import com.alibaba.asp.common.error.AspException;
import com.alibaba.asp.file.entity.AspFile;
import com.alibaba.asp.file.service.FileDomainService;
import com.alibaba.asp.service.FileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private FileDomainService fileDomainService;


    @Override
    public void uploadFiles(MultipartFile[] files) {
        if (files == null && files.length == 0) {
            throw new AspException(ResultCode.FAILURE, AspErrorCode.FILE_ARR_NOT_EMPTY);
        }

        for (MultipartFile file : files) {
            AspFile aspFile = buildAspFile(file);
            fileDomainService.addFile(aspFile);
            minioUtil.addObject(file, aspFile.getPath());
        }
    }

    private AspFile buildAspFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String dateFormat = new SimpleDateFormat("/yyyyMMdd/").format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String path = dateFormat + uuid + fileType;
        return AspFile.builder()
                .fileId(uuid)
                .fileName(fileName)
                .path(path)
                .build();
    }


    @Override
    public void downloadFile(HttpServletResponse response, String fileName) {

        InputStream in = null;
        try {
            // 获取文件信息
            in = minioUtil.getObject(fileName);
            if (in == null) {
                throw new AspException(ResultCode.FAILURE, AspErrorCode.FILE_NOT_FOUND);
            }
            StatObjectResponse stat = minioUtil.getStatObject(fileName);
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("InputStream 关闭失败" + e.getMessage());
                }
            }
        }
    }

    /**
     * 删除文件
     */
    @Override
    public boolean deleteFile(String fileId) {
        AspFile file = fileDomainService.getFileById(fileId);
        if (file != null) {
            boolean remove = fileDomainService.removeFileById(fileId);
            minioUtil.removeObject(file.getFileName());
            return remove;
        }
        return false;
    }

}
