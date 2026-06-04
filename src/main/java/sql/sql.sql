CREATE DATABASE IF NOT EXISTS juego;
USE juego;

-- Tablas independientes primero
DROP TABLE IF EXISTS torres;
CREATE TABLE torres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    rango INT NOT NULL,
    costo INT NOT NULL,
    daño INT NOT NULL,
    cadencia_fuego BIGINT NOT NULL,
    color_rgb VARCHAR(20) NOT NULL
);

DROP TABLE IF EXISTS tipo_bloon;
CREATE TABLE tipo_bloon (
    id_tipo VARCHAR(20) PRIMARY KEY,
    capas INT NOT NULL,
    rbe INT NOT NULL,
    velocidad DECIMAL(4,2) NOT NULL,
    color VARCHAR(20) NOT NULL,
    ruta_imagen VARCHAR(255) NOT NULL,
    id_hijo VARCHAR(20),
    FOREIGN KEY (id_hijo) REFERENCES tipo_bloon(id_tipo) ON DELETE SET NULL
);

-- Tabla dependiente al final (Relación de instancias o partidas)
DROP TABLE IF EXISTS partidas_historial;
CREATE TABLE partidas_historial (
    id_partida INT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ronda_alcanzada INT NOT NULL,
    dinero_final INT NOT NULL,
    vidas_restantes INT NOT NULL
);

-- Inserciones iniciales para pruebas
INSERT INTO torres (nombre, rango, costo, daño, cadencia_fuego, color_rgb) VALUES
('Mono Dardo', 130, 200, 1, 900, '139,69,19'),
('Mono Boomerang', 90, 350, 1, 1100, '128,128,128'),
('Mono Militar', 999, 400, 2, 2000, '0,100,0'),
('Super Kitty', 150, 2500, 1, 120, '218,165,32');

INSERT INTO tipo_bloon VALUES ('RED', 1, 1, 1.20, 'RED', 'imagenes/enemigos/enemy_1_bow_bat.png', NULL);
INSERT INTO tipo_bloon VALUES ('BLUE', 2, 2, 1.60, 'BLUE', 'imagenes/enemigos/enemy_2_skull_cat.png', 'RED');
INSERT INTO tipo_bloon VALUES ('GREEN', 3, 3, 2.20, 'GREEN', 'imagenes/enemigos/enemy_3_robot_kitty.png', 'BLUE');
INSERT INTO tipo_bloon VALUES ('YELLOW', 4, 4, 3.00, 'YELLOW', 'imagenes/enemigos/enemy_4_shadow_kitty.png', 'GREEN');