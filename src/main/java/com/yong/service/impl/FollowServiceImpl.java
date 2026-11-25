package com.yong.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.yong.pojo.Follow;
import com.yong.pojo.User;
import com.yong.repository.FollowRepository;
import com.yong.repository.PostRepository;
import com.yong.repository.UserRepository;
import com.yong.service.FollowService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FollowServiceImpl implements FollowService {

    

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    FollowServiceImpl(UserRepository userRepository,FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }


    

}
