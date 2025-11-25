package com.infreej.moment_canvas.global.security;

import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * loginId를 이용해 사용자 정보를 조회
     */
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByLoginId(loginId);

        // 사용자가 존재하지 않을 경우 예외 throw
        User user = userOptional.orElseThrow(() -> {
            log.warn("사용자를 찾을 수 없습니다: username={}", loginId);
            return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId);
        });

        return new CustomUserDetails(user);
    }
}
