package com.itheima.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.itheima.config.AliOSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class OssUtil {
    @Autowired
    private AliOSSProperties properties;
    public String upload(MultipartFile file, String folder) throws IOException {
        // 获取配置文件属性
        String accessKeyId = properties.getAccessKeyId();
        String endpoint = properties.getEndpoint();
        String bucketName = properties.getBucketName();
        String accessKeySecret = properties.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 防止上传头像文件重名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = folder + UUID.randomUUID() + suffix;

        // 上传文件流
        ossClient.putObject(bucketName, filename, file.getInputStream());

        // 返回可访问 URL
        return "https://" + bucketName + "." + endpoint + "/" + filename;

    }




}
