package com.quizai.controller;

import com.quizai.domain.R;
import com.quizai.domain.User;
import com.quizai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public R login(@RequestBody User user) {
        return userService.login(user);
    }

    @RequestMapping("register")
    public R register(@RequestBody User user) {
        return userService.register(user);
    }

    @RequestMapping("update")
    public R update(@RequestBody User user) {
        return userService.changeUserInfo(user);
    }
}
