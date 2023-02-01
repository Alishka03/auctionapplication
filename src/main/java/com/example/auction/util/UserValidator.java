package com.example.auction.util;

import com.example.auction.service.JpaUserDetailsService;
import com.example.auction.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private final JpaUserDetailsService userDetailsService;

    public UserValidator(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        try{
            userDetailsService.loadUserByUsername(user.getUsername());
        }catch(UsernameNotFoundException e){
            errors.rejectValue("username","","username exists with this username ");
        }
    }
}
