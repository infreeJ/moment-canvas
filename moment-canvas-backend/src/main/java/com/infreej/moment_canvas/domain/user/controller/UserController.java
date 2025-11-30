package com.infreej.moment_canvas.domain.user.controller;

import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.admin.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.service.UserService;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Users", description = "유저 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @SetSuccess(SuccessCode.USER_CREATED)
    @Operation(summary = "유저 회원가입", description = "유저 가입 요청 API 입니다. \n * loginId, pwd를 제외한 나머지는 null 가능 \n * 아이디는 영문과 숫자를 포함한 4~16자여야 하며, 영문은 필수입니다. \n 비밀번호는 영문 소문자, 대문자, 숫자, 특수문자를 모두 포함해야 하며 8~50자여야 합니다.")
    @PostMapping("/user")
    public UserResponse signup(@RequestBody @Valid SignupRequest signupRequest) {

        return userService.signup(signupRequest);
    }

    @SetSuccess(SuccessCode.USER_SUCCESS)
    @Operation(summary = "유저 정보 조회", security = @SecurityRequirement(name = "JWT"), description = "유저 정보 조회 API 입니다.")
    @GetMapping("/user")
    public UserResponse findById(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return userService.findUserById(customUserDetails.getUser().getUserId());
    }

    @SetSuccess(SuccessCode.USER_UPDATED)
    @Operation(summary = "유저 정보 변경", security = @SecurityRequirement(name = "JWT"), description = "유저 정보 변경 API 입니다. \n - 변경이 필요한 정보만 작성하세요.")
    @PatchMapping("/user")
    public UserResponse update(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UpdateRequest updateRequest) {

        return userService.update(customUserDetails.getUser().getUserId(), updateRequest);
    }


    @SetSuccess(SuccessCode.USER_UPDATED)
    @Operation(summary = "유저 프로필 이미지 변경", security = @SecurityRequirement(name = "JWT"), description = "프로필 이미지 변경 API 입니다.")
    @PatchMapping(value = "/user/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String profileImageUpdate(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam MultipartFile profileImage) throws IOException {

        return userService.profileImageUpdate(customUserDetails.getUser().getUserId(), profileImage);
    }


    @SetSuccess(SuccessCode.USER_WITHDRAWAL)
    @Operation(summary = "유저 탈퇴 처리", security = @SecurityRequirement(name = "JWT"), description = "유저 탈퇴 처리 API 입니다.")
    @PatchMapping("/user/withdrawal")
    public void withdrawal(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        userService.withdrawal(customUserDetails.getUser().getUserId());
    }


}
