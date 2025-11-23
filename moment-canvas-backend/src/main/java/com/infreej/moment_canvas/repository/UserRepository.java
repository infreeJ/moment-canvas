package com.infreej.moment_canvas.repository;

import com.infreej.moment_canvas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
