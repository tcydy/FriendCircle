package com.yong.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import com.yong.pojo.LikeResult;
import com.yong.pojo.Post;

public interface PostCustomRepository {
    public List<Post> findSquarePostByCursor(Integer size, LocalDateTime lastCreatedAt, String currentUserId);

    public List<Post> findFollowingPostByCursor(Integer size, LocalDateTime lastCreatedAt, String currentUserId);

    public List<Post> findNearbyPostByCursor(Integer size, LocalDateTime lastCreatedAt, double distance,
            String currentUserId, double x, double y);

    public List<Post> search(Integer size, LocalDateTime lastCreatedAt, String userId, String keyword);

    public LikeResult like(String postId, String userId, Integer action);

    public List<Post> findMyPostByCursor(Integer size, LocalDateTime lastCreatedAt, String userId);
}
