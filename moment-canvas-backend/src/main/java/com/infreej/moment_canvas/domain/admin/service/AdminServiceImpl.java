package com.infreej.moment_canvas.domain.admin.service;

import com.infreej.moment_canvas.domain.admin.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * 유저 상태 변경 메서드
     * @param role 권한 검증용 파라미터 (ADMIN만 접근 가능)
     * status = ACTIVE or INACTIVE or WITHDRAWAL
     */
    @Override
    @Transactional
    public void statusChange(Role role, StatusChangeRequest statusChangeRequest) {

        // 권한 검증
        if(!String.valueOf(role).equals("ADMIN")) {
            log.info("권한이 없습니다. role: {}", role);
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }

        long userId = statusChangeRequest.getUserId();

        // 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티 상태 수정
        user.statusChange(statusChangeRequest.getStatus());
    }
}
