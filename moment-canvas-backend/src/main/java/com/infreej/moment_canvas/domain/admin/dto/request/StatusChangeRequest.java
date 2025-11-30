package com.infreej.moment_canvas.domain.admin.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 유저 상태 변경을 위한 Request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusChangeRequest {

    private long userId; // 유저 PK
    private Status status; // 유저 상태
}
