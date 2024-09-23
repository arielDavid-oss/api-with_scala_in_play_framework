CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          title VARCHAR NOT NULL,
                          price DOUBLE PRECISION NOT NULL,
                          description TEXT,
                          category VARCHAR,
                          image VARCHAR
);

