package com.galapea.techblog.recipefinder.controller;

import com.galapea.techblog.recipefinder.entity.User;
import com.galapea.techblog.recipefinder.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;

    @GetMapping
    String users(Model model) {
        List<User> users = userService.fetchAll();
        model.addAttribute("users", users);
        return "users";
    }
}
