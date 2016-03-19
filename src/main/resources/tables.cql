CREATE TABLE person (
  first_name text,
  revision_id timeuuid,
  json text,

  PRIMARY KEY ((first_name), revision_id)
)
WITH CLUSTERING ORDER BY (revision_id DESC);