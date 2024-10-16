DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1024) NOT NULL,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_id BIGINT REFERENCES requests(id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id bigint NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    booker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
    );