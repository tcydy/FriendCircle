package com.yong.pojo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow")
public class Follow {
    /**
     * 主键 ID (对应 _id)
     * Spring Data MongoDB 会自动将 ObjectId 转换为 String，方便前端传参
     */
    @Id
    private String id;

    private String followerId;//关注者Id（关注发起者）
    private String followeeId;//被关注者Id（被关注对象）

    private LocalDateTime createdAt;//关注时间

}
