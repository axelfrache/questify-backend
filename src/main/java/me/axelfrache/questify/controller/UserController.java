package me.axelfrache.questify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.UserDto;
import me.axelfrache.questify.dto.request.UpdatePasswordRequest;
import me.axelfrache.questify.dto.request.UpdateProfileRequest;
import me.axelfrache.questify.security.JwtTokenProvider;
import me.axelfrache.questify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        var userId = tokenProvider.getUserIdFromToken(token);
        
        return ResponseEntity.ok(userService.getCurrentUser(userId));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateProfileRequest request) {
        token = token.substring(7);
        var userId = tokenProvider.getUserIdFromToken(token);
        
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
    
    @PutMapping("/password")
    public ResponseEntity<UserDto> updatePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdatePasswordRequest request) {
        token = token.substring(7);
        var userId = tokenProvider.getUserIdFromToken(token);
        
        return ResponseEntity.ok(userService.updatePassword(userId, request));
    }
}
