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
        MyUser user = new MyUser("testUser", "password", "password");

        MyUser userToBeSaved = new MyUser("testUser", "hashedPassword", "hashedPassword");
        userToBeSaved.setRoles(List.of("user"));

        UserDTO returnUser = new UserDTO("testUser", List.of("user"));

        MyUserRepo userRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoder.encode(user.getPassword())).thenReturn("hashedPassword");

        UserService userService = new UserService(userRepo, encoder);
        UserDTO actualReturnedUser = userService.registerUser(user);

        Mockito.verify(userRepo).save(userToBeSaved);
        Assertions.assertThat(actualReturnedUser).isEqualTo(returnUser);
    }

    @Test
    void shouldFailToSaveIfUserAlreadyExists() {
        MyUser existingUser = new MyUser("alreadyInUser", "password", "password");
        MyUser newUser = new MyUser("alreadyInUse", "password", "password");

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
        MyUser newUser = new MyUser("alreadyInUse", "password", "falsePassword");

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
        MyUser newUser = new MyUser("", "password", "password");

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