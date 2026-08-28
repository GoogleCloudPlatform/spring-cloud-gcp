-- Creates the users table idempotently. IF NOT EXISTS prevents failures when
-- multiple CI runners execute tests concurrently against the shared Cloud SQL database.
CREATE TABLE IF NOT EXISTS users (
  email VARCHAR(255),
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  PRIMARY KEY (email));
