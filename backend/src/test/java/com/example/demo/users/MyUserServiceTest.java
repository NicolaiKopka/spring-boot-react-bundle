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

        RegisterData registerUser = RegisterData.builder().username("testUser")
                .password("password")
                .checkPassword("password")
                .build();


        MyUser expectedUserToBeSaved = MyUser.builder().username("testUser")
                .password("hashedPassword")
                .roles(List.of("user"))
                .build();


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

        MyUser existingUser = MyUser.builder().username("alreadyInUser")
                .password("password")
                .roles(List.of("user"))
                .build();


        RegisterData newUser = RegisterData.builder().username("alreadyInUser")
                .password("password")
                .checkPassword("password")
                .build();

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

        RegisterData newUser = RegisterData.builder().username("alreadyInUse")
                .password("password")
                .checkPassword("falsePassword")
                .build();

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

        RegisterData newUser = RegisterData.builder().username("")
                .password("password")
                .checkPassword("password")
                .build();

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