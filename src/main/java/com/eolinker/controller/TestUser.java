package com.eolinker.controller;

import com.eolinker.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Create by 向威 on 2018/5/10
 */
@Controller
@RequestMapping("/test")
public class TestUser {
    @RequestMapping("/user")
    @ResponseBody
    public User testUser(){
        User user = new User();
        user.setUserID(1);
        user.setUserName("xiangwei");
        user.setUserNickName("向威");
        user.setUserPassword("xiangwei");
        return user;
    }

    @RequestMapping("/user001")
    @ResponseBody
    public User testUser001(){
        User user = new User();
        user.setUserID(1);
        user.setUserName("xiangwei001");
        user.setUserNickName("向威");
        user.setUserPassword("xiangwei001");
        return user;
    }
}
