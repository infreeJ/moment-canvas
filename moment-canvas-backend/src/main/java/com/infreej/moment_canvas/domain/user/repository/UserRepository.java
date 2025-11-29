package com.infreej.moment_canvas.domain.user.repository;

import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;
import com.infreej.moment_canvas.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 유저의 특징만 조회하는 메서드 (일기 이미지 생성 용도)
    Optional<UserCharacteristic> findByUserId(long userId);

    // loginId로 유저 정보 조회
    Optional<User> findByLoginId(String loginId);

    // loginId가 있으면 true를 반환한다 (중복 확인 용도)
    boolean existsByLoginId(String loginId);
}
