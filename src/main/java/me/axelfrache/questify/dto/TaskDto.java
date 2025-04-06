package me.axelfrache.questify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.axelfrache.questify.model.Task;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private UUID id;
    private String title;
    private String description;
    private Task.TaskDifficulty difficulty;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime dueDate;
    private UUID categoryId;
    private String categoryName;
    private int xpReward;
}
