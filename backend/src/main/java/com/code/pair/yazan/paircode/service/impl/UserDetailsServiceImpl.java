package com.code.pair.yazan.paircode.service.impl;

import lombok.AllArgsConstructor;
import com.code.pair.yazan.paircode.domain.AppUser;
import com.code.pair.yazan.paircode.service.AppUserService;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        AppUser currentUser = user.get();
        return User.builder()
                .username(currentUser.getUsername())
                .password(currentUser.getPassword())
                .build();
    }
}
