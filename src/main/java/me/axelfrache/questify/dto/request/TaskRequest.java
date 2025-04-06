package me.axelfrache.questify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TaskRequest {
    @NotBlank(message = "Task title cannot be empty")
    private String title;
    
    private String description;
    
    @NotNull(message = "Task difficulty must be specified")
    private Task.TaskDifficulty difficulty;
    
    private LocalDateTime dueDate;
    
    private UUID categoryId;
}
