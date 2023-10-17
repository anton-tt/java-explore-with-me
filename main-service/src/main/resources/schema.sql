DROP TABLE IF EXISTS users             cascade;
DROP TABLE IF EXISTS categories        cascade;
DROP TABLE IF EXISTS compilations      cascade;
DROP TABLE IF EXISTS locations         cascade;
DROP TABLE IF EXISTS requests          cascade;
DROP TABLE IF EXISTS events            cascade;
DROP TABLE IF EXISTS compilation_event cascade;

CREATE TABLE IF NOT EXISTS users (
  id    BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(254) NOT NULL UNIQUE,
  name  VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
  id        BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  is_pinned BOOLEAN      NOT NULL,
  title     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
  id    BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name  VARCHAR(50)  NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
  id  BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  lat FLOAT  NOT NULL,
  lon FLOAT  NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
  id                 BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title              VARCHAR(120)                NOT NULL,
  annotation         VARCHAR(2000)               NOT NULL,
  description        VARCHAR(7000)               NOT NULL,
  state              VARCHAR(20)                          DEFAULT 'PENDING',
  is_paid            BOOLEAN                              DEFAULT FALSE,
  category_id        BIGINT                               REFERENCES categories (id) ON DELETE CASCADE ON UPDATE CASCADE,
  location_id        BIGINT                               REFERENCES locations (id) ON DELETE CASCADE ON UPDATE CASCADE,
  initiator_id       BIGINT                               REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  created_date       TIMESTAMP WITHOUT TIME ZONE,
  published_date     TIMESTAMP WITHOUT TIME ZONE,
  event_date         TIMESTAMP WITHOUT TIME ZONE,
  participant_limit  INTEGER                               DEFAULT 0,
  confirmed_requests INTEGER                               DEFAULT 0,
  request_moderation BOOLEAN                               DEFAULT TRUE,
  views              INTEGER                               DEFAULT 0
);

CREATE TABLE IF NOT EXISTS requests (
  id           BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_id     BIGINT                      NOT NULL REFERENCES events (id) ON DELETE CASCADE ON UPDATE CASCADE,
  requester_id BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  status       VARCHAR(20)                 NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_event (
  id             BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  compilation_id BIGINT NOT NULL REFERENCES compilations (id),
  event_id       BIGINT NOT NULL REFERENCES events (id)
);