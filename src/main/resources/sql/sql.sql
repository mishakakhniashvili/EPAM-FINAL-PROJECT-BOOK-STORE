-- Idempotent seed data (safe to run on every start)
-- Uses MERGE KEY(...) so it won't crash on duplicates.

-- =========================
-- EMPLOYEES
-- =========================
MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1990-05-15', 'john.doe@email.com', 'John Doe', '$2a$10$SUcpxb5iEc6nAZdN/QId4e3z5btFGzUNMec8dBjvPqn7VQcIUSgU6', '555-123-4567');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1985-09-20', 'jane.smith@email.com', 'Jane Smith', '$2a$10$BqXMIhcmOVD33oQDa8An/OIrMUVFiN/5msOsjeLZFD/dr9S8eyC6u', '555-987-6543');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1978-03-08', 'bob.jones@email.com', 'Bob Jones', '$2a$10$6U.ZEUhK.oKm7Tyz2Ml.o.FVQJufnbG6B1H65ICG29wFyUw/dpJDe', '555-321-6789');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1982-11-25', 'alice.white@email.com', 'Alice White', '$2a$10$7AZKPp/EGtkJo5fS7KqNjOcPV40yGdCOdtOptsG2EoS9VDZNSPj4e', '555-876-5432');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1995-07-12', 'mike.wilson@email.com', 'Mike Wilson', '$2a$10$k.0v3u5Vf0v6sB2n8aC9E.7gJpQeQ1e7lXvPqfG9m1C5Q0YpGqgKu', '555-234-5678');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1989-01-30', 'sara.brown@email.com', 'Sara Brown', '$2a$10$1I8c2vC8H0VQyZg0mKfXQe7qJ2eYj3gQd1oKp0uK9mJw8EwqkZt4u', '555-876-5433');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1975-06-18', 'tom.jenkins@email.com', 'Tom Jenkins', '$2a$10$hH5n0y1Hk9c5JbY3d9z8qOeVxgGx0Qj5fN9v3c5fQqVvH5o2q2s6G', '555-345-6789');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1987-12-04', 'lisa.taylor@email.com', 'Lisa Taylor', '$2a$10$y6G9uVQkB8h6Qm5Q3QmW1eYdS6n7cB1tQm8k7gQ2dTQm4p2m0a7rO', '555-789-0123');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1992-08-22', 'david.wright@email.com', 'David Wright', '$2a$10$w9T2m3kQ6q1m8c4d0QmNbeGm3h2q8vQm0c1k7dQ2m8c5q1m9d0a7K', '555-456-7890');

MERGE INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
    KEY (EMAIL)
    VALUES ('1980-04-10', 'emily.harris@email.com', 'Emily Harris', '$2a$10$Sx2m9cQ1k7dQ0m8c5q1m9d0a7KpQm3h2q8vQm0c1k7dQ2m8c5q1m9', '555-098-7654');

-- =========================
-- CLIENTS
-- =========================
MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1000.00, 'client1@example.com', 'Medelyn Wright', '$2a$10$.6EMm3pwm9nPk9XitAichu4ek1Y6kj8eIAnEW/KOVlbsEy4Ds4lXm');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1500.50, 'client2@example.com', 'Landon Phillips', '$2a$10$Qd7m3kQ6q1m8c4d0QmNbeGm3h2q8vQm0c1k7dQ2m8c5q1m9d0a7K');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (800.75, 'client3@example.com', 'Harmony Mason', '$2a$10$u5Vf0v6sB2n8aC9E.7gJpQeQ1e7lXvPqfG9m1C5Q0YpGqgKu0v3u');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1200.25, 'client4@example.com', 'Archer Harper', '$2a$10$H0VQyZg0mKfXQe7qJ2eYj3gQd1oKp0uK9mJw8EwqkZt4u1I8c2v');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (900.80, 'client5@example.com', 'Kira Jacobs', '$2a$10$JbY3d9z8qOeVxgGx0Qj5fN9v3c5fQqVvH5o2q2s6GhH5n0y1Hk');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1100.60, 'client6@example.com', 'Maximus Kelly', '$2a$10$B8h6Qm5Q3QmW1eYdS6n7cB1tQm8k7gQ2dTQm4p2m0a7rOy6G9u');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1300.45, 'client7@example.com', 'Sierra Mitchell', '$2a$10$k.0v3u5Vf0v6sB2n8aC9E.7gJpQeQ1e7lXvPqfG9m1C5Q0YpGqgKu');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (950.30, 'client8@example.com', 'Quinton Saunders', '$2a$10$1I8c2vC8H0VQyZg0mKfXQe7qJ2eYj3gQd1oKp0uK9mJw8EwqkZt4u');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (1050.90, 'client9@example.com', 'Amina Clarke', '$2a$10$hH5n0y1Hk9c5JbY3d9z8qOeVxgGx0Qj5fN9v3c5fQqVvH5o2q2s6G');

MERGE INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
    KEY (EMAIL)
    VALUES (880.20, 'client10@example.com', 'Bryson Chavez', '$2a$10$7AZKPp/EGtkJo5fS7KqNjOcPV40yGdCOdtOptsG2EoS9VDZNSPj4e');

-- =========================
-- BOOKS
-- =========================
MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('The Hidden Treasure', 'Adventure', 'ADULT', 24.99, '2018-05-15', 'Emily White', 400, 'Mysterious journey', 'An enthralling adventure of discovery', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Echoes of Eternity', 'Fantasy', 'TEEN', 16.50, '2011-01-15', 'Daniel Black', 350, 'Magical realms', 'A spellbinding tale of magic and destiny', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Whispers in the Shadows', 'Mystery', 'ADULT', 29.95, '2018-08-11', 'Sophia Green', 450, 'Intriguing suspense', 'A gripping mystery that keeps you guessing', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('The Starlight Sonata', 'Romance', 'ADULT', 21.75, '2011-05-15', 'Michael Rose', 320, 'Heartwarming love story', 'A beautiful journey of love and passion', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Beyond the Horizon', 'Science Fiction', 'CHILD', 18.99, '2004-05-15', 'Alex Carter', 280, 'Interstellar adventure', 'An epic sci-fi adventure beyond the stars', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Dancing with Shadows', 'Thriller', 'ADULT', 26.50, '2015-05-15', 'Olivia Smith', 380, 'Suspenseful twists', 'A thrilling tale of danger and intrigue', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Voices in the Wind', 'Historical Fiction', 'ADULT', 32.00, '2017-05-15', 'William Turner', 500, 'Rich historical setting', 'A compelling journey through time', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Serenade of Souls', 'Fantasy', 'TEEN', 15.99, '2013-05-15', 'Isabella Reed', 330, 'Enchanting realms', 'A magical fantasy filled with wonder', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Silent Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Intricate detective work', 'A mystery that keeps you on the edge', 'ENGLISH');

MERGE INTO BOOKS (NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE)
    KEY (NAME)
    VALUES ('Whirlwind Romance', 'Romance', 'OTHER', 23.25, '2022-05-15', 'Emma Turner', 360, 'Passionate love affair', 'A romance that sweeps you off your feet', 'ENGLISH');
