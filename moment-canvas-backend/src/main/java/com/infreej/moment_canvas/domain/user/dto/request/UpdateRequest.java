package com.infreej.moment_canvas.domain.user.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

    private Long userId;
    private Integer age;
    private Gender gender;
    private String persona;
    private String orgProfileImageName;
    private String savedProfileImageName;
}
