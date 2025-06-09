package _11.asktpk.artisanconnectbackend.utils;

import _11.asktpk.artisanconnectbackend.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class Tools {
    private final JwtUtil jwtUtil;

    public Tools(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public Long getClientIdFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return jwtUtil.extractUserId(authorizationHeader.substring(7));
        } else {
            return -1L;
        }
    }
}
