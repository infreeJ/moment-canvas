package com.infreej.moment_canvas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String loginId;

    @Column(nullable = false, length = 200)
    private String pwd;

    @Column
    private Integer age;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 250)
    private String persona;

    @Column(length = 1000)
    private String orgProfileImageName;

    @Column(length = 50)
    private String savedProfileImageName;
}
