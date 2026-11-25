package com.commutertea.common;

import com.commutertea.common.dto.AgreementResponse;
import com.commutertea.common.dto.UpdateAgreementRequest;
import com.commutertea.user.User;
import com.commutertea.user.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/agreements")
public class AgreementController {

    private final AgreementRepository agreementRepository;

    public AgreementController(AgreementRepository agreementRepository) {
        this.agreementRepository = agreementRepository;
    }

    /** 获取协议内容，免登录。code 取 terms / privacy。 */
    @GetMapping("/{code}")
    public AgreementResponse get(@PathVariable String code) {
        Agreement agreement = agreementRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("协议不存在"));
        return AgreementResponse.from(agreement);
    }

    /** 后台更新协议内容。 */
    @PutMapping("/{code}")
    public AgreementResponse update(
            @PathVariable String code,
            @Valid @RequestBody UpdateAgreementRequest request,
            @org.springframework.web.bind.annotation.RequestAttribute("currentUser") User user
    ) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }
        Agreement agreement = agreementRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("协议不存在"));
        agreement.setContent(request.content());
        agreement.setUpdatedAt(LocalDateTime.now());
        agreementRepository.save(agreement);
        return AgreementResponse.from(agreement);
    }
}
