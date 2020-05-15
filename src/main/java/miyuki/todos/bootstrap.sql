DROP TABLE IF EXISTS todos;
DROP TYPE IF EXISTS states;

CREATE TYPE states AS ENUM ('TODO','DONE');

CREATE TABLE TODOS (
	ID       serial       PRIMARY KEY NOT NULL,
	AUTHOR   bigint                   NOT NULL,
	EVENT    varchar(50)              NOT NULL,
	SEQUENCE smallint                 NOT NULL,
	STATE    states,
	CONTENT  text
);
