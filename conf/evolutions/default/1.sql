CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          title VARCHAR NOT NULL,
                          price DOUBLE PRECISION NOT NULL,
                          description TEXT,
                          category VARCHAR,
                          image VARCHAR
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR NOT NULL UNIQUE,
                       email VARCHAR NOT NULL,
                       password VARCHAR NOT NULL
);

INSERT INTO users (username, email, password)
VALUES ('usuario123', 'usuario@example.com', 'password123');
