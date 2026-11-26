package com.yong.service;

import java.time.LocalDateTime;
import java.util.List;

import com.yong.pojo.LikeResult;
import com.yong.pojo.Post;

public interface PostService {

    // 发布动态
    String insert(Post post,String token);

    // 删除动态
    boolean delete(String postId, String token);

    // 分页的广场列表
    List<Post> squarePostList(Integer size, LocalDateTime lastCreatedAt, String token);

    // 分页的关注列表
    List<Post> followingPostList(Integer size, LocalDateTime lastCreatedAt, String token);

    // 分页的附近帖子列表(返回的Post里只有id,authorId,authorInfo,title,content,images,location,likesNum,status)
    List<Post> nearbyPostList(Integer size, LocalDateTime lastCreatedAt, String token, Double distance);

    // 搜索帖子名称
    List<Post> search(Integer size, LocalDateTime lastCreatedAt, String token, String keyword);

    // 点赞/取消点赞
    LikeResult like(String postId, String token, Integer action);

    // 帖子详情
    Post showDetails(String token, String id);

    // 个人（别人）主页帖子列表
    List<Post> myPostList(Integer size, LocalDateTime lastCreatedAt, String token,String userId);

    // 添加评论
    Post.PostComment addComment(String token, String id, String content);

    // 删除评论
    Boolean deleteComment(String token, String id, String commentId);

}
