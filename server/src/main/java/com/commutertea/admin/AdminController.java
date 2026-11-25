package com.commutertea.admin;

import com.commutertea.admin.dto.DashboardResponse;
import com.commutertea.common.Feedback;
import com.commutertea.common.FeedbackRepository;
import com.commutertea.post.Post;
import com.commutertea.post.PostCommentRepository;
import com.commutertea.post.PostRepository;
import com.commutertea.user.User;
import com.commutertea.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** 管理后台接口，全部经 AdminInterceptor 校验管理员权限。 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminController(UserRepository userRepository,
                           PostRepository postRepository,
                           PostCommentRepository commentRepository,
                           FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return new DashboardResponse(
                userRepository.count(),
                postRepository.count(),
                commentRepository.count(),
                feedbackRepository.count()
        );
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId).reversed())
                .map(user -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", user.getDisplayId());
                    item.put("username", user.getUsername());
                    item.put("nickname", user.getNickname());
                    item.put("role", user.getRole() == null ? "USER" : user.getRole().name());
                    item.put("transportType", user.getTransportType().name());
                    item.put("createdAt", user.getCreatedAt());
                    return item;
                }).toList();
    }

    @GetMapping("/posts")
    public List<Map<String, Object>> posts() {
        return postRepository.findAll().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", post.getId());
                    item.put("content", post.getContent());
                    item.put("category", post.getCategory().name());
                    item.put("transportType", post.getTransportType().name());
                    item.put("authorName", post.getAuthorName());
                    item.put("viewCount", post.getViewCount());
                    item.put("createdAt", post.getCreatedAt());
                    return item;
                }).toList();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/feedbacks")
    public List<Map<String, Object>> feedbacks() {
        return feedbackRepository.findAll().stream()
                .sorted(Comparator.comparing(Feedback::getCreatedAt).reversed())
                .map(feedback -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", feedback.getId());
                    item.put("description", feedback.getDescription());
                    item.put("images", feedback.getImages());
                    item.put("whatsapp", feedback.getWhatsapp());
                    item.put("userId", feedback.getUserId());
                    item.put("createdAt", feedback.getCreatedAt());
                    return item;
                }).toList();
    }

    @DeleteMapping("/feedbacks/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
