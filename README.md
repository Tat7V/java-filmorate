# java-filmorate
Template repository for Filmorate project.
## Filmorate Database Schema
```mermaid
erDiagram
	direction TB
	users {
		bigint id PK ""  
		varchar email  ""  
		varchar login  ""  
		varchar name  ""  
		date birthday  ""  
	}

	friendships {
		bigint user_id PK,FK ""  
		bigint friend_id PK,FK ""  
		varchar status  ""  
	}

	likes {
		bigint film_id PK,FK ""  
		bigint user_id PK,FK ""  
	}

	films {
		bigint id PK ""  
		varchar name  ""  
		varchar description  ""  
		date release_date  ""  
		integer duration  ""  
		bigint mpa_id FK ""  
	}

	mpa_ratings {
		bigint id PK ""  
		varchar name  ""  
		varchar description  ""  
	}

	film_genres {
		bigint id PK ""  
		bigint film_id FK ""  
		bigint genre_id FK ""  
	}

	genre {
		bigint id PK ""  
		varchar name  ""  
	}

	users||--o{friendships:""
	users||--o{likes:""
	films||--o{likes:""
	films||--o{film_genres:""
	genre||--o{film_genres:""
	mpa_ratings||--o{films:""
```





### 📝 Пояснение к схеме

**Основные сущности:**
- `users` — пользователи приложения
- `films` — информация о фильмах
- `friendships` — дружба между пользователями
- `likes` — лайки фильмов от пользователей
- `mpa_ratings` — возрастные рейтинги
- `genres` — жанры фильмов

---

### 🛠 Примеры SQL-запросов

#### 1. Операции с пользователями
**Добавление пользователя:**
```sql
INSERT INTO users (email, login, name, birthday)
VALUES ('user@example.com', 'login123', 'John Doe', '1990-01-01');
```

**Поиск общих друзей:**
```sql
SELECT u.* FROM users u
JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = 1
JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = 2
WHERE f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED';
```

#### 2. Работа с фильмами
**Добавление фильма:**
```sql
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Inception', 'A thief who steals corporate secrets', '2010-07-16', 148, 3);
```

**Топ-5 популярных фильмов:**
```sql
SELECT f.*, COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 5;
```

#### 3. Управление дружбой
**Запрос на дружбу:**
```sql
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'PENDING');
```

**Подтверждение дружбы:**
```sql
UPDATE friendships
SET status = 'CONFIRMED'
WHERE user_id = 2 AND friend_id = 1;
```

#### 4. Лайки
**Добавление лайка:**
```sql
INSERT INTO likes (film_id, user_id)
VALUES (5, 1);
```

**Удаление лайка:**
```sql
DELETE FROM likes
WHERE film_id = 5 AND user_id = 1;
```

#### 5. Поиск фильмов по жанру
```sql
SELECT f.* FROM films f
JOIN film_genres fg ON f.id = fg.film_id
WHERE fg.genre_id = 1; -- 1 = "Комедия"
```

---



