-- base de datos --
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