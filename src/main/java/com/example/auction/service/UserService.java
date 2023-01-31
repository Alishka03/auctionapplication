package com.example.auction.service;

import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User user(Long id){
        return userRepository.findById(id).get();
    }
    public User userByUsername(String username){
        return userRepository.findByUsername(username).get();
    }
    public final User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userByUsername(auth.getCredentials().toString());
    }
}
