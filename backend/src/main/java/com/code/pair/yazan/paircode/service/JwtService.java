package com.code.pair.yazan.paircode.service;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String getToken(String username);
    String getAuthUser(HttpServletRequest request);
    String getAuthUser(String token);
}
