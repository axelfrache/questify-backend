package me.axelfrache.questify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private UUID id;
    private String name;
    private String description;
    private int requiredCount;
    private int experienceReward;
    private UUID categoryId;
    private String categoryName;
    private boolean unlocked;
    private int currentProgress;
}
