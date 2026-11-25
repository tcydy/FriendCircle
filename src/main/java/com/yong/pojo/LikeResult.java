package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//点赞状态
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class LikeResult {
    private Integer likesNum; // 最新点赞总数
    private Boolean isLiked;  // 当前用户是否已点赞

}
