package com.foodorder.auth.service;

import com.foodorder.auth.dto.AuthResponse;
import com.foodorder.auth.dto.LoginRequest;
import com.foodorder.auth.dto.RegisterRequest;
import com.foodorder.auth.model.Role;
import com.foodorder.auth.model.User;
import com.foodorder.auth.repository.UserRepository;
import com.foodorder.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private  final UserRepository userRepository;

    private final  JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        //step1 : check if email exists
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email is already registered");
        }
        //step 2 : build new user

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        //step 3: save

        userRepository.save(user);

        //step 4 : generate token

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        //step 5:

        return AuthResponse.builder().token(token).build();
    }
        public AuthResponse login(LoginRequest request){

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if(existingUser.isEmpty()){
          throw new RuntimeException("Email is not found");

        }
        User user = existingUser.get();
        // check password

            if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
                throw new RuntimeException("Invalid Credentials");
            }
            // generate token

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            return AuthResponse.builder().token(token).build();

    }


}
