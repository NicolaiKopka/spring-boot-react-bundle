package com.example.demo.users;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MyUserServiceTest {

    @Test
    void shouldSaveUserWithHashedPW() {
        RegisterData registerUser = new RegisterData("testUser", "password", "password");

        MyUser expectedUserToBeSaved = new MyUser("testUser", "hashedPassword", "hashedPassword");
        expectedUserToBeSaved.setRoles(List.of("user"));

        MyUserRepo userRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(userRepo.findByUsername(registerUser.getUsername())).thenReturn(Optional.empty());

        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoder.encode(registerUser.getPassword())).thenReturn("hashedPassword");

        UserService userService = new UserService(userRepo, encoder);
        userService.registerUser(registerUser);

        Mockito.verify(userRepo).save(expectedUserToBeSaved);
    }

    @Test
    void shouldFailToSaveIfUserAlreadyExists() {
        MyUser existingUser = new MyUser("alreadyInUser", "password", "password");
        RegisterData newUser = new RegisterData("alreadyInUser", "password", "password");

        MyUserRepo userRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(userRepo.findByUsername(newUser.getUsername())).thenReturn(Optional.of(existingUser));

        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        UserService userService = new UserService(userRepo, encoder);

        try {
            userService.registerUser(newUser);
            fail();
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Username already in use");
        }
    }

    @Test
    void shouldFailToSaveIfPasswordsAreNotMatching() {
        RegisterData newUser = new RegisterData("alreadyInUse", "password", "falsePassword");

        MyUserRepo userRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(userRepo.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());

        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        UserService userService = new UserService(userRepo, encoder);

        try {
            userService.registerUser(newUser);
            fail();
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Passwords not matching");
        }
    }

    @Test
    void shouldFailToSaveIfInputFieldsAreEmpty() {
        RegisterData newUser = new RegisterData("", "password", "password");

        MyUserRepo userRepo = Mockito.mock(MyUserRepo.class);

        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        UserService userService = new UserService(userRepo, encoder);

        try {
            userService.registerUser(newUser);
            fail();
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("No empty fields allowed");
        }
    }


}