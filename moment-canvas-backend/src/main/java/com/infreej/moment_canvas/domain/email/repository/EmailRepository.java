package com.infreej.moment_canvas.domain.email.repository;

import com.infreej.moment_canvas.domain.email.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmail(String email);

    /**
     * 만료 시간이 지난 이메일 인증 데이터를 모두 조회
     * @param now 현재 시각
     */
    List<EmailVerification> findAllByExpiresAtBefore(LocalDateTime now);
}
