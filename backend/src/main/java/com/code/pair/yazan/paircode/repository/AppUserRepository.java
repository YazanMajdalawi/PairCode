package com.code.pair.yazan.paircode.repository;

import com.code.pair.yazan.paircode.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);
}
