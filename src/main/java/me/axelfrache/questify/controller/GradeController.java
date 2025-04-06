package me.axelfrache.questify.controller;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.GradeDto;
import me.axelfrache.questify.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/public")
    public ResponseEntity<List<GradeDto>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }
}
