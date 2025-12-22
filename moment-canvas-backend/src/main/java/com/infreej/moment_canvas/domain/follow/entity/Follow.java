package com.infreej.moment_canvas.domain.follow.entity;

import com.infreej.moment_canvas.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "follow",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_follow_follower_following",
                    columnNames = {"follower", "following"} // 중복 팔로우 방지
            )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User following;
}
