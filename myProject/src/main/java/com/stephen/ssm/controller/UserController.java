package com.stephen.ssm.controller;

import com.stephen.ssm.model.User;
import com.stephen.ssm.service.UserService;
import com.stephen.ssm.service.impl.UserServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by stephen on 2016/7/15.
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger log = Logger.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @RequestMapping("/showUser")
    public String showUser(HttpServletRequest request, Model model){
        log.info("查询所有用户信息");
        List<User> userList = userService.getAllUser();
        model.addAttribute("userList",userList);
        return "showUser";
    }


    public static void main(String[] args) {

        String[] bytes = {"1","2","3"};
        Set<String> set = new HashSet<String>(Arrays.<String>asList(bytes));
        System.out.println(set);

        List<String> strings = new ArrayList<String>();
        System.out.println(Arrays.toString(strings.getClass().getTypeParameters()));

        int[] ints = new int[]{1,2,3};

        UserServiceImpl  service = new UserServiceImpl();
//        service.test();


    }



}
