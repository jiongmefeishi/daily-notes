package com.alibaba.asp.common.config;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
@Component
@Slf4j
public class MinioUtil {

    @Value("${minio.bucket-name}")
    private String BUCKET_NAME;

    @Autowired
    private MinioClient minioClient;

    /**
     * 获取文件流
     * @param objectName
     * @return InputStream
     */
    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build());
        } catch (Exception e) {
            log.info("文件不存在，文件名：{}", objectName);
            log.info(e.getMessage());
        }
        return null;
    }

    /**
     * 判断对象是否存在
     * @param objectName 对象名
     * @return true/false
     */
    public boolean existObject(String objectName) {
        return getObject(objectName) == null;
    }

    /**
     * 删除对象
     */
    public boolean removeObject(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build());
            return true;
        } catch (Exception e) {
            log.info("文件删除失败，文件名：{}", objectName);
            log.info(e.getMessage());
        }
        return false;
    }

    public StatObjectResponse getStatObject(String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build());
    }

    public boolean addObject(MultipartFile object, String objectName) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(object.getInputStream(), object.getSize(), -1)
                    .contentType(object.getContentType()).build());
            return true;
        } catch (Exception e) {
            log.info("文件删除失败，文件名：{}", objectName);
            log.info(e.getMessage());
        }
        return false;
    }

    public void downLoadObj(String objectName) {

        try {
            minioClient.downloadObject(DownloadObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).filename("test").build());
        } catch (Exception e) {
            log.info("文件删除失败，文件名：{}", objectName);
            log.info(e.getMessage());
        }
    }

}
