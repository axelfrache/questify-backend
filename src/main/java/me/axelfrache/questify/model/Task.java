package me.axelfrache.questify.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@ToString(exclude = {"user", "category"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskDifficulty difficulty;
    
    @Column(nullable = false)
    private boolean completed;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime dueDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    public int calculateXp() {
        return switch (difficulty) {
            case EASY -> 10;
            case MEDIUM -> 20;
            case HARD -> 40;
            case EPIC -> 80;
        };
    }
    
    public void complete() {
        if (!this.completed) {
            this.completed = true;
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public enum TaskDifficulty {
        EASY, MEDIUM, HARD, EPIC
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
