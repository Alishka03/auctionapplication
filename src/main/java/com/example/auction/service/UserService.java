package com.example.auction.service;

import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

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
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return userByUsername(currentUserName);
        }else{
            throw new RuntimeException("No User");
        }
    }
}
