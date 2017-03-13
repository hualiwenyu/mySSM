package com.stephen.ssm.service.impl;

import com.stephen.ssm.dao.UserDao;
import com.stephen.ssm.model.User;
import com.stephen.ssm.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.ls.LSOutput;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by stephen on 2016/7/15.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    
    @Resource
    private UserDao userDao;

    public User getUserById(Long userId) {
        return userDao.selectUserById(userId);
    }
    
    public User getUserByPhoneOrEmail(String emailOrPhone, Short state) {
        return userDao.selectUserByPhoneOrEmail(emailOrPhone,state);
    }
    
    public List<User> getAllUser() {
        return userDao.selectAllUser();
    }

    private void test(){
        System.out.println("test");
    }
}
