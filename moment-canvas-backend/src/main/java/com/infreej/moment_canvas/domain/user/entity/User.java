package com.infreej.moment_canvas.domain.user.entity;

import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 고유번호

    @Column(nullable = true, length = 30, unique = true) // 일반회원은 not null
    private String loginId; // 아이디

    @Column(length = 200)  // 일반회원은 not null
    private String pwd; // 비밀번호

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Column
    private LocalDate birthday; // 생년월일

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별 (MALE, FEMALE)

    @Column(length = 250)
    private String persona; // 사용자 특징

    @Column(length = 1000)
    private String orgProfileImageName; // 원본 프로필 이미지명

    @Column(length = 50)
    private String savedProfileImageName; // 저장된 프로필 이미지명

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴 사용 시 기본값을 쓰라고 명시
    private Status status = Status.ACTIVE; // 유저 상태: (ACTIVE, INACTIVE, WITHDRAWAL)

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴 사용 시 기본값을 쓰라고 명시
    private Role role = Role.USER; // 권한: (USER, VIP, ADMIN)

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴 사용 시 기본값을 쓰라고 명시
    private Provider provider = Provider.NONE; // 가입 종류 (GOOGLE, KAKAO, NONE)

    @Column(length = 50)
    private String providerId; // 소셜에서 준 식별값


    /**
     * 유저 엔티티 정보 변경 메서드
     * 각 값들은 상태값으로 관리되기 때문에 null로 들어오지 않으므로 null 처리가 필요없다.
     * @param updateRequest 유저 수정 요청 정보
     */
    public void updateUserInfo(UpdateRequest updateRequest) {
        this.birthday = updateRequest.getBirthday();
        this.gender = updateRequest.getGender();
        this.persona = updateRequest.getPersona();
    }

    // 프로필 이미지 변경 메서드
    public void updateUserProfileImage(ImageSaveRequest imageSaveRequest) {
        this.orgProfileImageName = imageSaveRequest.getOrgImageName();
        this.savedProfileImageName = imageSaveRequest.getSavedImageName();
    }

    // 유저 엔티티 상태 변경 메서드
    public void statusChange(Status status) {
        this.status = status;
    }
    
    // 유저 엔티티 탈퇴 처리 메서드
    public void withdrawal() {
        this.status = Status.WITHDRAWAL;
    }

}
