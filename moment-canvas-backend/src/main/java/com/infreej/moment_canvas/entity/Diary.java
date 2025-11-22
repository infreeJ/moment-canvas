package com.infreej.moment_canvas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

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
}
