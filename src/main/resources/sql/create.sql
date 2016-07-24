-- Table: entry

-- DROP TABLE entry;

CREATE TABLE entry
(
  id serial NOT NULL,
  content character varying(1024),
  creation_date date,
  status character varying(1024),
  path character varying(1024),
  CONSTRAINT entry_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);