package com.yong.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.LikeResult;
import com.yong.pojo.LocationRequst;
import com.yong.pojo.Post;
import com.yong.pojo.Result;
import com.yong.service.PostService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    // 发布动态
    @PostMapping
    public Result insert(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        log.info("发布动态：{}", post);
        String postId = postService.insert(post, token);
        return Result.success(postId);
    }

    // 更新帖子位置（传入帖子id，经度、纬度）
    @PutMapping("/location")
    public Result updateLocationById(@RequestBody LocationRequst locationRequst) {
        log.info("locationRequst:{}", locationRequst);
        postService.updateLocationById(locationRequst.getPostId(), locationRequst.getLocationName(), locationRequst.getLongitude(), locationRequst.getLatitude());
        return Result.success();
    }

    // 删除动态
    @PostMapping("/delete/{postId}")
    public Result delete(@PathVariable String postId, @RequestHeader("Authorization") String token) {
        log.info("删除动态：postId:{},token:{}", postId, token);
        if (postService.delete(postId, token)) {
            return Result.success();
        }
        return Result.error("没有删除权限！");
    }

    // 分页的广场动态列表(返回的Post里只有id,authorId,authorInfo,title,content,images,location,likesNum,status)
    @GetMapping("/square")
    public Result squarePostList(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestHeader("Authorization") String token) {
        List<Post> rows = postService.squarePostList(size, lastCreatedAt, token);
        return Result.success(rows);
    }

    // 分页的关注列表(返回的Post里只有id,authorId,authorInfo,title,content,images,location,likesNum,status)
    @GetMapping("/following")
    public Result followingPostList(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestHeader("Authorization") String token) {
        List<Post> rows = postService.followingPostList(size, lastCreatedAt, token);
        return Result.success(rows);
    }

    // 分页的附近帖子列表(返回的Post里只有id,authorId,authorInfo,title,content,images,location,likesNum,status)
    @GetMapping("/nearby")
    public Result nearbyPostList(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "5000") Double distance,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestHeader("Authorization") String token) {
        List<Post> rows = postService.nearbyPostList(size, lastCreatedAt, token, distance);
        return Result.success(rows);
    }

    // 搜索帖子名称
    @GetMapping("/search")
    public Result search(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {

        if (!StringUtils.hasText(keyword)) {
            return Result.success(Collections.emptyList());
        }
        List<Post> rows = postService.search(size, lastCreatedAt, token, keyword);
        return Result.success(rows);
    }

    // 点赞/取消点赞(0=like/1=cancel)
    @PostMapping("/{postId}/like")
    public Result like(@PathVariable String postId,
            @RequestHeader("Authorization") String token,
            @RequestBody Integer action) {
        LikeResult likeResult = postService.like(postId, token, action);
        return Result.success(likeResult);
    }

    // 帖子详情
    @GetMapping("/{id}")
    public Result showDetails(@RequestHeader("Authorization") String token,
            @PathVariable String id) {
        Post post = postService.showDetails(token, id);
        if (post == null) {
            return Result.error("没有访问权限！");
        }
        return Result.success(post);
    }

    // 个人（别人）主页帖子列表
    @GetMapping("/myPost/{userId}")
    public Result myPost(
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestHeader("Authorization") String token,
            @PathVariable String userId) {

        log.info("userId:{}", userId);
        List<Post> rows = postService.myPostList(size, lastCreatedAt, token, userId);
        return Result.success(rows);
    }

    // 添加评论
    @PostMapping("/{id}/comment")
    public Result addComment(@RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody String content) {
        Post.PostComment comment = postService.addComment(token, id, content);
        if (comment == null) {
            return Result.error("用户或帖子不存在！");
        }
        return Result.success(comment);
    }

    // 删除评论
    @DeleteMapping("/{id}/comment/{commentId}")
    public Result deleteComment(@RequestHeader("Authorization") String token,
            @PathVariable String id,
            @PathVariable String commentId) {
        Boolean isSuccess = postService.deleteComment(token, id, commentId);
        if (!isSuccess) {
            return Result.error("没有删除权限！");
        }
        return Result.success();
    }

}
