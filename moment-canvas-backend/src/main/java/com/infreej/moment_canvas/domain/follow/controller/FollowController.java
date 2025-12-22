package com.infreej.moment_canvas.domain.follow.controller;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.follow.service.FollowService;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Follow", description = "팔로우 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class FollowController {

    private final FollowService followService;

    /**
     * 특정 유저의 follower 목록 조회
     */
    @SetSuccess(SuccessCode.FOLLOW_FOLLOWER_SUCCESS)
    @Operation(summary = "팔로워 조회", security = @SecurityRequirement(name = "JWT"), description = "팔로워 조회 API 입니다.")
    @GetMapping("/follower")
    public List<FollowerResponse> findFollowerList(Long userId) {

        return followService.findFollowerList(userId);
    }

    /**
     * 특정 유저의 following 목록 조회
     */
    @SetSuccess(SuccessCode.FOLLOW_FOLLOWER_SUCCESS)
    @Operation(summary = "팔로잉 조회", security = @SecurityRequirement(name = "JWT"), description = "팔로잉 조회 API 입니다.")
    @GetMapping("/following")
    public List<FollowingResponse> findFollowingList(Long userId) {

        return followService.findFollowingList(userId);
    }
}
