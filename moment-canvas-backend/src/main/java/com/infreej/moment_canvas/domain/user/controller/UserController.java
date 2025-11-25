package com.infreej.moment_canvas.domain.user.controller;

import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "유저 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @SetSuccess(SuccessCode.USER_CREATED)
    @Operation(summary = "유저 회원가입", description = "유저 가입 요청 API 입니다. \n - loginId, pwd를 제외한 나머지는 null 가능")
    @PostMapping("/user")
    public UserResponse signup(@RequestBody SignupRequest signupRequest) {

        return userService.signup(signupRequest);
    }

    @SetSuccess(SuccessCode.USER_SUCCESS)
    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회 API 입니다.")
    @GetMapping("/user/{userId}")
    public UserResponse findById(@PathVariable long userId) {

        return userService.findUserById(userId);
    }

    @SetSuccess(SuccessCode.USER_UPDATED)
    @Operation(summary = "유저 정보 변경", description = "유저 정보 변경 API 입니다. \n - 변경이 필요한 정보만 작성하세요.")
    @PatchMapping("/user")
    public UserResponse update(@RequestBody UpdateRequest updateRequest) {

        return userService.update(updateRequest);
    }

    @SetSuccess(SuccessCode.USER_STATUS_CHANGE)
    @Operation(summary = "유저 상태 변경", description = "유저 정보 변경 API 입니다. \n - 활성화, 비활성화")
    @PatchMapping("/user/status")
    public void statusChange(@RequestBody StatusChangeRequest statusChangeRequest) {

        userService.statusChange(statusChangeRequest);
    }

    @SetSuccess(SuccessCode.USER_WITHDRAWAL)
    @Operation(summary = "유저 탈퇴 처리", description = "유저 탈퇴 처리 API 입니다.")
    @PatchMapping("/user/{userId}/withdrawal")
    public void withdrawal(@PathVariable long userId) {

        userService.withdrawal(userId);
    }


}
