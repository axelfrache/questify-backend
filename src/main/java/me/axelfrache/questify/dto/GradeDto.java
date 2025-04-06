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
public class GradeDto {
    private UUID id;
    private String name;
    private String description;
    private int minLevel;
    private int maxLevel;
}
