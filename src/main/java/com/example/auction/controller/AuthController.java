package com.example.auction.controller;

import com.example.auction.dto.UserDto;
import com.example.auction.exception.ApiRequestException;
import com.example.auction.model.User;
import com.example.auction.response.InvalidOperationException;
import com.example.auction.service.RegistrationService;

import com.example.auction.util.UserValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Controller for registration")
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @PostMapping("/register")
    @Operation(summary = "Registration of new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrated user",

                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "500", description = "User has already registered or email is not valid"),})
    public User register(@RequestBody @Valid @Parameter(description = "username and password") UserDto userDto, BindingResult bind) {
        if (bind.hasErrors()) {
            throw new ApiRequestException("Oops email is not valid!");
        } else {
            log.trace("Registering new user");
            return registrationService.registerUser(userDto);
        }
    }
}
