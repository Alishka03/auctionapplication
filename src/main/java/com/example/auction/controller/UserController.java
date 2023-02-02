package com.example.auction.controller;

import com.example.auction.model.User;
import com.example.auction.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
@Tag(name = "Controller for user")
@SecurityRequirement(name = "basicAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/profile")
    @Operation(description = "My Profile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        String username =principal.getName();
        log.trace("Logged in user : "+username);
        User user = userService.userByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/myposts")
    @Operation(description = "Getting my posts")
    public ResponseEntity<?> getMyPosts(Principal principal){
        String username =principal.getName();

        User user = userService.userByUsername(username);
        return new ResponseEntity<>(user.getPostsList(),HttpStatus.OK);
    }
    public User getAuthenticatedUser(Principal principal){
        String username =principal.getName();
        User user = userService.userByUsername(username);
        return user;
    }
}
