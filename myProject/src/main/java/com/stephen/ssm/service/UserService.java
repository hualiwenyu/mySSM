package com.stephen.ssm.service;


import com.stephen.ssm.model.User;

import java.util.List;

/**
 * Created by stephen on 2016/7/15.
 */
public interface UserService {

    List<User> getAllUser();

    User getUserByPhoneOrEmail(String emailOrPhone, Short state);

    User getUserById(Long userId);

}
