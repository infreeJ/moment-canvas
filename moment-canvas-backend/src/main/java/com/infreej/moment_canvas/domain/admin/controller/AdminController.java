package com.infreej.moment_canvas.domain.admin.controller;

import com.infreej.moment_canvas.domain.admin.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.admin.service.AdminService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @SetSuccess(SuccessCode.USER_STATUS_CHANGE)
    @Operation(summary = "유저 상태 변경", security = @SecurityRequirement(name = "JWT"), description = "유저 정보 변경 API 입니다. \n - 활성화, 비활성화")
    @PatchMapping("/user/status")
    public void statusChange(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody StatusChangeRequest statusChangeRequest) {

        adminService.statusChange(customUserDetails.getUser().getRole(), statusChangeRequest);
    }
}
