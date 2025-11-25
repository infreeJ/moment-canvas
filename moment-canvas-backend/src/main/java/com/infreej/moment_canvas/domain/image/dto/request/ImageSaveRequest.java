package com.infreej.moment_canvas.domain.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * org, saved로 가공된 이미지를 DB에 저장하기 위한 Request
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageSaveRequest {
    private String orgImageName;
    private String savedImageName;
}
