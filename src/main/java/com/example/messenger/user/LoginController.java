package com.example.messenger.user;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/login")
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> login(@RequestBody User user) {

        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        boolean isAuthenticated = userService.authenticate(user.getUsername(), user.getPassword());

        if (isAuthenticated) {
            responseMap.put("code", 200);
            responseMap.put("username", user.getUsername());
            responseMap.put("message", "Login successful!");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseMap));
        }

        responseMap.put("code", 401);
        responseMap.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(gson.toJson(responseMap));
    }
}
