package com.commutertea.post;

import com.commutertea.post.dto.DraftResponse;
import com.commutertea.post.dto.PostResponse;
import com.commutertea.post.dto.SaveDraftRequest;
import com.commutertea.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/** 浏览历史、我的发帖、草稿箱。 */
@Service
public class PostLibraryService {

    private final PostRepository postRepository;
    private final PostHistoryRepository historyRepository;
    private final PostDraftRepository draftRepository;

    public PostLibraryService(PostRepository postRepository,
                              PostHistoryRepository historyRepository,
                              PostDraftRepository draftRepository) {
        this.postRepository = postRepository;
        this.historyRepository = historyRepository;
        this.draftRepository = draftRepository;
    }

    @Transactional
    public void recordView(Long postId, User user) {
        if (!postRepository.existsById(postId)) {
            return;
        }
        PostHistory history = historyRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseGet(PostHistory::new);
        if (history.getId() == null) {
            history.setPostId(postId);
            history.setUserId(user.getId());
        }
        history.setViewedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> history(User user) {
        List<Long> postIds = historyRepository.findByUserIdOrderByViewedAtDesc(user.getId()).stream()
                .map(PostHistory::getPostId)
                .toList();
        if (postIds.isEmpty()) {
            return List.of();
        }
        return postRepository.findAllById(postIds).stream()
                .map(PostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> myPosts(User user) {
        return postRepository.findByGuestIdOrderByCreatedAtDesc("user_" + user.getId()).stream()
                .map(PostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<DraftResponse> drafts(User user) {
        return draftRepository.findByUserIdOrderByUpdatedAtDesc(user.getId()).stream()
                .map(DraftResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DraftResponse draft(Long id, User user) {
        return DraftResponse.from(requireOwnedDraft(id, user));
    }

    @Transactional
    public DraftResponse saveDraft(Long id, User user, SaveDraftRequest request) {
        PostDraft draft = id == null ? new PostDraft() : requireOwnedDraft(id, user);
        if (id == null) {
            draft.setUserId(user.getId());
        }
        draft.setContent(request.content() == null ? "" : request.content().trim());
        draft.setCategory(request.category() == null ? "FRESH" : request.category());
        draft.setAnonymous(request.anonymous());
        draft.setImages(request.images() == null ? List.of() : request.images());
        draft.setUpdatedAt(LocalDateTime.now());
        draftRepository.save(draft);
        return DraftResponse.from(draft);
    }

    @Transactional
    public void deleteDraft(Long id, User user) {
        draftRepository.delete(requireOwnedDraft(id, user));
    }

    private PostDraft requireOwnedDraft(Long id, User user) {
        PostDraft draft = draftRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("草稿不存在"));
        if (!draft.getUserId().equals(user.getId())) {
            throw new NoSuchElementException("草稿不存在");
        }
        return draft;
    }
}
