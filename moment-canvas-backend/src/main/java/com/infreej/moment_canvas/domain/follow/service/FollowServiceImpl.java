package com.infreej.moment_canvas.domain.follow.service;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.follow.entity.Follow;
import com.infreej.moment_canvas.domain.follow.repository.FollowRepository;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 특정 유저의 팔로워 목록 조회
     */
    @Override
    @Transactional
    public List<FollowerResponse> findFollowerList(Long userId) {

        return followRepository.findFollowerList(userId);
    }

    /**
     * 특정 유저의 팔로잉 목록 조회
     */
    @Override
    @Transactional
    public List<FollowingResponse> findFollowingList(Long userId) {

        return followRepository.findFollowingList(userId);
    }

    /**
     * 팔로우 추가
     * @param userId 팔로워 Id
     * @param targetUserId 팔로잉 Id
     */
    @Override
    @Transactional
    public void follow(Long userId, Long targetUserId) {

        if(followRepository.existsByFollower_UserIdAndFollowing_UserId(userId, targetUserId)) {
            throw new BusinessException(ErrorCode.FOLLOW_ALREADY_FOLLOWING);
        }

        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    /**
     * 팔로우 해제
     * @param userId 팔로워 Id
     * @param targetUserId 팔로잉 Id
     */
    @Override
    @Transactional
    public void unfollow(Long userId, Long targetUserId) {

        Follow follow = followRepository.findByFollower_UserIdAndFollowing_UserId(userId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FOLLOW_NOT_FOLLOWING));

        followRepository.delete(follow);
    }
}
