package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

//用户
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post")
public class Post {

    @Id
    private String id;

    private String authorId;//发布者用户ID
    private AuthorInfo authorInfo;//冗余：发布者快照信息
    private String title;//动态标题，可选，最长100字符，用于搜索
    private String content;//动态文本内容
    private List<String> images;//图片url数组
    private PostLocation location;//打卡位置信息

    private Integer likesNum;//点赞数量
    private List<String> likes;//点赞用户id列表
    private List<PostComment> comments;//内嵌评论列表
    private Integer status;//动态状态：0=公布,1=私密,2=仅被关注可见
    private LocalDateTime createdAt;//发布时间

    @Transient 
    private Double distance;//帖子发布与用户的距离

    // ===================== 内嵌类 =====================
    @Data
    public static class AuthorInfo {
        private String nickname;
        private String avatar;
    }

    @Data
    public static class PostLocation {
        private String name;//地点名称，例如：学校图书馆
        private GeoJsonPoint geo;//GeoJSON Point [lng, lat]
    }

    @Data
    public static class PostComment {

        private String id;//评论独立id
        private String userId;//评论用户id
        private String nickname;//冗余：评论者昵称
        private String avatar;//冗余：评论者头像
        private String content;//评论内容
        private Integer status;//评论状态：0=公布,1=私密
        private LocalDateTime createdAt;//评论时间
    }
}
