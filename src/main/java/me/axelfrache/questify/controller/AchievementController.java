package me.axelfrache.questify.controller;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.AchievementDto;
import me.axelfrache.questify.security.JwtTokenProvider;
import me.axelfrache.questify.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<AchievementDto>> getAllAchievements(@RequestHeader("Authorization") String token) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(achievementService.getAllAchievements(userId));
    }

    @GetMapping("/unlocked")
    public ResponseEntity<List<AchievementDto>> getUnlockedAchievements(@RequestHeader("Authorization") String token) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(achievementService.getUnlockedAchievements(userId));
    }

    @GetMapping("/locked")
    public ResponseEntity<List<AchievementDto>> getLockedAchievements(@RequestHeader("Authorization") String token) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(achievementService.getLockedAchievements(userId));
    }

    private UUID getUserIdFromToken(String token) {
        token = token.substring(7); // Enlever "Bearer "
        return tokenProvider.getUserIdFromToken(token);
    }
}
