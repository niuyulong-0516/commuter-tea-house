package com.commutertea.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

/** 图片上传的公共校验逻辑。 */
public final class FileValidation {

    private FileValidation() {
    }

    public static void validate(MultipartFile file, Set<String> allowedExtensions, long maxFileSize) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("单张图片不能超过 10MB");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        if (!allowedExtensions.contains(extensionOf(file.getOriginalFilename()))) {
            throw new IllegalArgumentException("仅支持 jpg、jpeg、png、webp、gif 图片");
        }
    }

    public static String extensionOf(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
