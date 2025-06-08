package _11.asktpk.artisanconnectbackend.utils;

import _11.asktpk.artisanconnectbackend.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

public class Tools {
    private static JwtUtil jwtUtil = null;

    public Tools(JwtUtil jwtUtil) {
        Tools.jwtUtil = jwtUtil;
    }

    static public Long getClientIdFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return jwtUtil.extractUserId(authorizationHeader.substring(7));
        } else {
            return null;
        }
    }
}
