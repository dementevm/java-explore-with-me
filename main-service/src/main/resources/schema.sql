DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGSERIAL    NOT NULL,
    name  VARCHAR(250) NOT NULL CHECK (char_length(name) >= 2),
    email VARCHAR(254) NOT NULL CHECK (char_length(email) >= 6),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGSERIAL   NOT NULL,
    name VARCHAR(50) NOT NULL CHECK (char_length(name) >= 1),
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);


CREATE TABLE IF NOT EXISTS locations
(
    id  BIGSERIAL NOT NULL,
    lat FLOAT     NOT NULL,
    lon FLOAT     NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGSERIAL                                                     NOT NULL,
    annotation         VARCHAR(2000) CHECK (char_length(annotation) >= 20)           NOT NULL,
    category_id        BIGINT                                                        NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE                                   NOT NULL,
    initiator_id       BIGINT                                                        NOT NULL,
    location_id        BIGINT                                                        NOT NULL,
    paid               BOOLEAN                                                       NOT NULL,
    title              VARCHAR(120) CHECK (title IS NULL OR char_length(title) >= 3) NOT NULL,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR(7000) CHECK (description IS NULL OR char_length(description) >= 20),
    participant_limit  INTEGER,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(255),
    views              BIGINT,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_LOCATION FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_USER FOREIGN KEY (initiator_id) REFERENCES users (id);


CREATE TABLE compilation_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT pk_compilation_events PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE compilations
(
    id     BIGSERIAL   NOT NULL,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(50) NOT NULL CHECK (char_length(title) >= 1),
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

ALTER TABLE compilation_events
    ADD CONSTRAINT fk_comeve_on_compilation FOREIGN KEY (compilation_id) REFERENCES compilations (id);

ALTER TABLE compilation_events
    ADD CONSTRAINT fk_comeve_on_event FOREIGN KEY (event_id) REFERENCES events (id);


CREATE TABLE IF NOT EXISTS requests
(
    id           BIGSERIAL                   NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id     BIGINT                      NOT NULL,
    requester_id BIGINT                      NOT NULL,
    status       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id)
);

ALTER TABLE requests
    ADD CONSTRAINT FK_REQUESTS_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE requests
    ADD CONSTRAINT FK_REQUESTS_ON_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id);


CREATE TABLE IF NOT EXISTS comments
(
    id         BIGSERIAL                   NOT NULL,
    text       VARCHAR(2000)               NOT NULL,
    event_id   BIGINT                      NOT NULL,
    author_id  BIGINT                      NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);