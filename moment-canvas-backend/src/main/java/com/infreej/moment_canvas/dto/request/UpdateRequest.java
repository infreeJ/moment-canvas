package com.infreej.moment_canvas.dto.request;

import com.infreej.moment_canvas.entity.Gender;
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
}
