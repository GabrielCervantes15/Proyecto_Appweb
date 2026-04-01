from flask import Flask, request, jsonify
from mysql.connector import pooling
import mysql.connector

app = Flask(__name__)

# Configuración de la base de datos
db_config = {
    "database": "happy_box_db",
    "user": "root",
    "password": "xrapayel", 
    "host": "localhost"
}

connection_pool = pooling.MySQLConnectionPool(
    pool_name="mypool",
    pool_size=10,
    **db_config
)

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    try:
        conn = connection_pool.get_connection()
        cursor = conn.cursor(dictionary=True)
        
        query = "SELECT nombre, correo FROM usuarios WHERE correo = %s AND password = %s"
        cursor.execute(query, (data['correo'], data['password']))
        user = cursor.fetchone()
        
        if user:
            return jsonify({"status": "success", "user": user}), 200
        return jsonify({"status": "error", "message": "Credenciales inválidas"}), 401
    finally:
        if 'cursor' in locals(): cursor.close()
        if 'conn' in locals(): conn.close()

@app.route('/productos', methods=['GET'])
def get_productos():
    try:
        conn = connection_pool.get_connection()
        cursor = conn.cursor(dictionary=True)
        
        # Obtenemos los productos. imagen_url ya viene completa desde el SQL.
        cursor.execute("SELECT nombre, precio, descripcion, categoria, imagen_url, stock FROM productos")
        rows = cursor.fetchall()
        
        return jsonify(rows), 200
    finally:
        if 'cursor' in locals(): cursor.close()
        if 'conn' in locals(): conn.close()

@app.route('/registro', methods=['POST'])
def registro():
    data = request.json
    try:
        conn = connection_pool.get_connection()
        cursor = conn.cursor()
        
        query = "INSERT INTO usuarios (nombre, correo, password) VALUES (%s, %s, %s)"
        cursor.execute(query, (data['nombre'], data['correo'], data['password']))
        conn.commit()
        return jsonify({"status": "success"}), 201
    except mysql.connector.Error as err:
        return jsonify({"status": "error", "message": "El correo ya está registrado" if err.errno == 1062 else str(err)}), 400
    finally:
        if 'cursor' in locals(): cursor.close()
        if 'conn' in locals(): conn.close()


@app.route('/actualizar_stock', methods=['POST'])
def actualizar_stock():
    data = request.get_json()
    productos = data.get('productos', [])
    
    # Error corregido: Usar el pool de conexiones en lugar de crear_conexion()
    conn = None
    cursor = None
    
    try:
        conn = connection_pool.get_connection()
        cursor = conn.cursor(dictionary=True)
        
        # 1. Validar que TODOS los productos tengan stock suficiente antes de restar nada
        for item in productos:
            cursor.execute("SELECT stock FROM productos WHERE nombre = %s", (item['nombre'],))
            producto_db = cursor.fetchone()
            
            if not producto_db:
                return jsonify({"status": "error", "message": f"Producto {item['nombre']} no encontrado"}), 404
            
            if producto_db['stock'] < item['cantidad']:
                return jsonify({"status": "error", "message": f"Stock insuficiente para {item['nombre']} (Quedan {producto_db['stock']})"}), 400
        
        # 2. Si llegamos aquí, hay stock de todo. Procedemos a descontar.
        for item in productos:
            cursor.execute("UPDATE productos SET stock = stock - %s WHERE nombre = %s", 
                           (item['cantidad'], item['nombre']))
        
        conn.commit()
        return jsonify({"status": "success", "message": "Stock actualizado correctamente"}), 200

    except Exception as e:
        if conn: conn.rollback()
        return jsonify({"status": "error", "message": str(e)}), 500
    finally:
        if cursor: cursor.close()
        if conn: conn.close()

if __name__ == '__main__':
    # host='0.0.0.0' es vital para que Android Studio lo vea
    app.run(host='0.0.0.0', port=5000, debug=True)