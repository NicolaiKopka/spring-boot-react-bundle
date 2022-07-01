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
    public UserDTO registerUser(MyUser user) {

        if(user.getUsername().isBlank() || user.getPassword().isBlank() || user.getCheckPassword().isBlank()) {
            throw new RuntimeException("No empty fields allowed");
        }

        Optional<MyUser> checkUser = userRepo.findByUsername(user.getUsername());

        if(checkUser.isEmpty()) {
            if(!user.getPassword().equals(user.getCheckPassword())) {
                throw new RuntimeException("Passwords not matching");
            }

            String hashedPW = encoder.encode(user.getPassword());
            user.setPassword(hashedPW);
            user.setCheckPassword(hashedPW);
            user.setRoles(List.of("user"));
            userRepo.save(user);

            return new UserDTO(user.getUsername(), user.getRoles());

        } else {
            throw new RuntimeException("Username already in use");
        }
    }
}
