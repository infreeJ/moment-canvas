package com.infreej.moment_canvas.domain.user.repository;

import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;
import com.infreej.moment_canvas.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserCharacteristic> findByUserId(long userId);
}
