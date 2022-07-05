package com.example.demo.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MyUserRepo userRepo;
    private final PasswordEncoder encoder;
    public MyUser registerUser(RegisterData user) {

        if(user.getUsername().isBlank() || user.getPassword().isBlank() || user.getCheckPassword().isBlank()) {
            throw new RuntimeException("No empty fields allowed");
        }

        Optional<MyUser> checkUser = userRepo.findByUsername(user.getUsername());

        if(checkUser.isEmpty()) {
            if(!user.getPassword().equals(user.getCheckPassword())) {
                throw new RuntimeException("Passwords not matching");
            }

            MyUser userToSave = new MyUser();
            String hashedPW = encoder.encode(user.getPassword());
            userToSave.setUsername(user.getUsername());
            userToSave.setPassword(hashedPW);
            userToSave.setRoles(List.of("user"));
            return userRepo.save(userToSave);

        } else {
            throw new RuntimeException("Username already in use");
        }
    }

    public MyUser findOrCreateUser(String email, String userId, String name) {
        return userRepo.findByUsername(name).orElseGet(() -> {
            MyUser user = new MyUser();
            user.setUsername(name);
            user.setEmail(email);
            user.setGoogleId(userId);
            user.setRoles(List.of("user"));
            return userRepo.save(user);
        });
    }

}
