package com.example.messenger.user;

import com.example.messenger.exception.ApiError;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/register")
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody User user) {

        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        // Check if passwords match
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            responseMap.put("code", 400);
            responseMap.put("message", "Passwords don't match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(responseMap));
        }

        boolean newUserCreated = userService.registerUser(user.getUsername(), user.getPassword());

        if (newUserCreated) {
            responseMap.put("code", 201);
            responseMap.put("username", user.getUsername());
            responseMap.put("message", "Registration successful!");
            return ResponseEntity.status(HttpStatus.CREATED).body(gson.toJson(responseMap));
        }

        responseMap.put("code", 400);
        responseMap.put("message", "Username already exists");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(responseMap));
    }
}
