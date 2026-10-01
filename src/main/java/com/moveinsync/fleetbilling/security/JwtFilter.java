package com.moveinsync.fleetbilling.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Frontend files and auth endpoint do not require JWT
        if (uri.startsWith("/auth/")
                || !uri.startsWith("/api/")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String authorization =
                request.getHeader("Authorization");

        // Token is missing
        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write("JWT token required");
            return;
        }

        // Remove "Bearer " from token
        String token =
                authorization.substring(7);

        // Validate token
        if (!jwtService.isValid(token)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "Invalid or expired JWT token"
            );

            return;
        }

        // Get username from token
        String username =
                jwtService.getUsername(token);

        // Create authentication object
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        // Store authentication in SecurityContext
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // Continue request
        filterChain.doFilter(request, response);
    }
}