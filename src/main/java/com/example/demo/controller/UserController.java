package com.example.demo.controller;

import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return  userService.getAllUsers();
    }


    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody RegisterUserDto registerUserDto) {
        return userService.createUser(registerUserDto);
    }

    @PostMapping("/auth/login")
    public String loginUser(@RequestBody RegisterUserDto registerUserDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerUserDto.getEmail(), registerUserDto.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateAccessToken(userDetails);
            return token;
        }catch (Exception e){
            return "Error";
        }
    }
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/users/user")
    public String getUser() {
        return "User";
    }
}
