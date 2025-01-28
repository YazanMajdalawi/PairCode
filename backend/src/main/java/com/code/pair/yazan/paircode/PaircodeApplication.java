package com.code.pair.yazan.paircode;

import com.code.pair.yazan.paircode.domain.AppUser;
import com.code.pair.yazan.paircode.repository.AppUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PaircodeApplication {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public PaircodeApplication(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        appUserRepository.save(new AppUser("user1", passwordEncoder.encode("123")));
        appUserRepository.save(new AppUser("user2", passwordEncoder.encode("123")));
    }

    public static void main(String[] args) {
        SpringApplication.run(PaircodeApplication.class, args);
    }
}
