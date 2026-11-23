package com.yong.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.yong.pojo.AliyunOSSProperties;

@Component
public class AliyunOSSOperator {

    private final AliyunOSSProperties aliyunOSSProperties;

    AliyunOSSOperator(AliyunOSSProperties aliyunOSSProperties) {
        this.aliyunOSSProperties = aliyunOSSProperties;
    }

    public String upload(byte[] content, String originalFilename) throws Exception {
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();

        // 从系统环境变量读取 AK / SK
        CredentialsProvider provider = new EnvironmentVariableCredentialsProvider();

        // 获取当前系统日期，格式为yyyy/MM
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        // 生成一个新的不重复的文件名
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf((".")));
        String objectName = dir + "/" + newFileName;

        try (OSSClient client = OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(region)
                .endpoint(endpoint)
                .build()) {

            // 构建上传请求
            PutObjectRequest request = PutObjectRequest.newBuilder()
                    .bucket(bucketName)
                    .key(objectName)
                    // byte[] 转为SDK二进制数据
                    .body(BinaryData.fromBytes(content))
                    .build();

            PutObjectResult result = client.putObject(request);

            // 判断http状态码，200代表上传成功
            if (result.statusCode() != 200) {
                throw new RuntimeException(
                        "OSS上传失败，statusCode=" + result.statusCode() + ", requestId=" + result.requestId());
            }

            // 拼接完整访问URL返回
            // 格式: https://bucket.endpoint/对象key
            return String.format("https://%s.%s/%s", bucketName, endpoint.replace("https://", ""), objectName);
        }
    }

}
