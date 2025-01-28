package com.code.pair.yazan.paircode.service;

import com.code.pair.yazan.paircode.domain.AppUser;

import java.util.Optional;

public interface AppUserService {
    Optional<AppUser> findByUsername(String username);
    boolean availableUsername(String username);
    AppUser save(AppUser user);
}
