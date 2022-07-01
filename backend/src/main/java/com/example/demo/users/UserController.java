package com.example.demo.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> registerUser(@RequestBody RegisterData user) {
        try {
            MyUser returnedUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(returnedUser.getUsername(), returnedUser.getRoles()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
