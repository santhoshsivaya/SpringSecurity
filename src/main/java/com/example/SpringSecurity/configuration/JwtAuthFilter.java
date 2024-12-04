package com.example.SpringSecurity.configuration;

import com.example.SpringSecurity.service.UserDetailsServicelmpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private final UserDetailsServicelmpl userDetailsServicelmpl;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException{
        final String authorization =request.getHeader("Authorization");
        final String jwt;
        final String email;
        if (authorization==null || !authorization.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authorization.substring(7);
        email = jwtUtils.extractEmail(jwt);
        if (email!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails=userDetailsServicelmpl.loadUserByUsername(email);
            if (jwtUtils.isTokenValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                jwtUtils.extractRole(jwt)
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }


        }
        filterChain.doFilter(request,response);

    }
}

