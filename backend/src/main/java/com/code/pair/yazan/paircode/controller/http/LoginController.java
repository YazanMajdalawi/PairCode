package com.code.pair.yazan.paircode.controller.http;

import com.code.pair.yazan.paircode.domain.AccountCredentials;
import com.code.pair.yazan.paircode.domain.AppUser;
import com.code.pair.yazan.paircode.service.AppUserService;
import com.code.pair.yazan.paircode.service.impl.JwtServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class LoginController {
    private final PasswordEncoder passwordEncoder;
    private final AppUserService userService;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AccountCredentials accountCredentials) {
        if (userService.availableUsername(accountCredentials.username())) {
            var user = AppUser.builder()
                    .username(accountCredentials.username())
                    .password(passwordEncoder.encode(accountCredentials.password()))
                    .build();

            userService.save(user);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody AccountCredentials accountCredentials) {
        UsernamePasswordAuthenticationToken credentialsToken = new UsernamePasswordAuthenticationToken(
                        accountCredentials.username(),
                        accountCredentials.password());

        Authentication auth = authenticationManager.authenticate(credentialsToken);

        String jwtToken = jwtServiceImpl.getToken(auth.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .build();
    }

    // TODO: Invalidate the JWT
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return null;
    }
}
