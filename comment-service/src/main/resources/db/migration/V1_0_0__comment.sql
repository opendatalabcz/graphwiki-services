CREATE TABLE COMMENT (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    entity_id VARCHAR(50) NOT NULL,
    author VARCHAR(50) NOT NULL,
    created timestamp NOT NULL DEFAULT current_timestamp,
    root BOOLEAN NOT NULL,
    text VARCHAR(2000) NOT NULL,
    parent_id VARCHAR(50),
    ip_address BYTEA NOT NULL,
    CONSTRAINT comment_comment_constraint FOREIGN KEY (parent_id) REFERENCES COMMENT (id)
);
