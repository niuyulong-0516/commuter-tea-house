package com.commutertea.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/** Cloudflare R2 存储（S3 兼容协议），适合部署环境。 */
@Service
@ConditionalOnProperty(name = "app.storage", havingValue = "r2")
public class R2FileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    private final S3Client s3Client;
    private final String bucket;
    private final String publicUrl;

    public R2FileStorageService(
            @Value("${app.r2.endpoint}") String endpoint,
            @Value("${app.r2.bucket}") String bucket,
            @Value("${app.r2.access-key}") String accessKey,
            @Value("${app.r2.secret-key}") String secretKey,
            @Value("${app.r2.public-url}") String publicUrl
    ) {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();
        this.bucket = bucket;
        this.publicUrl = publicUrl.endsWith("/") ? publicUrl : publicUrl + "/";
    }

    @Override
    public String store(MultipartFile file) {
        FileValidation.validate(file, ALLOWED_EXTENSIONS, MAX_FILE_SIZE);

        String extension = FileValidation.extensionOf(file.getOriginalFilename());
        String key = LocalDate.now().format(DATE_FORMAT) + "/"
                + UUID.randomUUID().toString().replace("-", "") + "." + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException error) {
            throw new IllegalStateException("图片上传失败", error);
        }

        return publicUrl + key;
    }
}
