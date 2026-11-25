package com.commutertea.common;

import com.commutertea.common.dto.CreateFeedbackRequest;
import com.commutertea.common.dto.FeedbackResponse;
import com.commutertea.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(
            @RequestAttribute("currentUser") User user,
            @Valid @RequestBody CreateFeedbackRequest request
    ) {
        Feedback feedback = new Feedback();
        feedback.setUserId(user.getId());
        feedback.setDescription(request.description().trim());
        feedback.setImages(request.images() == null ? java.util.List.of() : request.images());
        feedback.setWhatsapp(request.whatsapp() == null ? "" : request.whatsapp().trim());
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedbackResponse.from(feedback));
    }
}
