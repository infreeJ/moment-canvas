package com.infreej.moment_canvas.domain.email.service;

import com.infreej.moment_canvas.domain.email.dto.request.EmailRequest;
import com.infreej.moment_canvas.domain.email.dto.request.EmailVerificationRequest;
import com.infreej.moment_canvas.domain.email.entity.EmailVerification;
import com.infreej.moment_canvas.domain.email.entity.IsVerified;
import com.infreej.moment_canvas.domain.email.repository.EmailRepository;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    /**
     * 인증 메일 발송 메서드
     * @param emailRequest
     */
    @Override
    @Transactional
    public void sendVerificationMail(EmailRequest emailRequest) {

        String email = emailRequest.getEmail();

        // 중복 확인
        if(userRepository.existsByEmail(email)) {
            log.info("이미 사용 중인 이메일입니다. email: {}", email);
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }

        // 인증 코드 생성
        String code = createVerificationCode();

        // 만료 시간 정의
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // 이메일 인증 테이블에서 데이터 조회
        EmailVerification emailVerification = emailRepository.findByEmail(email).orElse(null);

        if(emailVerification != null) { // 이미 인증 데이터가 있다면
            log.info("이미 인증 데이터가 있으므로 기존 데이터를 업데이트합니다. email: {}", email);
            emailVerification.updateVerificationMail(code, expiresAt);

        } else { // 인증 데이터가 없다면
            log.info("인증 데이터가 없으므로 인증 데이터를 생성합니다. email: {}", email);
            EmailVerification newEmailVerification = EmailVerification.builder()
                    .email(email)
                    .verificationCode(code)
                    .expiresAt(expiresAt)
                    .isVerified(IsVerified.N)
                    .build();

            emailRepository.save(newEmailVerification);
        }

        // 인증 메일 발송
        try {
            MimeMessage mimeMessage = createVerificationMail(email, code);
            javaMailSender.send(mimeMessage);
            log.info("인증 메일 발송 완료: {}", email);
            log.info("인증 코드 번호: {}", code);
        } catch (Exception e) {
            log.error("인증 메일 발송 실패. email: {}", email);
            throw new RuntimeException(e);
        }

    }


    /**
     * 이메일 인증 코드 검증 메서드
     */
    @Override
    @Transactional
    public void checkVerificationEmailCode(EmailVerificationRequest emailVerificationRequest) {

        // 인증 데이터 조회
        EmailVerification emailVerification = emailRepository.findByEmail(emailVerificationRequest.getEmail()).orElse(null);

        // null 이거나 만료되었거나, 코드가 일치하지 않을 경우 throw
        if(emailVerification == null
                || emailVerification.getExpiresAt().isBefore(LocalDateTime.now())
                || !emailVerification.getVerificationCode().equals(emailVerificationRequest.getVerificationCode())) {

            log.info("인증 코드가 유효하지 않습니다. email: {}, code: {}", emailVerificationRequest.getEmail(), emailVerificationRequest.getVerificationCode());
            throw new BusinessException(ErrorCode.USER_INVALID_EMAIL_CODE);
        }

        // 인증 여부 Y로 변경
        emailVerification.certifyEmail();
    }


    /**
     * 인증 메일 생성 메서드
     */
    private MimeMessage createVerificationMail(String email, String code) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage(); // 메일 생성
        message.setFrom(senderEmail); // 이메일 발송처
        message.setRecipients(MimeMessage.RecipientType.TO, email); // 이메일 수신처
        message.setSubject("Moment Canvas 이메일 인증"); // 제목
        String body = "<h3>요청하신 인증 번호입니다.</h3><h1>" + code + "</h1><h3>감사합니다.</h3>"; // 내용
        message.setText(body, "UTF-8", "html");
        return message;
    }


    /**
     * 인증 코드 생성 메서드
     * @return
     */
    private String createVerificationCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
    
    
    // TODO: 만료된 이메일 인증 데이터 삭제 스케줄러 필요
}
