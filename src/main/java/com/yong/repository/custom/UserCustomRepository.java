package com.yong.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import com.yong.pojo.User;

public interface UserCustomRepository {

    void updateProfileById(User user);

    void updatePasswordById(User user);

    void updateLocationById(String id, Double longitude, Double latitude);

    boolean isUsernameExist(String username);

    void updateStats(String userId, int followingDelta, int followerDelta);

    List<User> relations(String safeKeyword, Integer safeSize, LocalDateTime lastCreatedAt, String userId, Integer type);

}
