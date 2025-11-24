package com.infreej.moment_canvas.domain.user.controller;

import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.response.SuccessResponse;
import com.infreej.moment_canvas.global.util.MessageUtil;
import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.service.UserService;
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

    @Operation(summary = "유저 상태 변경", description = "유저 정보 변경 API 입니다. \n - 활성화, 비활성화")
    @PatchMapping("/user/status")
    public ResponseEntity<SuccessResponse<Void>> statusChange(@RequestBody StatusChangeRequest statusChangeRequest) {

        userService.statusChange(statusChangeRequest);

        String code = SuccessCode.USER_STATUS_CHANGE.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_STATUS_CHANGE.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg));
    }

    @Operation(summary = "유저 탈퇴 처리", description = "유저 탈퇴 처리 API 입니다.")
    @PatchMapping("/user/{userId}/withdrawal")
    public ResponseEntity<SuccessResponse<Void>> withdrawal(@PathVariable long userId) {

        userService.withdrawal(userId);

        String code = SuccessCode.USER_WITHDRAWAL.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_WITHDRAWAL.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg));
    }


}
