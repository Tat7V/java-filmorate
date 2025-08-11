MERGE INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@example.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'user2', 'User Two', '1995-05-15'),
(3, 'user3@example.com', 'user3', 'User Three', '2000-10-20');

MERGE INTO friendship (user_id, friend_id) VALUES
(1, 2),
(2, 3);

MERGE INTO mpa (mpa_id, name, description)
VALUES (1, 'G', 'Нет возрастных ограничений'),
       (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
       (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
       (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
       (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

MERGE INTO genres (genre_id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

MERGE INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Film One', 'Description One', '2000-01-01', 120, 1),
(2, 'Film Two', 'Description Two', '2010-05-15', 90, 2);

MERGE INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(2, 2);

MERGE INTO likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1);