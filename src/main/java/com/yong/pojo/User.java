package com.yong.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

//用户
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class User {

    /**
     * 主键 ID (对应 _id)
     * Spring Data MongoDB 会自动将 ObjectId 转换为 String，方便前端传参
     */
    @Id
    private String id;

    /**
     * 登录账号 (唯一索引，防止重复注册)
     */
    @Indexed(unique = true)
    private String username;

    /**
     * 密码
     * 加 @JsonIgnore 防止查询用户信息时把密码哈希返回给前端
     */
    @JsonIgnore
    private String password;
    private String nickname;//昵称
    private String avatar;//头像 URL
    private String profile;//简介

    /**
     * 用户最新定位
     * 1. 使用 GeoJsonPoint 自动映射 { type: "Point", coordinates: [lng, lat] }
     * 2. @GeoSpatialIndexed 会在应用启动时自动创建 2dsphere 地理空间索引
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private Stats stats;//社交统计数据（内嵌文档）
    private LocalDateTime createdAt;//注册时间

    //不保存到数据库
    @Transient
    private String token;//登录令牌
    @Transient
    private String newPassword;//更改密码
    

    /**
     * 内嵌的统计类
     * 作为一个内部类，MongoDB 会将其存储为 user 文档里的一个嵌套对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        
        private Integer followingCount;//关注数
        private Integer followerCount;//被关注数
    }
}
