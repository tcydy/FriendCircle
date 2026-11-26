package com.yong.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.yong.pojo.Follow;
import com.yong.pojo.LikeResult;
import com.yong.pojo.Post;
import com.yong.pojo.Post.PostComment;
import com.yong.pojo.User;
import com.yong.repository.FollowRepository;
import com.yong.repository.PostRepository;
import com.yong.repository.UserRepository;
import com.yong.service.PostService;
import com.yong.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    PostServiceImpl(PostRepository postRepository, UserRepository userRepository, FollowRepository followRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    // 发布动态
    @Override
    public String insert(Post post, String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        User user = userRepository.findById((String) claims.get("id")).orElse(null);
        if (user == null)
            return null;
        post.setAuthorId(user.getId());

        Post.AuthorInfo authorInfo = new Post.AuthorInfo(user.getNickname(), user.getAvatar());
        post.setAuthorInfo(authorInfo);

        post.setCreatedAt(LocalDateTime.now());
        post.setLikesNum(0);
        List<String> likes = new ArrayList<>();
        post.setLikes(likes);

        List<PostComment> comments = new ArrayList<>();
        post.setComments(comments);

        Post post2 = postRepository.save(post);// 返回了id
        return post2.getId();
    }
    
    // 更新帖子位置（传入帖子id，经度、纬度）
    @Override 
    public void updateLocationById(String postId, String locationName, Double longitude, Double latitude) {
        postRepository.updateLocationById(postId,locationName, longitude, latitude);
    }

    // 删除动态
    @Override
    public boolean delete(String postId, String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Post post = postRepository.findById(postId).orElse(null);
        // 操作者与帖子作者不是同一人
        if (!Objects.equals(post.getAuthorId(), (String) claims.get("id"))) {
            return false;
        }
        postRepository.deleteById(postId);
        return true;
    }

    // 分页的广场列表
    @Override
    public List<Post> squarePostList(Integer size, LocalDateTime lastCreatedAt, String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        return postRepository.findSquarePostByCursor(size, lastCreatedAt, (String) claims.get("id"));
    }

    // 分页的关注列表
    @Override
    public List<Post> followingPostList(Integer size, LocalDateTime lastCreatedAt, String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        return postRepository.findFollowingPostByCursor(size, lastCreatedAt, (String) claims.get("id"));
    }

    // 分页的附近帖子列表(返回的Post里只有id,authorId,authorInfo,title,content,images,location,likesNum,status)
    @Override
    public List<Post> nearbyPostList(Integer size, LocalDateTime lastCreatedAt, String token, Double distance) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        User user = userRepository.findById((String) claims.get("id")).orElse(null);

        // 1. 参数安全兜底
        double safeDistance = (distance == null || distance <= 0) ? 5000 : Math.min(distance, 50000); // 最大 50km
        Integer safeSize = (size == null || size <= 0) ? 10 : Math.min(size, 50); // 最大拉取 50 条

        if (user.getLocation() == null) {
            throw new RuntimeException("请先授权定位以查看附近动态");
        }

        return postRepository.findNearbyPostByCursor(safeSize, lastCreatedAt, safeDistance, user.getId(),
                user.getLocation().getX(), user.getLocation().getY());
    }

    // 搜索帖子名称
    @Override
    public List<Post> search(Integer size, LocalDateTime lastCreatedAt, String token, String keyword) {
        Integer safeSize = (size == null || size <= 0) ? 10 : Math.min(size, 50); // 最大拉取 50 条
        // 对用户输入的关键词进行转义，防止正则注入 (比如用户输入 `.*` 会导致全表扫描)
        String safeKeyword = Pattern.quote(keyword);

        Map<String, Object> claims = JwtUtils.parseToken(token);
        String userId = (String) claims.get("id");

        return postRepository.search(safeSize, lastCreatedAt, userId, safeKeyword);
    }

    // 点赞/取消点赞
    @Override
    public LikeResult like(String postId, String token, Integer action) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        String userId = (String) claims.get("id");
        LikeResult likeResult = postRepository.like(postId, userId, action);
        return likeResult;
    }

    // 帖子详情
    @Override
    public Post showDetails(String token, String id) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        String userId = (String) claims.get("id");

        Post post = postRepository.findById(id).orElse(null);
        if (post == null)
            return null;
        // 公开可见
        if (post.getStatus() == 0) {
            return post;
        }
        //自己帖子一律放行
        else if (Objects.equals(post.getAuthorId(), userId)) {
            return post;
        }
        // 仅粉见
        else if (post.getStatus() == 2) {
            Follow follow = followRepository.findByFollowerIdAndFolloweeId(userId, post.getAuthorId());
            // 用户已关注该帖主
            if (follow != null) {
                return post;
            }
        }
        return null;
    }

    // 个人（别人）主页帖子列表
    @Override
    public List<Post> myPostList(Integer size, LocalDateTime lastCreatedAt, String token,String userId) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        String myId = (String) claims.get("id");
        Integer safeSize = (size == null || size <= 0) ? 10 : Math.min(size, 50); // 最大拉取 50 条

        log.info("myId:{},userId:{}",myId,userId);
        
        return postRepository.findMyPostByCursor(safeSize, lastCreatedAt, myId,userId);
    }

    // 添加评论
    @Override
    public Post.PostComment addComment(String token, String id, String content) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        String userId = (String) claims.get("id");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return null;
        Post.PostComment comment = new Post.PostComment();
        comment.setId(UUID.randomUUID().toString());
        comment.setUserId(userId);
        comment.setNickname(user.getNickname());
        comment.setAvatar(user.getAvatar());
        comment.setContent(content);
        comment.setStatus(0);
        comment.setCreatedAt(LocalDateTime.now());

        boolean isSuccess = postRepository.addComment(id, comment);

        if (!isSuccess) {
            throw new RuntimeException("帖子不存在或评论失败");
        }

        // 直接返回组装好的对象给前端
        return comment;
    }

    // 删除评论
    @Override
    public Boolean deleteComment(String token, String id, String commentId) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        String userId = (String) claims.get("id");
        return postRepository.deleteComment(userId, id, commentId);
    }

}
