package com.yong.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dept {
    private Integer id;//ID,主键
    private String name;//院系名称
    private String officeLocation;//办公地点
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
}