package com.infreej.moment_canvas.controller;

import com.infreej.moment_canvas.common.code.SuccessCode;
import com.infreej.moment_canvas.common.response.SuccessResponse;
import com.infreej.moment_canvas.common.util.MessageUtil;
import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.response.SignupResponse;
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


    @Operation(summary = "유저 회원가입", description = "회원 가입 요청 \n - loginId, pwd를 제외한 나머지는 null 가능")
    @PostMapping("/user")
    public ResponseEntity<SuccessResponse<SignupResponse>> signup(@RequestBody SignupRequest signupRequest) {

        SignupResponse userResponse = userService.signup(signupRequest);
        String code = SuccessCode.USER_CREATED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.USER_CREATED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, userResponse));
    }

}
