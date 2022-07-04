package com.example.demo.login;

import com.example.demo.security.JWTService;
import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import com.example.demo.users.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login/google")
public class GoogleLoginController {

    private final String CLIENT_ID = "1019999010766-lld8krspracip7l0gp13oi1jr1ifcgpg.apps.googleusercontent.com";
    private final UserService userService;
    private final JWTService jwtService;

    public GoogleLoginController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/{idTokenString}")
    public ResponseEntity<LoginResponse> verifyGoogleLogin(@PathVariable String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Get profile information from payload
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String userId = payload.getSubject();

            MyUser user = userService.findOrCreateUser(email, userId, name);

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", user.getRoles());
            return ResponseEntity.ok(new LoginResponse(jwtService.createToken(claims, user.getUsername())));

        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
