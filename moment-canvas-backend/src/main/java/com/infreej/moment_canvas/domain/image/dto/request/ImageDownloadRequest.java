package com.infreej.moment_canvas.domain.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * URL 이미지를 로컬 폴더로 다운로드하기 위한 Request
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDownloadRequest {
    private String imageUrl;
    private ImageType imageType;
}
