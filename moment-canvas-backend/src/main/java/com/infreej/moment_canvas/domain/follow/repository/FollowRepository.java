package com.infreej.moment_canvas.domain.follow.repository;

import com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse;
import com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse;
import com.infreej.moment_canvas.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Long, Follow> {

    /**
     * 특정 유저의 팔로워 조회
     */
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.follow.dto.response.FollowerResponse(f.follower.userId, f.follower.nickname, f.follower.savedProfileImageName)
            FROM Follow f
            WHERE f.following.userId = :userId
            """)
    List<FollowerResponse> findFollowerList(Long userId);

    /**
     * 특정 유저의 팔로잉 조회
     */
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.follow.dto.response.FollowingResponse(f.f.follower.userId, f.follower.nickname, f.follower.savedProfileImageName)
            FROM Follow f
            WHERE f.follower.userId = :userId
            """)
    List<FollowingResponse> findFollowingList(Long userId);
}
