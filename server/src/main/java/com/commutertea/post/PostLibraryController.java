package com.commutertea.post;

import com.commutertea.post.dto.DraftResponse;
import com.commutertea.post.dto.PostResponse;
import com.commutertea.post.dto.SaveDraftRequest;
import com.commutertea.user.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class PostLibraryController {

    private final PostLibraryService postLibraryService;

    public PostLibraryController(PostLibraryService postLibraryService) {
        this.postLibraryService = postLibraryService;
    }

    @PostMapping("/history/{postId}")
    public void recordView(
            @PathVariable Long postId,
            @RequestAttribute("currentUser") User user
    ) {
        postLibraryService.recordView(postId, user);
    }

    @GetMapping("/history")
    public List<PostResponse> history(@RequestAttribute("currentUser") User user) {
        return postLibraryService.history(user);
    }

    @GetMapping("/my-posts")
    public List<PostResponse> myPosts(@RequestAttribute("currentUser") User user) {
        return postLibraryService.myPosts(user);
    }

    @GetMapping("/drafts")
    public List<DraftResponse> drafts(@RequestAttribute("currentUser") User user) {
        return postLibraryService.drafts(user);
    }

    @GetMapping("/drafts/{id}")
    public DraftResponse draft(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user
    ) {
        return postLibraryService.draft(id, user);
    }

    @PostMapping("/drafts")
    public DraftResponse createDraft(
            @RequestAttribute("currentUser") User user,
            @Valid @RequestBody SaveDraftRequest request
    ) {
        return postLibraryService.saveDraft(null, user, request);
    }

    @PutMapping("/drafts/{id}")
    public DraftResponse updateDraft(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user,
            @Valid @RequestBody SaveDraftRequest request
    ) {
        return postLibraryService.saveDraft(id, user, request);
    }

    @DeleteMapping("/drafts/{id}")
    public void deleteDraft(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User user
    ) {
        postLibraryService.deleteDraft(id, user);
    }
}
