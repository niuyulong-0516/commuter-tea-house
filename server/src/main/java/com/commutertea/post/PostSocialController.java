package com.commutertea.post;

import com.commutertea.post.dto.CommentResponse;
import com.commutertea.post.dto.CreateCommentRequest;
import com.commutertea.post.dto.InteractionStateResponse;
import com.commutertea.post.dto.InteractionToggleResponse;
import com.commutertea.post.dto.PageResponse;
import com.commutertea.post.dto.PostResponse;
import com.commutertea.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostSocialController {

    private final PostSocialService postSocialService;

    public PostSocialController(PostSocialService postSocialService) {
        this.postSocialService = postSocialService;
    }

    @GetMapping("/favorites")
    public List<PostResponse> favorites(@RequestAttribute("currentUser") User user) {
        return postSocialService.favorites(user);
    }

    @GetMapping("/{id}/interactions")
    public InteractionStateResponse interactions(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user
    ) {
        return postSocialService.state(id, user);
    }

    @PostMapping("/{id}/like")
    public InteractionToggleResponse like(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user
    ) {
        return postSocialService.toggle(id, user, InteractionType.LIKE);
    }

    @PostMapping("/{id}/favorite")
    public InteractionToggleResponse favorite(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user
    ) {
        return postSocialService.toggle(id, user, InteractionType.FAVORITE);
    }

    @GetMapping("/{id}/comments")
    public PageResponse<CommentResponse> comments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postSocialService.comments(id, page, size);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postSocialService.createComment(id, user, request));
    }
}
