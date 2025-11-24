package com.infreej.moment_canvas.dto.request;

import com.infreej.moment_canvas.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusChangeRequest {

    private long userId;
    private Status status;
}
