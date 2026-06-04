 USE juego;

    DROP TABLE IF EXISTS torres ;

    CREATE TABLE

    torres(
            id INT
    
    AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    rango INT NOT NULL,
    costo INT NOT NULL,
    daño INT NOT NULL,
    cadencia_fuego BIGINT NOT NULL,
    color_rgb VARCHAR(20) NOT NULL
);

    INSERT INTO

    torres(nombre, rango, costo, daño, cadencia_fuego, color_rgb) 
        VALUES(
         
         
        'Mono Dardo', 130, 200, 1, 900, '139,69,19'),
( 
         
        'Mono Boomerang', 90, 350, 1, 1100, '128,128,128'),
( 
         
        'Mono Militar', 999, 400, 2, 2000, '0,100,0'),
( 
    

 
    

'Super Kitty', 150, 2500, 1, 120, '218,165,32');