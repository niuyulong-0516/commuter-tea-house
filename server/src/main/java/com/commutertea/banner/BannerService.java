package com.commutertea.banner;

import com.commutertea.banner.dto.BannerResponse;
import com.commutertea.post.Post;
import com.commutertea.post.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BannerService {

    private static final int BANNER_COUNT = 5;

    private final PostRepository postRepository;

    public BannerService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /** 取浏览量最高的若干帖子作为 banner，有图取第一张，无图则 image 为 null。 */
    @Transactional(readOnly = true)
    public List<BannerResponse> list() {
        PageRequest pageable = PageRequest.of(0, BANNER_COUNT,
                Sort.by(Sort.Order.desc("viewCount"), Sort.Order.desc("createdAt")));
        return postRepository.findAll(pageable).getContent().stream()
                .map(this::toBanner)
                .toList();
    }

    private BannerResponse toBanner(Post post) {
        List<String> images = post.getImages();
        String image = images == null || images.isEmpty() ? null : images.get(0);
        return new BannerResponse(post.getId(), post.getContent(), image);
    }
}
