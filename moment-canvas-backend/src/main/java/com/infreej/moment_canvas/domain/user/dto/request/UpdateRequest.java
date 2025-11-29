package com.infreej.moment_canvas.domain.user.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

    private LocalDate birthday;
    private Gender gender;
    private String persona;
}
