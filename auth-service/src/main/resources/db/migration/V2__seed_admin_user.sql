-- Admin user: email=admin@scalecart.com, password=Admin@123
-- Password hash generated with BCrypt strength 10
-- In production: never seed admin credentials in SQL — use env vars or first-run setup
INSERT INTO users (username, email, password, enabled)
VALUES (
           'admin',
           'admin@scalecart.com',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyuSrKgyT3Q5SFKHGQhFmpBqw8j0EhKFQy',
           true
       ) ON CONFLICT (email) DO NOTHING;

-- Assign ROLE_ADMIN to the admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@scalecart.com'
  AND r.name = 'ROLE_ADMIN'
    ON CONFLICT DO NOTHING;