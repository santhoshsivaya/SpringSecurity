package com.example.SpringSecurity.service;

import com.example.SpringSecurity.configuration.JwtUtils;
import com.example.SpringSecurity.model.ERole;
import com.example.SpringSecurity.model.Login;
import com.example.SpringSecurity.model.Register;
import com.example.SpringSecurity.model.Users;
import com.example.SpringSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String register(Register register){
        var user= Users.builder()
                .username(register.getName())
                .email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword()))
                .number(register.getNumber())
                .erole(ERole.USER).build();
        userRepository.save(user);
        return "User Added Sucessfully";
    }
    public ResponseEntity<?> authenticate(Login login) {
        try {
            Authentication authentication = AuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        Optional<Users> userDetails = userRepository.findByEmail(login.getEmail());
        if (userDetails.isPresent()) {
            String token = jwtUtils.generateToken(userDetails.get());
        } else {
            return ResponseEntity.noContent().build();

        }
    }

    public ResponseEntity<Users> update(int id, String role){
        Optional<Users> user=userRepository.findById(id);
        if (user.isPresent()) {
            if (role.equals("DEVELOPER")) {
                user.get().setErole(ERole.DEVELOPER);
            } else if (role.equals("ADMIN")) {
                user.get().setErole(ERole.ADMIN);
            } else {
                user.get().setErole(ERole.USER);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user.get()));
        } else {
            return ResponseEntity.noContent().build();
            }
        }
        }
