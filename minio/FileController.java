package com.alibaba.asp.web.controller;

import com.alibaba.asp.response.RestResponse;
import com.alibaba.asp.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
@Api(tags = "文件接口")
@RestController
@RequestMapping("/v1/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "下载文件")
    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response, @RequestParam(value = "fileName", required = true) String fileName) {
        fileService.downloadFile(response, fileName);
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("upload")
    public RestResponse<Boolean> uploadFiles(@RequestParam(name = "files", required = true) MultipartFile[] files) {
        fileService.uploadFiles(files);
        return RestResponse.success(true);
    }

    @ApiOperation(value = "删除文件")
    @GetMapping("delete")
    public RestResponse<Boolean> deleteFile(String fileId) {
        return RestResponse.success(fileService.deleteFile(fileId));
    }

}
