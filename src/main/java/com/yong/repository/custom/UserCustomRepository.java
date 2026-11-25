package com.yong.repository.custom;

import com.yong.pojo.User;

public interface UserCustomRepository {

    void updateProfileById(User user);

    void updatePasswordById(User user);

    void updateLocationById(String id, Double longitude, Double latitude);

    boolean isUsernameExist(String username);

    void updateStats(String userId, int followingDelta, int followerDelta);

}
