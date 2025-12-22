package com.infreej.moment_canvas.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowingResponse {

    private Long userId;
    private String nickname;
    private String savedProfileImageName;


}
