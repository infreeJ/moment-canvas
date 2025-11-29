package com.infreej.moment_canvas.domain.admin.service;

import com.infreej.moment_canvas.domain.admin.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.entity.Role;

public interface AdminService {

    public void statusChange(Role role, StatusChangeRequest statusChangeRequest);
}
