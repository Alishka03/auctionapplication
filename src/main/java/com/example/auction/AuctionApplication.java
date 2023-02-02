package com.example.auction;

import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class AuctionApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuctionApplication.class, args);
    }
    @Bean
    public CommandLineRunner CommandLineRunnerBean(UserRepository repo, PasswordEncoder encoder) {
        return (args) -> {
            User user = new User();
            user.setUsername("admin@mail.ru");
            user.setPassword(encoder.encode("admin"));
            user.setRoles("ADMIN");
            repo.save(user);
        };
    }
}
