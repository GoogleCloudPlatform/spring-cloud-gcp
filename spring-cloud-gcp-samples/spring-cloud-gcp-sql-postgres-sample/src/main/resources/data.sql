-- Inserts sample users idempotently. ON CONFLICT (email) DO NOTHING ensures
-- restarting the application or running tests against an existing database
-- does not fail with duplicate key violations on the primary key, avoiding
-- interference between concurrent CI test runs sharing the database instance.
INSERT INTO users VALUES
  ('luisao@example.com', 'Anderson', 'Silva'),
  ('jonas@example.com', 'Jonas', 'Goncalves'),
  ('fejsa@example.com', 'Ljubomir', 'Fejsa')
ON CONFLICT (email) DO NOTHING;
