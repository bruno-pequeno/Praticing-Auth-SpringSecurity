package com.bruno.auth_spring_security.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bruno.auth_spring_security.domain.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String SECRET;

    public String generateToken(UserModel userModel){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            return JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(userModel.getEmail())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);

        }catch(JWTCreationException e){
            throw new RuntimeException("Error while authenticating");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        }catch(JWTVerificationException e){
            return null;
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.ofHours(-3));
    }
}
