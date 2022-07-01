package com.example.demo.login;

import com.example.demo.security.JWTService;
import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final MyUserRepo myUserRepo;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginData login) {
        // TODO ask if should Test for empty fields ??
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));

            MyUser user = myUserRepo.findByUsername(login.getUsername()).orElseThrow();
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", user.getRoles());

            return ResponseEntity.ok(new LoginResponse(jwtService.createToken(claims, login.getUsername())));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }



}
