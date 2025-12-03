package com.yongwang.service.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.yongwang.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 阿里云OSS文件上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;

    /**
     * 上传文件
     *
     * @param file   文件
     * @param folder 文件夹名称
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String objectName = generateObjectName(folder, extension);

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, inputStream, metadata);
            ossClient.putObject(putObjectRequest);

            return urlPrefix + "/" + objectName;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 上传Base64图片
     *
     * @param base64Data Base64编码的图片数据
     * @param folder     文件夹名称
     * @return 文件访问URL
     */
    public String uploadBase64(String base64Data, String folder) {
        if (base64Data == null || base64Data.isEmpty()) {
            throw new BusinessException("上传数据不能为空");
        }

        // 移除Base64前缀（如果有）
        String pureBase64 = base64Data;
        String extension = "png";
        if (base64Data.contains(",")) {
            String[] parts = base64Data.split(",");
            pureBase64 = parts[1];
            // 从前缀中提取文件类型
            if (parts[0].contains("jpeg") || parts[0].contains("jpg")) {
                extension = "jpg";
            } else if (parts[0].contains("gif")) {
                extension = "gif";
            } else if (parts[0].contains("webp")) {
                extension = "webp";
            }
        }

        byte[] bytes = Base64.getDecoder().decode(pureBase64);
        String objectName = generateObjectName(folder, extension);

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(bytes.length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, inputStream, metadata);
            ossClient.putObject(putObjectRequest);

            return urlPrefix + "/" + objectName;
        } catch (Exception e) {
            log.error("Base64图片上传失败", e);
            throw new BusinessException("图片上传失败");
        }
    }

    /**
     * 批量上传文件
     *
     * @param files  文件列表
     * @param folder 文件夹名称
     * @return 文件访问URL列表
     */
    public List<String> batchUpload(List<MultipartFile> files, String folder) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(upload(file, folder));
        }
        return urls;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String objectName = fileUrl.replace(urlPrefix + "/", "");
            ossClient.deleteObject(bucket, objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
        }
    }

    /**
     * 生成对象名称
     */
    private String generateObjectName(String folder, String extension) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return folder + "/" + date + "/" + uuid + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "png";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
