package com.example.demo.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class UserDTO {

    private String username;
    private List<String> roles;

    public UserDTO(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}
