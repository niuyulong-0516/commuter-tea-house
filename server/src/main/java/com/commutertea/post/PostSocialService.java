package com.commutertea.post;

import com.commutertea.notification.Notification;
import com.commutertea.notification.NotificationService;
import com.commutertea.post.dto.CommentResponse;
import com.commutertea.post.dto.CreateCommentRequest;
import com.commutertea.post.dto.InteractionStateResponse;
import com.commutertea.post.dto.InteractionToggleResponse;
import com.commutertea.post.dto.PageResponse;
import com.commutertea.post.dto.PostResponse;
import com.commutertea.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostSocialService {

    private final PostRepository postRepository;
    private final PostInteractionRepository interactionRepository;
    private final PostCommentRepository commentRepository;
    private final NotificationService notificationService;

    public PostSocialService(PostRepository postRepository,
                             PostInteractionRepository interactionRepository,
                             PostCommentRepository commentRepository,
                             NotificationService notificationService) {
        this.postRepository = postRepository;
        this.interactionRepository = interactionRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public InteractionStateResponse state(Long postId, User user) {
        requirePost(postId);
        return new InteractionStateResponse(
                exists(postId, user.getId(), InteractionType.LIKE),
                exists(postId, user.getId(), InteractionType.FAVORITE),
                interactionRepository.countByPostIdAndType(postId, InteractionType.LIKE),
                interactionRepository.countByPostIdAndType(postId, InteractionType.FAVORITE),
                commentRepository.countByPostId(postId)
        );
    }

    @Transactional
    public InteractionToggleResponse toggle(Long postId, User user, String type) {
        requirePost(postId);
        var existing = interactionRepository.findByPostIdAndUserIdAndType(postId, user.getId(), type);
        boolean active;
        if (existing.isPresent()) {
            interactionRepository.delete(existing.get());
            active = false;
        } else {
            PostInteraction interaction = new PostInteraction();
            interaction.setPostId(postId);
            interaction.setUserId(user.getId());
            interaction.setType(type);
            interaction.setCreatedAt(LocalDateTime.now());
            interactionRepository.save(interaction);
            active = true;
            // 点赞（收藏不算）产生通知
            if (InteractionType.LIKE.equals(type)) {
                notificationService.notifyPostInteraction(postId, Notification.TYPE_LIKE, user, null);
            }
        }
        return new InteractionToggleResponse(active, interactionRepository.countByPostIdAndType(postId, type));
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> comments(Long postId, int page, int size) {
        requirePost(postId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 30);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<CommentResponse> result = commentRepository
                .findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(CommentResponse::from);
        return new PageResponse<>(
                result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.hasNext()
        );
    }

    @Transactional
    public CommentResponse createComment(Long postId, User user, CreateCommentRequest request) {
        requirePost(postId);
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setAuthorName(user.getNickname() == null ? user.getUsername() : user.getNickname());
        comment.setContent(request.content().trim());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        notificationService.notifyPostInteraction(postId, Notification.TYPE_COMMENT, user, comment.getContent());
        return CommentResponse.from(comment);
    }

    /** 当前用户收藏的帖子，按收藏时间倒序。 */
    @Transactional(readOnly = true)
    public List<PostResponse> favorites(User user) {
        List<Long> postIds = interactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), InteractionType.FAVORITE)
                .stream().map(PostInteraction::getPostId).toList();
        if (postIds.isEmpty()) {
            return List.of();
        }
        return postRepository.findAllById(postIds).stream()
                .map(PostResponse::from).toList();
    }

    private boolean exists(Long postId, Long userId, String type) {
        return interactionRepository.findByPostIdAndUserIdAndType(postId, userId, type).isPresent();
    }

    private void requirePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("这杯茶暂时找不到了");
        }
    }
}
