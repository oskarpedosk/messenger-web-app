package com.example.messenger.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        return userOptional.filter(value -> passwordEncoder.matches(password, value.getPassword())).isPresent();
    }

    public boolean usernameExists(String username) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        return userOptional.isPresent();
    }

    public boolean registerUser(String username, String password) {
        if (usernameExists(username)) return false;

        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, hashedPassword);
        userRepository.save(newUser);
        return true;
    }

}
