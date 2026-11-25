package com.yong.repository.custom;

import com.yong.pojo.Follow;

public interface FollowCustomRepository {

    public Follow findByFollowerIdAndFolloweeId(String followerId, String followeeId);

    public void saveNoReturn(Follow follow);
    public void delete(Follow follow);

}
