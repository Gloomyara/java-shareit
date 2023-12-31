DROP TABLE IF EXISTS requests_items;
DROP TABLE IF EXISTS item_comments;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name        VARCHAR(64) NOT NULL,
    email       VARCHAR(64) UNIQUE NOT NULL,
    CONSTRAINT  pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    description VARCHAR(255)                NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id   BIGINT                      NOT NULL,
    CONSTRAINT  pk_request PRIMARY KEY (id),
    CONSTRAINT  fk_ru FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name        VARCHAR(64)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    available   BOOLEAN      NOT NULL,
    owner_id    BIGINT       NOT NULL,
    request_id  BIGINT,
    CONSTRAINT  pk_item PRIMARY KEY (id),
    CONSTRAINT  fk_iu FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT  fk_ir FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS requests_items
(
    request_id  BIGINT NOT NULL,
    items_id     BIGINT NOT NULL,
    CONSTRAINT  fk_ri_r FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT  fk_ri_i FOREIGN KEY (items_id) REFERENCES item (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    item_id     BIGINT                      NOT NULL,
    booker_id   BIGINT                      NOT NULL,
    status      VARCHAR(64)                 NOT NULL,
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT  pk_booking PRIMARY KEY (id),
    CONSTRAINT  fk_bu FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT  fk_bi FOREIGN KEY (item_id)   REFERENCES item (id)  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    author_id   BIGINT                      NOT NULL,
    item_id     BIGINT                      NOT NULL,
    text        VARCHAR(255)                NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT  pk_comment PRIMARY KEY (id),
    CONSTRAINT  fk_cu FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT  fk_ci FOREIGN KEY (item_id)   REFERENCES item (id)  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item_comments
(
    item_id     BIGINT NOT NULL,
    comments_id BIGINT NOT NULL,
    CONSTRAINT  fk_ic_i FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE,
    CONSTRAINT  fk_ic_c FOREIGN KEY (comments_id) REFERENCES comment (id) ON DELETE CASCADE
);
