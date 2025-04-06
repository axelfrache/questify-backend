package me.axelfrache.questify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.TaskDto;
import me.axelfrache.questify.dto.request.TaskRequest;
import me.axelfrache.questify.model.Task;
import me.axelfrache.questify.security.JwtTokenProvider;
import me.axelfrache.questify.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(@RequestHeader("Authorization") String token) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.getAllTasks(userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TaskDto>> getTasksByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID categoryId) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.getTasksByCategory(userId, categoryId));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<TaskDto>> getTasksByDifficulty(
            @RequestHeader("Authorization") String token,
            @PathVariable Task.TaskDifficulty difficulty) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.getTasksByDifficulty(userId, difficulty));
    }

    @GetMapping("/completed/{completed}")
    public ResponseEntity<List<TaskDto>> getTasksByCompletion(
            @RequestHeader("Authorization") String token,
            @PathVariable boolean completed) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.getTasksByCompletion(userId, completed));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID taskId) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.getTaskById(userId, taskId));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TaskRequest request) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.createTask(userId, request));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest request) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.updateTask(userId, taskId, request));
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskDto> completeTask(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID taskId) {
        var userId = getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.completeTask(userId, taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID taskId) {
        var userId = getUserIdFromToken(token);
        taskService.deleteTask(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserIdFromToken(String token) {
        token = token.substring(7); // Enlever "Bearer "
        return tokenProvider.getUserIdFromToken(token);
    }
}
