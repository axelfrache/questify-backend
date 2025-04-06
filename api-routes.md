# API Questify - Documentation des Routes

Cette documentation liste toutes les routes disponibles dans l'API Questify, leurs méthodes HTTP, paramètres et descriptions.

> **Note**: Toutes les routes (sauf celles explicitement marquées comme publiques) nécessitent un token JWT dans l'en-tête `Authorization` sous la forme `Bearer {token}`.

## Authentification

| Méthode | Route | Description | Corps de la requête | Réponse |
|---------|-------|-------------|---------------------|---------|
| POST | `/api/auth/register` | Inscription d'un nouvel utilisateur | `RegisterRequest` (username, email, password) | `AuthResponse` (token, user) |
| POST | `/api/auth/login` | Connexion d'un utilisateur | `AuthRequest` (username, password) | `AuthResponse` (token, user) |

## Utilisateurs

| Méthode | Route | Description | Paramètres | Réponse |
|---------|-------|-------------|------------|---------|
| GET | `/api/users/me` | Obtenir les informations de l'utilisateur connecté | - | `UserDto` |

## Catégories

| Méthode | Route | Description | Paramètres | Corps de la requête | Réponse |
|---------|-------|-------------|------------|---------------------|---------|
| GET | `/api/categories` | Obtenir toutes les catégories de l'utilisateur | - | - | Liste de `CategoryDto` |
| GET | `/api/categories/{categoryId}` | Obtenir une catégorie par son ID | `categoryId` (UUID) | - | `CategoryDto` |
| POST | `/api/categories` | Créer une nouvelle catégorie | - | `CategoryRequest` (name, color) | `CategoryDto` |
| PUT | `/api/categories/{categoryId}` | Mettre à jour une catégorie | `categoryId` (UUID) | `CategoryRequest` (name, color) | `CategoryDto` |
| DELETE | `/api/categories/{categoryId}` | Supprimer une catégorie | `categoryId` (UUID) | - | - |

## Tâches

| Méthode | Route | Description | Paramètres | Corps de la requête | Réponse |
|---------|-------|-------------|------------|---------------------|---------|
| GET | `/api/tasks` | Obtenir toutes les tâches de l'utilisateur | - | - | Liste de `TaskDto` |
| GET | `/api/tasks/category/{categoryId}` | Obtenir les tâches par catégorie | `categoryId` (UUID) | - | Liste de `TaskDto` |
| GET | `/api/tasks/difficulty/{difficulty}` | Obtenir les tâches par difficulté | `difficulty` (EASY, MEDIUM, HARD) | - | Liste de `TaskDto` |
| GET | `/api/tasks/completed/{completed}` | Obtenir les tâches par statut de complétion | `completed` (boolean) | - | Liste de `TaskDto` |
| GET | `/api/tasks/{taskId}` | Obtenir une tâche par son ID | `taskId` (UUID) | - | `TaskDto` |
| POST | `/api/tasks` | Créer une nouvelle tâche | - | `TaskRequest` (title, description, difficulty, categoryId, dueDate) | `TaskDto` |
| PUT | `/api/tasks/{taskId}` | Mettre à jour une tâche | `taskId` (UUID) | `TaskRequest` (title, description, difficulty, categoryId, dueDate) | `TaskDto` |
| PATCH | `/api/tasks/{taskId}/complete` | Marquer une tâche comme complétée | `taskId` (UUID) | - | `TaskDto` |
| DELETE | `/api/tasks/{taskId}` | Supprimer une tâche | `taskId` (UUID) | - | - |

## Grades (Niveaux)

| Méthode | Route | Description | Paramètres | Réponse |
|---------|-------|-------------|------------|---------|
| GET | `/api/grades/public` | Obtenir tous les grades disponibles (route publique) | - | Liste de `GradeDto` |

## Achievements (Succès)

| Méthode | Route | Description | Paramètres | Réponse |
|---------|-------|-------------|------------|---------|
| GET | `/api/achievements` | Obtenir tous les achievements de l'utilisateur | - | Liste de `AchievementDto` |
| GET | `/api/achievements/unlocked` | Obtenir les achievements débloqués | - | Liste de `AchievementDto` |
| GET | `/api/achievements/locked` | Obtenir les achievements verrouillés | - | Liste de `AchievementDto` |

## Structures des DTO

### AuthRequest
```json
{
  "username": "string",
  "password": "string"
}
```

### RegisterRequest
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

### AuthResponse
```json
{
  "token": "string",
  "user": {
    // UserDto structure
  }
}
```

### UserDto
```json
{
  "id": "UUID",
  "username": "string",
  "email": "string",
  "level": "integer",
  "experience": "integer",
  "currentGrade": {
    // GradeDto structure
  }
}
```

### CategoryRequest
```json
{
  "name": "string",
  "color": "string" // Format hexadécimal (#RRGGBB)
}
```

### CategoryDto
```json
{
  "id": "UUID",
  "name": "string",
  "color": "string",
  "taskCount": "integer"
}
```

### TaskRequest
```json
{
  "title": "string",
  "description": "string",
  "difficulty": "EASY|MEDIUM|HARD",
  "categoryId": "UUID",
  "dueDate": "string" // Format ISO-8601 (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
}
```

### TaskDto
```json
{
  "id": "UUID",
  "title": "string",
  "description": "string",
  "difficulty": "EASY|MEDIUM|HARD",
  "completed": "boolean",
  "createdAt": "string",
  "dueDate": "string",
  "category": {
    // CategoryDto structure
  }
}
```

### GradeDto
```json
{
  "id": "UUID",
  "name": "string",
  "minLevel": "integer",
  "maxLevel": "integer"
}
```

### AchievementDto
```json
{
  "id": "UUID",
  "name": "string",
  "description": "string",
  "unlocked": "boolean",
  "unlockedAt": "string"
}
```
