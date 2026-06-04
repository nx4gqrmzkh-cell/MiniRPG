-- 1. Tabla catálogo: Define las propiedades fijas de cada tipo de enemigo
CREATE TABLE tipo_bloon (
    id_tipo VARCHAR(20) PRIMARY KEY, -- 'RED', 'BLUE', 'GREEN', 'YELLOW'
    capas INT NOT NULL,               -- Equivalente a 'layers' (vida/fases)
    rbe INT NOT NULL,                 -- Red Bloon Equivalent (daño/vida total)
    velocidad DECIMAL(4,2) NOT NULL,  -- Equivalente a 'speed'
    color VARCHAR(20) NOT NULL,       -- Nombre del color o código HEX
    ruta_imagen VARCHAR(255) NOT NULL,-- Ruta del archivo de imagen
    id_hijo VARCHAR(20),              -- Auto-referencia para el enemigo que suelta al morir
    FOREIGN KEY (id_hijo) REFERENCES tipo_bloon(id_tipo)
);

-- 2. Tabla de instancias: Para guardar el estado de los enemigos en la partida actual
CREATE TABLE instancia_bloon (
    id_instancia INT AUTO_INCREMENT PRIMARY KEY,
    id_tipo VARCHAR(20) NOT NULL,
    posicion_x DECIMAL(8,2) DEFAULT 0.00,
    posicion_y DECIMAL(8,2) DEFAULT 0.00,
    distancia_recorrida DECIMAL(8,2) DEFAULT 0.00,
    explotado BOOLEAN DEFAULT FALSE,
    id_path INT, -- Por si manejas múltiples caminos en tu juego
    FOREIGN KEY (id_tipo) REFERENCES tipo_bloon(id_tipo)
);
-- Primero insertamos el RED porque no tiene hijo (es el nivel más bajo)
INSERT INTO tipo_bloon (id_tipo, capas, rbe, velocidad, color, ruta_imagen, id_hijo)
VALUES ('RED', 1, 1, 1.2, 'RED', 'imagenes/enemigos/enemy_1_bow_bat.png', NULL);

-- Ahora los demás, referenciando al que acabamos de crear o al anterior
INSERT INTO tipo_bloon (id_tipo, capas, rbe, velocidad, color, ruta_imagen, id_hijo)
VALUES 
('BLUE', 2, 2, 1.6, 'BLUE', 'imagenes/enemigos/enemy_2_skull_cat.png', 'RED'),
('GREEN', 3, 3, 2.2, 'GREEN', 'imagenes/enemigos/enemy_3_robot_kitty.png', 'BLUE'),
('YELLOW', 4, 4, 3.0, 'YELLOW', 'imagenes/enemigos/enemy_4_shadow_kitty.png', 'GREEN');