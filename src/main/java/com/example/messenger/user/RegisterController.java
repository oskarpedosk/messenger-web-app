package com.example.messenger.user;

import com.google.gson.Gson;
import com.example.messenger.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody User user) {

        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        // Check if passwords match
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            responseMap.put("code", 400);
            responseMap.put("message", "Passwords don't match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(responseMap));
        }

        boolean newUser = userService.registerUser(user.getUsername(), user.getPassword());

        if (newUser) {
            responseMap.put("code", 201);
            responseMap.put("username", user.getUsername());
            responseMap.put("message", "Registration successful!");
            return ResponseEntity.status(HttpStatus.CREATED).body(gson.toJson(responseMap));
        } else {
            responseMap.put("code", 400);
            responseMap.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(responseMap));
        }
    }
}
