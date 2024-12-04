package com.bruno.auth_spring_security.application;

import com.bruno.auth_spring_security.domain.user.UserModel;
import com.bruno.auth_spring_security.dto.LoginRequestDTO;
import com.bruno.auth_spring_security.dto.LoginResponseDTO;
import com.bruno.auth_spring_security.dto.RegisterRequestDTO;
import com.bruno.auth_spring_security.infra.security.TokenService;
import com.bruno.auth_spring_security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body){
        UserModel userModel = this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(passwordEncoder.matches(body.password(), userModel.getPassword())){
            String token = this.tokenService.generateToken(userModel);

            return ResponseEntity.ok(new LoginResponseDTO(userModel.getName(), token));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body){
        Optional<UserModel> userFounded = this.userRepository.findByEmail(body.email());

        if(userFounded.isEmpty()) {
            UserModel userModel = new UserModel();
            userModel.setPassword(passwordEncoder.encode(body.password()));
            userModel.setEmail(body.email());
            userModel.setName(body.name());

            this.userRepository.save(userModel);

            String token = this.tokenService.generateToken(userModel);

            return ResponseEntity.ok(new LoginResponseDTO(userModel.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
