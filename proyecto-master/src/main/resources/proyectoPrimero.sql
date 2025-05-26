
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

CREATE TABLE IF NOT EXISTS Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS Libro (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    estado ENUM('Disponible', 'Prestado') DEFAULT 'Disponible' NOT NULL
);

CREATE TABLE IF NOT EXISTS Prestamo (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE DEFAULT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
    FOREIGN KEY (id_libro) REFERENCES Libro(id_libro)
);


CREATE USER IF NOT EXISTS 'usuario_remoto'@'%' IDENTIFIED BY 'password123';

GRANT ALL PRIVILEGES ON biblioteca.* TO 'usuario_remoto'@'%';
FLUSH PRIVILEGES;
