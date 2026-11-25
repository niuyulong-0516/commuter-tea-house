package com.commutertea.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CreatePostRequest {

    @NotBlank(message = "内容不能为空")
    @Size(max = 500, message = "内容最多 500 个字")
    private String content;

    @Pattern(regexp = "FRESH|RANT", message = "分类只能是 FRESH 或 RANT")
    private String category = "FRESH";

    private boolean anonymous = true;

    @Size(max = 9, message = "最多上传 9 张图片")
    private List<String> images = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
