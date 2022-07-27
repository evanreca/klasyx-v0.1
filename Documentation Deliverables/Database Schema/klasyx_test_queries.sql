-- Klasyx Database Test Queries
-- Evan Reca

use create_klasyx_db;

SELECT *
FROM accounts; -- SUCCESS

SELECT *
FROM accounts_history; -- SUCCESS

SELECT *
FROM composers; -- SUCCESS

SELECT *
FROM playlists; -- SUCCESS

SELECT *
FROM songs; -- SUCCESS

SET PASSWORD FOR 'root'@'localhost' = 'password';