CREATE TYPE TASK_STATE AS ENUM ('OPEN', 'ASSIGNED', 'DONE');
CREATE TYPE TASK_TYPE AS ENUM ('COMPLAINT', 'ENTITY_REQUEST');

CREATE TABLE TASK (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    type TASK_TYPE NOT NULL,
    author VARCHAR(50) NOT NULL,
    created timestamp NOT NULL DEFAULT current_timestamp,
    entity_id VARCHAR(50) NOT NULL,
    entity_label VARCHAR(200) NOT NULL,
    entity_url BYTEA NOT NULL,
    assign_url BYTEA NOT NULL,
    state TASK_STATE NOT NULL,
    assignee VARCHAR(50),
    assignee_role VARCHAR(50)
);
