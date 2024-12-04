package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {
    Optional<Users>findByEmail(String email);

    Optional<Users>findByUserName(String username);
}
