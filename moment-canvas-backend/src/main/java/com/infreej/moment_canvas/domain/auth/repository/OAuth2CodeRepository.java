package com.infreej.moment_canvas.domain.auth.repository;

import com.infreej.moment_canvas.domain.auth.entity.OAuth2Code;
import org.springframework.data.repository.CrudRepository;

public interface OAuth2CodeRepository extends CrudRepository<OAuth2Code, String> {
}
