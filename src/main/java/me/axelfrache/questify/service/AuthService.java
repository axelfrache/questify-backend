package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.auth.AuthRequest;
import me.axelfrache.questify.dto.auth.AuthResponse;
import me.axelfrache.questify.dto.auth.RegisterRequest;
import me.axelfrache.questify.exception.BadRequestException;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.GradeRepository;
import me.axelfrache.questify.repository.UserRepository;
import me.axelfrache.questify.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUserName()))
            throw new BadRequestException("This username is already taken");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("This email is already in use");

        var initialGrade = gradeRepository.findByMinLevelLessThanEqualAndMaxLevelGreaterThanEqual(1, 1)
                .orElse(null);

        var user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .level(1)
                .experience(0)
                .currentGrade(initialGrade)
                .achievements(new HashSet<>())
                .tasks(new HashSet<>())
                .build();

        var savedUser = userRepository.save(user);
        var token = tokenProvider.generateToken(savedUser.getUserName(), savedUser.getId());
        var userDto = userService.getCurrentUser(savedUser.getId());
        
        return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new BadRequestException("User not found"));

        var token = tokenProvider.generateToken(user.getUserName(), user.getId());
        
        var userDto = userService.getCurrentUser(user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }
}
