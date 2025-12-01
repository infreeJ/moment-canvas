package com.infreej.moment_canvas.domain.diary.dto.request;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 일기 저장 요청 Request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryCreateRequest {

    private String title;
    private String content;
    private int mood;


    // Entity 변환 메서드
    public Diary toEntity(User user) {
        return Diary.builder()
                .user(user)
                .title(this.title)
                .content(this.content)
                .mood(this.mood)
                .build();
    }
}
