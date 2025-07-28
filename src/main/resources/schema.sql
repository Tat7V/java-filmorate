-- Создание таблицы для возрастных рейтингов (MPA)
CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL
);

-- Создание таблицы для жанров
CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

-- Создание таблицы статусов дружбы
CREATE TABLE IF NOT EXISTS friendship_status (
    status_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Создание таблицы друзей
CREATE TABLE IF NOT EXISTS friends (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES friendship_status (status_id)
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id INTEGER,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

-- Создание таблицы для связи фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id)
);

-- Создание таблицы лайков
CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);