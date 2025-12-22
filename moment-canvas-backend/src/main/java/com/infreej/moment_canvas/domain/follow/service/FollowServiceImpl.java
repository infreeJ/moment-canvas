package com.infreej.moment_canvas.domain.follow.service;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    /**
     * 특정 유저의 팔로워 조회
     */
    @Override
    public List<FollowerResponse> findFollowerList(Long userId) {

        return followRepository.findFollowerList(userId);
    }

    /**
     * 특정 유저의 팔로잉 조회
     */
    @Override
    public List<FollowingResponse> findFollowingList(Long userId) {

        return followRepository.findFollowingList(userId);
    }
}
