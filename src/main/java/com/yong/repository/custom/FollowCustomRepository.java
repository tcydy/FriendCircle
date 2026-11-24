package com.yong.repository.custom;

import com.yong.pojo.Follow;

public interface FollowCustomRepository {

    public Follow findByFollowerIdAndFolloweeId(String followerId,String followeeId);

}
