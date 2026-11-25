package com.yong.repository.custom;

import com.yong.pojo.Follow;
import com.yong.pojo.User;

public interface FollowCustomRepository {

    public Follow findByFollowerIdAndFolloweeId(String followerId, String followeeId);

    public void saveNoReturn(Follow follow);

    public void delete(Follow follow);

    public void updateProfileById(User user);

}
