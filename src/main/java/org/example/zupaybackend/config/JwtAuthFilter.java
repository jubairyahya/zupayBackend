package org.example.zupaybackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.zupaybackend.service.JwtService;
import org.example.zupaybackend.service.TokenBlacklist;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthFilter(JwtService jwtService, TokenBlacklist tokenBlacklist) {
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Let OPTIONS preflight pass through immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }


        String jwt = extractToken(request);

        if (jwt == null) {
            // No token found at all — let the request through unauthenticated
            // Spring Security will block it if the route requires auth
            filterChain.doFilter(request, response);
            return;
        }

        //  Reject blacklisted tokens
        if (tokenBlacklist.contains(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Validate and set authentication in context
        String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Tries to extract the JWT from:
     * 1. HttpOnly cookie "zupay_access"  ← new secure method
     * 2. Authorization: Bearer header    ← fallback (mobile / old clients)
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Check cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("zupay_access".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        // 2. Fall back to Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}