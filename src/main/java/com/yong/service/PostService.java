package com.yong.service;

import java.time.LocalDateTime;
import java.util.List;

import com.yong.pojo.LikeResult;
import com.yong.pojo.Post;

public interface PostService {

    //发布动态
    String insert(Post post);

    //删除动态
    boolean delete(String postId, String token);

    // 分页的广场列表
    public List<Post> squarePostList(Integer size, LocalDateTime lastCreatedAt, String token);

    // 分页的关注列表
    List<Post> followingPostList(Integer size, LocalDateTime lastCreatedAt, String token);

    List<Post> nearbyPostList(Integer size, LocalDateTime lastCreatedAt, String token,Double distance);

    List<Post> search(Integer size, LocalDateTime lastCreatedAt, String token, String keyword);

    LikeResult like(String postId, String token, Integer action);

    Post showDetails(String token, String id);

    List<Post> myPostList(Integer size, LocalDateTime lastCreatedAt, String token);

    public Post.PostComment addComment(String token, String id, String content);

    Boolean deleteComment(String token, String id, String commentId);
    


}
