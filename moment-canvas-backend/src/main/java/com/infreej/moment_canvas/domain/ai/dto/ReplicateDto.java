package com.infreej.moment_canvas.domain.ai.dto;

import java.util.List;

public class ReplicateDto {

    public record ReplicateRequest(ReplicateInput input) {}

    public record ReplicateInput(
            String prompt,
            String aspect_ratio, // "1:1", "16:9", "2:3" 등
            String output_format, // "webp", "jpg"
            int output_quality,
            boolean go_fast // Flux.1 Schnell 전용 옵션
    ) {}

    public record ReplicateResponse(
            String id,
            List<String> output,
            String status
    ) {}
}
