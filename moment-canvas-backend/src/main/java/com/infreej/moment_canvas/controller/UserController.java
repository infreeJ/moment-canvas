package com.infreej.moment_canvas.controller;

import com.infreej.moment_canvas.common.code.SuccessCode;
import com.infreej.moment_canvas.common.response.SuccessResponse;
import com.infreej.moment_canvas.common.util.MessageUtil;
import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.request.UpdateRequest;
import com.infreej.moment_canvas.dto.response.UserResponse;
import com.infreej.moment_canvas.entity.User;
import com.infreej.moment_canvas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "유저 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageUtil messageUtil;


    @Operation(summary = "유저 회원가입", description = "유저 가입 요청 API 입니다. \n - loginId, pwd를 제외한 나머지는 null 가능")
    @PostMapping("/user")
    public ResponseEntity<SuccessResponse<UserResponse>> signup(@RequestBody SignupRequest signupRequest) {

        UserResponse userResponse = userService.signup(signupRequest);
        String code = SuccessCode.USER_CREATED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_CREATED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, userResponse));
    }

    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회 API 입니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponse<UserResponse>> findById(@PathVariable long userId) {

        UserResponse userResponse = userService.findById(userId);

        String code = SuccessCode.USER_SUCCESS.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_SUCCESS.getMessageKey());
        return ResponseEntity.ok(SuccessResponse.of(code, msg, userResponse));
    }

    @Operation(summary = "유저 정보 변경", description = "유저 정보 변경 API 입니다. \n - 변경이 필요한 정보만 작성하세요.")
    @PatchMapping("/user")
    public ResponseEntity<SuccessResponse<UserResponse>> update(@RequestBody UpdateRequest updateRequest) {

        UserResponse userResponse = userService.update(updateRequest);
        String code = SuccessCode.USER_UPDATED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_UPDATED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, userResponse));
    }

}
