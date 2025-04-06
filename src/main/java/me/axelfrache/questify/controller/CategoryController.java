package me.axelfrache.questify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.CategoryDto;
import me.axelfrache.questify.dto.request.CategoryRequest;
import me.axelfrache.questify.security.JwtTokenProvider;
import me.axelfrache.questify.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestHeader("Authorization") String token) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(categoryService.getAllCategories(userId));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID categoryId) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(categoryService.getCategoryById(userId, categoryId));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CategoryRequest request) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(categoryService.createCategory(userId, request));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequest request) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(categoryService.updateCategory(userId, categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID categoryId) {
        var userId = getUserIdFromToken(token);
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserIdFromToken(String token) {
        token = token.substring(7); // Enlever "Bearer "
        return tokenProvider.getUserIdFromToken(token);
    }
}
