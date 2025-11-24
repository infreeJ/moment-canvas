package com.infreej.moment_canvas.domain.diary.dto.request;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequest {

    private Long userId;
    private String title;
    private String content;
    private int mood;
    private String orgDiaryImageName;


    public Diary toEntity(User user) {
        return Diary.builder()
                .user(user)
                .title(this.title)
                .content(this.content)
                .mood(this.mood)
                .orgDiaryImageName(this.orgDiaryImageName)
                .build();
    }
}
