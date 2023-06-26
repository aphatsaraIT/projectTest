CREATE TABLE IF NOT EXISTS `user_flyway` (
      user_id INT AUTO_INCREMENT PRIMARY KEY,
      first_name VARCHAR(255) NOT NULL,
      last_name VARCHAR(255) NOT NULL,
      birth_date VARCHAR(255),
      gender VARCHAR(10),
      address VARCHAR(255),
      email VARCHAR(255),
      phone VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;