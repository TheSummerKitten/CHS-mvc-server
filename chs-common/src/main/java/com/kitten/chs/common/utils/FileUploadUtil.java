package com.kitten.chs.common.utils;

import com.kitten.chs.common.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    public static class UploadResult {
        private boolean success;
        private String message;
        private String url;
        private String objectName;

        public static UploadResult success(String url, String objectName) {
            UploadResult result = new UploadResult();
            result.success = true;
            result.url = url;
            result.objectName = objectName;
            return result;
        }

        public static UploadResult fail(String message) {
            UploadResult result = new UploadResult();
            result.success = false;
            result.message = message;
            return result;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getUrl() {
            return url;
        }

        public String getObjectName() {
            return objectName;
        }
    }

    public UploadResult validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return UploadResult.fail("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return UploadResult.fail("文件大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return UploadResult.fail("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return UploadResult.fail("文件格式不支持，仅支持 jpg、jpeg、png、gif、webp 格式");
        }

        return null;
    }

    public UploadResult uploadFile(MultipartFile file, String folder) {
        UploadResult validation = validateFile(file);
        if (validation != null) {
            return validation;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        try {
            String bucketName = minioConfig.getBucketName();
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            String objectName = folder + "/" + UUID.randomUUID().toString() + "." + extension;

            byte[] fileBytes = file.getBytes();
            long fileSize = fileBytes.length;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, fileSize, -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            String fileUrl = minioConfig.getEndpoint() + "/" + bucketName + "/" + objectName;
            return UploadResult.success(fileUrl, objectName);

        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            return UploadResult.fail("上传文件失败: " + e.getMessage());
        }
    }

    public UploadResult uploadAvatar(MultipartFile file) {
        return uploadFile(file, "avatar");
    }

    public UploadResult uploadFoodImage(MultipartFile file) {
        return uploadFile(file, "food");
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    public String getMinioEndpoint() {
        return minioConfig.getEndpoint();
    }

    public String getBucketName() {
        return minioConfig.getBucketName();
    }
}
