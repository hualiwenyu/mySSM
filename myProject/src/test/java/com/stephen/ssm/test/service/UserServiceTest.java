package com.stephen.ssm.test.service;

import com.stephen.ssm.model.User;
import com.stephen.ssm.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by stephen on 17/2/21.
 */

//加载需要的配置文件
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void test(){


        User user = userService.getUserById(1l);

        System.out.println(user.toString());

    }

}
