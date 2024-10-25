package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.sky.properties.AliOSSProperties;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class AliOSSUtils {
    AliOSSProperties aliOSSProperties;

    public AliOSSUtils(AliOSSProperties aliOSSProperties) {
        this.aliOSSProperties = aliOSSProperties;
    }


    //上传文件到阿里云OSS
    public String upLoad(MultipartFile file) throws Exception {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        //@Value("${aliyun.oss.endpoint}")
        String endpoint = aliOSSProperties.getEndpoint();

        // 填写Bucket名称
        //@Value("${aliyun.oss.bucketName}")
        String bucketName = aliOSSProperties.getBucketName();

        // 填写Object完整路径，即为存储在OSS上的文件名
        //截取原始名的文件后缀
        String originalFilename = file.getOriginalFilename();
        String lastName = null;
        if (originalFilename != null) {
            lastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //UUID生成文件名
        String objectName = UUID.randomUUID() + lastName;

        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        //新版工具包从环境变量中获取访问凭证，因此无需配置后两项
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
        try {
            System.out.println(bucketName + " " + endpoint);
            ossClient.putObject(bucketName, objectName, file.getInputStream());
        }
        //异常处理
        catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        //截取文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
        // 关闭ossClient
        ossClient.shutdown();
        return url;// 把上传到oss的路径返回
    }
}
