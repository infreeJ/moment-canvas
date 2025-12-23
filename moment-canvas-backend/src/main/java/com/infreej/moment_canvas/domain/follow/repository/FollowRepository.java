package com.infreej.moment_canvas.domain.follow.repository;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 특정 유저의 팔로워 목록 조회
     */
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse(
                f.follower.userId, f.follower.nickname, f.follower.savedProfileImageName)
            FROM Follow f
            WHERE f.following.userId = :userId
            """)
    List<FollowerResponse> findFollowerList(Long userId);

    /**
     * 특정 유저의 팔로잉 목록 조회
     */
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse(
                f.following.userId, f.following.nickname, f.following.savedProfileImageName)
            FROM Follow f
            WHERE f.follower.userId = :userId
            """)
    List<FollowingResponse> findFollowingList(Long userId);

    /**
     * 팔로우 상태인지 확인
     */
    boolean existsByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);


    /**
     * 팔로우 엔티티 조회(팔로우 해제에서 사용)
     * @param followerId 팔로워 Id
     * @param followingId 팔로잉 Id
     */
    Optional<Follow> findByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);
}
