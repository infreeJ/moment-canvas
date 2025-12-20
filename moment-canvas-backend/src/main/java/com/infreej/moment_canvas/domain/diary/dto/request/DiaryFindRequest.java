package com.infreej.moment_canvas.domain.diary.dto.request;

import com.infreej.moment_canvas.global.entity.YesOrNo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryFindRequest {
    YesOrNo yesOrNo;
    String yearMonth;
}
