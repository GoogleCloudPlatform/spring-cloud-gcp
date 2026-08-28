-- Inserts sample users idempotently. INSERT IGNORE ensures restarting
-- the application or running tests against an existing database does not fail
-- with duplicate key violations on the primary key.
INSERT IGNORE INTO users VALUES
  ('luisao@example.com', 'Anderson', 'Silva'),
  ('jonas@example.com', 'Jonas', 'Goncalves'),
  ('fejsa@example.com', 'Ljubomir', 'Fejsa');
