package com.commutertea.notification;

import com.commutertea.notification.dto.NotificationResponse;
import com.commutertea.notification.dto.UnreadResponse;
import com.commutertea.post.Post;
import com.commutertea.post.PostRepository;
import com.commutertea.user.User;
import com.commutertea.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationWebSocketHandler socketHandler;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repository,
                               NotificationWebSocketHandler socketHandler,
                               PostRepository postRepository,
                               UserRepository userRepository) {
        this.repository = repository;
        this.socketHandler = socketHandler;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> list(User user, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 30));
        return repository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public UnreadResponse unread(User user) {
        return new UnreadResponse(repository.countByRecipientIdAndReadFalse(user.getId()));
    }

    @Transactional
    public void markRead(User user, Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("消息不存在"));
        if (!user.getId().equals(notification.getRecipientId())) {
            throw new IllegalArgumentException("无权操作这条消息");
        }
        notification.setRead(true);
    }

    @Transactional
    public void markAllRead(User user) {
        repository.findByRecipientIdOrderByCreatedAtDesc(user.getId()).stream()
                .filter(n -> !n.isRead())
                .forEach(n -> n.setRead(true));
    }

    /** 给帖子作者发点赞/评论通知（自己操作也发）。匿名帖跳过（作者显示为匿名茶客）。 */
    @Transactional
    public void notifyPostInteraction(Long postId, String type, User actor, String snippet) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || post.isAnonymous()) return;
        Long recipientId = parseAuthorId(post.getGuestId());
        if (recipientId == null) return;

        String actorName = actor.getNickname() == null || actor.getNickname().isBlank()
                ? actor.getUsername() : actor.getNickname();
        String title;
        String content;
        if (Notification.TYPE_LIKE.equals(type)) {
            title = "有人赞了你的帖子";
            content = actorName + " 赞了你发的「" + brief(post.getContent()) + "」";
        } else {
            title = "有人评论了你的帖子";
            content = actorName + " 评论了「" + brief(post.getContent()) + "」：" + brief(snippet);
        }
        saveAndPush(recipientId, type, title, content, postId);
    }

    /** 后台发官方消息：userId 为空则广播给所有用户。 */
    @Transactional
    public int sendOfficial(String title, String content, Long userId) {
        List<Long> recipients = userId == null
                ? userRepository.findAll().stream().map(User::getId).toList()
                : List.of(userId);
        for (Long recipient : recipients) {
            saveAndPush(recipient, Notification.TYPE_OFFICIAL, title, content, null);
        }
        return recipients.size();
    }

    private void saveAndPush(Long recipientId, String type, String title, String content, Long postId) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setPostId(postId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        repository.save(notification);
        socketHandler.push(recipientId, NotificationResponse.from(notification));
    }

    private Long parseAuthorId(String guestId) {
        if (guestId == null || !guestId.startsWith("user_")) return null;
        try {
            return Long.valueOf(guestId.substring(5));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String brief(String text) {
        if (text == null) return "";
        String trimmed = text.trim();
        return trimmed.length() <= 24 ? trimmed : trimmed.substring(0, 24) + "…";
    }
}
