package com.example.demo.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class MyUser {

    @Id
    private String id;
    private String username;
    private String password;

    private String googleId;

    private String email;

    private List<String> roles;

    public MyUser(String username, String password, String id) {
        this.username = username;
        this.password = password;
    }

    public MyUser(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
