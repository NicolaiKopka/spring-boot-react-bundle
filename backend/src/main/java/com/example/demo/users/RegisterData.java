package com.example.demo.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterData {

    private String username;
    private String password;
    private String checkPassword;

}
