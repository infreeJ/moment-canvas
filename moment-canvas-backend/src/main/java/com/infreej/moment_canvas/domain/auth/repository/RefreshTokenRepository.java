package com.infreej.moment_canvas.domain.auth.repository;

import com.infreej.moment_canvas.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
