-- base de datos --
DROP DATABASE IF EXISTS happy_box_db;
CREATE DATABASE IF NOT EXISTS happy_box_db;
USE happy_box_db;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tarjeta VARCHAR(16) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_correo (correo)
) ENGINE=InnoDB;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(50),
    imagen_url VARCHAR(255), 
    stock INT DEFAULT 0,
    INDEX idx_categoria (categoria)
) ENGINE=InnoDB;

CREATE TABLE carritos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    producto_id INT,
    cantidad INT DEFAULT 1,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

SET GLOBAL max_connections = 1000;

INSERT INTO usuarios (nombre, correo, password, tarjeta) 
VALUES ('el feo', 'nose@gmail.com', '123456', '1234567812345678');
select *from usuarios;
INSERT INTO productos (nombre, precio, descripcion, categoria, imagen_url, stock) VALUES
('Oso Gigante', 450.00, 'Peluche suave de 1m con listón.', 'Peluches', 'http://192.168.100.12:5000/static/images/peluche_gigante.jpg', 10),
('Perrito Puppy', 280.00, 'Peluche de perrito con orejas largas y textura extra suave.', 'Peluches', 'http://192.168.100.12:5000/static/images/puppy.jpg', 15),
('Unicornio Mágico', 320.00, 'Unicornio de felpa con cuerno brillante de colores.', 'Peluches', 'http://192.168.100.12:5000/static/images/unicornio.jpg', 8),
('León de la Selva', 350.00, 'Peluche de león con melena frondosa de 40cm.', 'Peluches', 'http://192.168.100.12:5000/static/images/leon.jpg', 5),
('Globo Metálico', 120.00, 'Globo de helio Happy Birthday.', 'Globos', 'http://192.168.100.12:5000/static/images/globo_happy_birthday.jpg', 50),
('Globo Te Amo', 130.00, 'Globo metálico rojo en forma de corazón con helio.', 'Globos', 'http://192.168.100.12:5000/static/images/globo_teamo.jpg', 40),
('Arreglo Graduación', 380.00, 'Set de globos negros y dorados con forma de birrete.', 'Globos', 'http://192.168.100.12:5000/static/images/globo_graduacion.jpg', 12),
('Globo Gigante #1', 160.00, 'Globo de número gigante para aniversarios.', 'Globos', 'http://192.168.100.12:5000/static/images/globo_gigante.jpg', 20),
('Taza con Dulces', 180.00, 'Taza rellena de chocolates variados.', 'Tazas', 'http://192.168.100.12:5000/static/images/taza_personalizada.jpg', 30),
('Taza Súper Mamá', 190.00, 'Taza de cerámica con diseño especial y dulces.', 'Tazas', 'http://192.168.100.12:5000/static/images/taza_super.jpg', 25),
('Set Tazas Pareja', 340.00, 'Dúo de tazas que encajan, ideales para San Valentín.', 'Tazas', 'http://192.168.100.12:5000/static/images/taza_pareja.jpg', 10),
('Taza Gamer', 210.00, 'Taza con asa en forma de control de videojuegos.', 'Tazas', 'http://192.168.100.12:5000/static/images/taza_gamer.jpg', 15),
('Jenga Madera', 300.00, 'Clásico juego de torre de madera.', 'Juegos', 'http://192.168.100.12:5000/static/images/jenga.jpg', 20),
('Rompecabezas 500', 290.00, 'Rompecabezas con paisaje artístico de alta calidad.', 'Juegos', 'http://192.168.100.12:5000/static/images/rompecabezas.jpg', 18),
('Ajedrez de Madera', 450.00, 'Tablero plegable con piezas talladas a mano.', 'Juegos', 'http://192.168.100.12:5000/static/images/ajedrez.jpg', 7),
('Ramo 12 Rosas', 400.00, 'Doce rosas rojas frescas recién cortadas.', 'Flores', 'http://192.168.100.12:5000/static/images/ramo.jpg', 10),
('Caja de Girasoles', 420.00, 'Caja decorativa con 5 girasoles y follaje verde.', 'Flores', 'http://192.168.100.12:5000/static/images/girasoles.jpg', 10),
('Orquídea Blanca', 550.00, 'Elegante orquídea en maceta de cerámica blanca.', 'Flores', 'http://192.168.100.12:5000/static/images/orquidea.jpg', 5);
select * from productos;

DROP DATABASE IF EXISTS sistema_bancario;
CREATE DATABASE sistema_bancario;
USE sistema_bancario;

CREATE TABLE tarjetas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_tarjeta VARCHAR(16) UNIQUE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    fecha_expiracion VARCHAR(5) NOT NULL, 
    saldo DECIMAL(10, 2) NOT NULL,
    titular VARCHAR(100) NOT NULL,
    correo_propietario VARCHAR(100) NOT NULL 
);

INSERT INTO tarjetas (numero_tarjeta, cvv, fecha_expiracion, saldo, titular, correo_propietario) 
VALUES ('1234567812345678', '123', '12/28', 5000.00, 'el feo', 'nose@gmail.com');
select*from tarjetas;