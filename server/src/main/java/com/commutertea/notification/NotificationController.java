package com.commutertea.notification;

import com.commutertea.post.dto.PageResponse;
import com.commutertea.notification.dto.NotificationResponse;
import com.commutertea.notification.dto.UnreadResponse;
import com.commutertea.user.User;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<NotificationResponse> list(
            @RequestAttribute("currentUser") User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<NotificationResponse> result = service.list(user, page, size);
        return new PageResponse<>(
                result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.hasNext()
        );
    }

    @GetMapping("/unread-count")
    public UnreadResponse unread(@RequestAttribute("currentUser") User user) {
        return service.unread(user);
    }

    @PostMapping("/{id}/read")
    public void markRead(@RequestAttribute("currentUser") User user, @PathVariable Long id) {
        service.markRead(user, id);
    }

    @PostMapping("/read-all")
    public void markAllRead(@RequestAttribute("currentUser") User user) {
        service.markAllRead(user);
    }
}
