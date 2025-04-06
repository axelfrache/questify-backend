package me.axelfrache.questify.controller;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.UserDto;
import me.axelfrache.questify.security.JwtTokenProvider;
import me.axelfrache.questify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
