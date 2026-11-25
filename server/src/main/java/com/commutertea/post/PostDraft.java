package com.commutertea.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "post_drafts")
public class PostDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false)
    private boolean anonymous;

    @Column(columnDefinition = "TEXT")
    private String imageUrls;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public List<String> getImages() {
        if (imageUrls == null || imageUrls.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.stream(imageUrls.split("\n"))
                .map(String::trim).filter(url -> !url.isEmpty()).toList());
    }

    public void setImages(List<String> images) {
        this.imageUrls = images == null || images.isEmpty() ? "" : String.join("\n", images);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
