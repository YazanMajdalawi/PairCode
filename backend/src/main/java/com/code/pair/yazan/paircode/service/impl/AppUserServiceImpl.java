package com.code.pair.yazan.paircode.service.impl;

import com.code.pair.yazan.paircode.domain.AppUser;
import com.code.pair.yazan.paircode.repository.AppUserRepository;
import com.code.pair.yazan.paircode.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public boolean availableUsername(String username) {
        return findByUsername(username).isEmpty();
    }

    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }
}
