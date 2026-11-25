package com.commutertea.post;

import com.commutertea.common.TransportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostCategory category;

    /** 旧库中的历史帖子可能为空，读取时按轨道交通处理。 */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TransportType transportType;

    @Column(nullable = false)
    private boolean anonymous;

    @Column(nullable = false, length = 64)
    private String guestId;

    @Column(nullable = false, length = 40)
    private String authorName;

    /**
     * 图片地址用换行分隔保存，避免第一版为了图片单独建关联表。
     */
    @Column(columnDefinition = "TEXT")
    private String imageUrls;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private long viewCount;

    public List<String> getImages() {
        if (imageUrls == null || imageUrls.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.stream(imageUrls.split("\n"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .toList());
    }

    public void setImages(List<String> images) {
        this.imageUrls = images == null || images.isEmpty() ? "" : String.join("\n", images);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory category) {
        this.category = category;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}
