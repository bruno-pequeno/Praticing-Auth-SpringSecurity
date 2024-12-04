package com.bruno.auth_spring_security.repositories;

import com.bruno.auth_spring_security.domain.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, String> {
    UserModel findByEmail();
}
