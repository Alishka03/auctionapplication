package com.example.auction.service;

import com.example.auction.dto.UserDto;
import com.example.auction.exception.ApiRequestException;
import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import com.example.auction.response.InvalidOperationException;
import com.example.auction.util.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RegistrationService {
    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }
    public User registerUser(UserDto userDto){
        User user = new User();
        Optional<User> userToCheck = userRepository.findByUsername(userDto.getUsername());
        if(userToCheck.isPresent()){
            log.error("User exists with username: "+userDto.getUsername());
            throw new ApiRequestException("User exists with username: "+userDto.getUsername());
        }
        user.setPassword(userDto.getPassword());
        user.setUsername(userDto.getUsername());
        user.setRoles("USER");
        return userRepository.save(user);
    }
}
