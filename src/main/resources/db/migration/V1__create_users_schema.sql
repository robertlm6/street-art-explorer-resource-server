CREATE TABLE users
(
    id                  SERIAL PRIMARY KEY,
    auth_server_user_id INT          NOT NULL UNIQUE,
    username            VARCHAR(50)  NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    first_name          VARCHAR(100),
    last_name           VARCHAR(100),
    birth_date          TIMESTAMP(6),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP
);
