package com.commutertea.post;

import com.commutertea.common.TransportType;
import com.commutertea.post.dto.CreatePostRequest;
import com.commutertea.post.dto.PageResponse;
import com.commutertea.post.dto.PostResponse;
import com.commutertea.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PostService {

    private static final int MAX_PAGE_SIZE = 30;
    private static final int MAX_IMAGES = 9;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> list(String category, String transportType,
                                           String sort, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        PostCategory parsedCategory = parseCategory(category);
        TransportType parsedTransport = parseTransportType(transportType);

        Sort sortSpec = "hot".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Order.desc("viewCount"), Sort.Order.desc("createdAt"))
                : Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(safePage, safeSize, sortSpec);

        Page<Post> result;
        if (parsedCategory == null && parsedTransport == null) {
            result = postRepository.findAll(pageable);
        } else if (parsedCategory == null) {
            result = postRepository.findByTransportType(parsedTransport, pageable);
        } else if (parsedTransport == null) {
            result = postRepository.findByCategory(parsedCategory, pageable);
        } else {
            result = postRepository.findByCategoryAndTransportType(parsedCategory, parsedTransport, pageable);
        }

        List<PostResponse> items = result.getContent().stream()
                .map(PostResponse::from)
                .toList();

        return new PageResponse<>(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.hasNext()
        );
    }

    /** 按内容关键词搜索帖子，关键词为空时返回空列表。 */
    @Transactional(readOnly = true)
    public PageResponse<PostResponse> search(String keyword, int page, int size) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (safeKeyword.isEmpty()) {
            return new PageResponse<>(List.of(), safePage, safeSize, 0, false);
        }

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> result = postRepository.findByContentContainingIgnoreCase(safeKeyword, pageable);
        List<PostResponse> items = result.getContent().stream()
                .map(PostResponse::from)
                .toList();
        return new PageResponse<>(
                items, result.getNumber(), result.getSize(), result.getTotalElements(), result.hasNext()
        );
    }

    @Transactional
    public PostResponse detail(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("这杯茶暂时找不到了"));
        post.setViewCount(post.getViewCount() + 1);
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse create(CreatePostRequest request, User user) {
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("内容最多 500 个字");
        }

        PostCategory category = parseCategory(request.getCategory());
        if (category == null) {
            category = PostCategory.FRESH;
        }

        Post post = new Post();
        post.setContent(content);
        post.setCategory(category);
        // 帖子的出行类型跟随作者当前偏好；切换偏好只影响之后发的帖子。
        post.setTransportType(user.getTransportType());
        post.setAnonymous(request.isAnonymous());
        post.setGuestId("user_" + user.getId());
        post.setAuthorName(request.isAnonymous() ? "匿名茶客" : user.getUsername());
        post.setImages(normalizeImages(request.getImages()));
        post.setCreatedAt(LocalDateTime.now());
        post.setViewCount(0);
        postRepository.save(post);

        return PostResponse.from(post);
    }

    private PostCategory parseCategory(String category) {
        if (category == null || category.isBlank() || "ALL".equalsIgnoreCase(category)) {
            return null;
        }
        try {
            return PostCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("分类不正确");
        }
    }

    private TransportType parseTransportType(String transportType) {
        if (transportType == null || transportType.isBlank() || "ALL".equalsIgnoreCase(transportType)) {
            return null;
        }
        try {
            return TransportType.valueOf(transportType.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("出行类型不正确");
        }
    }

    private List<String> normalizeImages(List<String> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(url -> url.startsWith("/uploads/"))
                .distinct()
                .limit(MAX_IMAGES)
                .toList();
    }
}
