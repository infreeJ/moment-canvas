package com.infreej.moment_canvas.domain.email.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId; // 고유 번호

    @Column
    private String email;

    @Column
    private String verificationCode;

    @Column
    private LocalDateTime expiresAt;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴 사용 시 기본값을 쓰라고 명시
    private IsVerified isVerified = IsVerified.N;



}
