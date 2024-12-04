package com.example.SpringSecurity.configuration;

import com.example.SpringSecurity.model.Users;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.awt.image.renderable.ParameterBlock;

import java.security.Key;
import java.security.Signature;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtUtils {
    public static final String SECRET_KEY="https://jwt.io/#debugger-io?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    public String extractEmail(String token){
        return  extractEmail(token).get("email").toString();
    }

    public Collection<? extends GrantedAuthority> extractRole(String token){
        Object roleClaim=extractClaim(token).get("role");
        System.out.println("test2");

        if (roleClaim instanceof List<?>){
            @SuppressWarnings("Unchecked")
                    List<HashMap<String,String>> roles=(List<HashMap<String, String>>) roleClaim;
                    return roles.stream()
                            .map(x-> new SimpleGrantedAuthority(x.get("authority")))
                            .collect(Collectors.toList());
        } else {
            return List.of(new SimpleGrantedAuthority((String) roleClaim));
        }

    }

    public String generateToken(Users userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
            Map<String,Object> extraClaims,
            Users userDetails
    ){
        extraClaims.put("email",userDetails.getEmail());
        extraClaims.put("role",userDetails.getAuthorities());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssueAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +1000 * 60 * 24))
                .signWith(getSignInKey(), Signature
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username=extractEmail(token);
        return (username.equals(userDetails.getUsername())) && ! isTokenExpired(token);

    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token).getExpiration();
    }
    private Key getSignInKey(){
        byte[] keyBytes = Decorders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaFor(keyBytes);
    }
    public Claim extractClaim(String token){
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build()
                .parseClaimsJws(token).getBody();
    }


}
