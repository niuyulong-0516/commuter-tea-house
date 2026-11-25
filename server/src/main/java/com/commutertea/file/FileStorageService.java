package com.commutertea.file;

import org.springframework.web.multipart.MultipartFile;

/** 图片存储抽象：本地文件与 Cloudflare R2 两种实现。 */
public interface FileStorageService {

    /** 保存图片并返回可访问的 URL（相对路径或完整 URL）。 */
    String store(MultipartFile file);
}
