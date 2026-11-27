package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class LocationRequst {

    String postId;
    String locationName="";
    Double longitude;
    Double latitude;
}
