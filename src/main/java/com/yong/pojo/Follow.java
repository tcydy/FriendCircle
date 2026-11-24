package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

//用户
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow")
public class Follow{

    /**
     * 主键 ID (对应 _id)
     * Spring Data MongoDB 会自动将 ObjectId 转换为 String，方便前端传参
     */
    @Id
    private String id;

    private String followerId;//关注者Id（关注发起者）
    private String followeeId;//被关注者Id（被关注对象）

    private LocalDateTime createdAt;//关注时间

    //快照
    private String followerNickName;//冗余：关注者昵称
    private String followerAvatar;//冗余：关注者头像
    private String followeeNickName;//冗余：关注者昵称
    private String followeeAvatar;//冗余：被关注者头像

}
