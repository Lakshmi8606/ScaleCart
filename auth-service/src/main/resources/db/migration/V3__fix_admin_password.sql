-- Correct BCrypt hash for password Admin@123 (strength 10)
UPDATE users
SET password = '$2a$10$T9n1x15J9su6Y5y1TX9ETuPFogcA0kRhElRCl8aTQQil/..PNslbi'
WHERE email = 'admin@scalecart.com';
