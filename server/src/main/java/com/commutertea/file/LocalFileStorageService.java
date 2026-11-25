package com.commutertea.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** 本地文件存储（默认，适合本地开发）。 */
@Service
@ConditionalOnProperty(name = "app.storage", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    private final Path root;

    public LocalFileStorageService(@Value("${app.upload-dir:./uploads}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    @Override
    public String store(MultipartFile file) {
        FileValidation.validate(file, ALLOWED_EXTENSIONS, MAX_FILE_SIZE);

        String extension = FileValidation.extensionOf(file.getOriginalFilename());
        String relativeDir = LocalDate.now().format(DATE_FORMAT);
        Path targetDir = root.resolve(relativeDir);
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        try {
            Files.createDirectories(targetDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException error) {
            throw new IllegalStateException("图片保存失败", error);
        }

        return "/uploads/" + relativeDir + "/" + filename;
    }
}
