package com.infreej.moment_canvas.domain.follow.service;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.user.entity.User;

import java.util.List;

public interface FollowService {

    List<FollowerResponse> findFollowerList(Long userId);

    List<FollowingResponse> findFollowingList(Long userId);

    void follow(Long userId, Long targetUserId);

    void unfollow(Long userId, Long targetUserId);

    boolean existsMutualFollow(Long userIdA, Long userIdB);
}
