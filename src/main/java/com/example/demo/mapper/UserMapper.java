package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entities.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

    }

    public static User mapToUser (UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        return user;
    }
}
