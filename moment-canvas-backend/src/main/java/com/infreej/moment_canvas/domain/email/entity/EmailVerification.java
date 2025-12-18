package com.infreej.moment_canvas.domain.email.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId; // 고유 번호

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴 사용 시 기본값을 쓰라고 명시
    private IsVerified isVerified = IsVerified.N;


    /**
     * 인증 메일 재발송 시 코드, 만료시간, 인증 여부 초기화
     */
    public void updateVerificationMail(String code, LocalDateTime expiresAt) {
        this.verificationCode = code;
        this.expiresAt = expiresAt;
        this.isVerified = IsVerified.N;
    }

    /**
     * 인증 성공 시 인증 여부 Y 변경 메서드
     */
    public void certifyEmail() {
        this.isVerified = IsVerified.Y;
    }

}
