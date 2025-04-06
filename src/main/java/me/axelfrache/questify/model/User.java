package me.axelfrache.questify.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"tasks", "achievements"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false, unique = true, name = "user_name")
    private String userName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private int level;
    
    @Column(nullable = false)
    private int experience;
    
    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade currentGrade;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "user_achievements",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private Set<Achievement> achievements = new HashSet<>();
    
    public void addExperience(int xp) {
        this.experience += xp;
        updateLevel();
    }
    
    private void updateLevel() {
        int newLevel = 1;
        int xpRequired = 100;
        int remainingXp = this.experience;
        
        while (remainingXp >= xpRequired) {
            remainingXp -= xpRequired;
            newLevel++;
            xpRequired = 100 * newLevel;
        }
        
        if (newLevel != this.level) {
            this.level = newLevel;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
