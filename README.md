# java-filmorate
Template repository for Filmorate project.

![Схема базы данных](Filmorate_dbdiagram.png)

# Filmorate DB

Filmorate — это база данных для социальной платформы о кино, где пользователи могут лайкать фильмы, добавлять друзей и просматривать информацию о фильмах, жанрах и рейтингах.

## Структура базы

- `users` — пользователи
- `films` — фильмы
- `genres` — жанры фильмов
- `rating` — возрастные рейтинги
- `film_genre` — связь "многие ко многим" между фильмами и жанрами
- `film_likes` — лайки пользователей
- `friends` — друзья пользователей

## Ссылка на диаграмму
https://dbdiagram.io/d/68b9906b61a46d388e76f176

## Примеры SQL-запросов

### Создать все таблицы
```sql
CREATE TABLE "films" (
  "id" integer PRIMARY KEY,
  "name" varchar(100) NOT NULL,
  "description" varchar(200),
  "release_date" timestamp,
  "duration" integer,
  "rating_id" integer
);

CREATE TABLE "rating" (
  "id" integer PRIMARY KEY,
  "title" varchar(20)
);

CREATE TABLE "film_likes" (
  "user_id" integer,
  "film_id" integer,
  PRIMARY KEY ("user_id", "film_id")
);

CREATE TABLE "users" (
  "id" integer PRIMARY KEY,
  "email" varchar(100) NOT NULL,
  "login" varchar(100) NOT NULL,
  "name" varchar(100),
  "birthday" timestamp
);

CREATE TABLE "genres" (
  "id" integer PRIMARY KEY,
  "title" varchar(100) UNIQUE NOT NULL
);

CREATE TABLE "film_genre" (
  "film_id" integer,
  "genre_id" integer,
  PRIMARY KEY ("film_id", "genre_id")
);

CREATE TABLE "friends" (
  "user_id" integer,
  "friend_id" integer,
  "confirmed" bool,
  PRIMARY KEY ("user_id", "friend_id")
);

ALTER TABLE "films" ADD FOREIGN KEY ("rating_id") REFERENCES "rating" ("id");

ALTER TABLE "film_likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "film_likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("id");
```

### Получить все фильмы с жанрами и рейтингом
```sql
SELECT f.id, f.name, f.description, r.title AS rating, g.title AS filmGenre
FROM films f
LEFT JOIN mpa r ON f.rating_id = r.id
LEFT JOIN film_genre fg ON f.id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.id;
```

### Для запуска БД в серверном режиме:
## Windows:
```powershell
java -cp C:\Users\<ТВОЙ_ПОЛЬЗОВАТЕЛЬ>\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar org.h2.tools.Server -tcp
```
## MacOS/Linus:
```bash
java -cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar org.h2.tools.Server -tcp
```

