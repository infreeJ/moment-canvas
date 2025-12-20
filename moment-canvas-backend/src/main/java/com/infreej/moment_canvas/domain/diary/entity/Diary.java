package com.infreej.moment_canvas.domain.diary.entity;

import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.global.entity.BaseTimeEntity;
import com.infreej.moment_canvas.global.entity.yesOrNo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Lob
    private String content;

    @Column(nullable = false, columnDefinition = "INT CHECK (mood IN (1, 2, 3, 4, 5))")
    private int mood;

    @Column(length = 1000)
    private String orgDiaryImageName;

    @Column(length = 50)
    private String savedDiaryImageName;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false, length = 1)
    private yesOrNo isDeleted = yesOrNo.N;

    // 일기 엔티티 정보 변경 메서드
    public void updateDiaryInfo(String title, String content, Integer mood, LocalDate targetDate) {
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.targetDate = targetDate;
    }

    // 일기 엔티티 이미지 저장/변경 메서드
    public void updateDiaryImage(String orgDiaryImageName, String savedDiaryImageName) {
        this.orgDiaryImageName = orgDiaryImageName;
        this.savedDiaryImageName = savedDiaryImageName;
    }

    // 일기 논리 삭제 메서드
    public void updateDiaryDeleted() {
        this.isDeleted = yesOrNo.Y;
    }


}
