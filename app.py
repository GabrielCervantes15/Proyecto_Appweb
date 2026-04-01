from flask import Flask, request, jsonify
from mysql.connector import pooling
import mysql.connector

app = Flask(__name__)

db_config = {
    "database": "happy_box_db",
    "user": "root",
    "password": "TU_PASSWORD",
    "host": "localhost"
}

connection_pool = pooling.MySQLConnectionPool(
    pool_name="mypool",
    pool_size=10,
    **db_config
)

BASE_IMAGE_URL = "http://192.168.1.100:5000/static/images/"

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    conn = connection_pool.get_connection()
    cursor = conn.cursor(dictionary=True)
    
    query = "SELECT * FROM usuarios WHERE correo = %s AND password = %s"
    cursor.execute(query, (data['correo'], data['password']))
    user = cursor.fetchone()
    
    cursor.close()
    conn.close()
    
    if user:
        return jsonify({"status": "success", "user": user}), 200
    return jsonify({"status": "error", "message": "Credenciales inválidas"}), 401

@app.route('/productos', methods=['GET'])
def get_productos():
    conn = connection_pool.get_connection()
    cursor = conn.cursor(dictionary=True)
    
    cursor.execute("SELECT * FROM productos")
    rows = cursor.fetchall()
    
    for row in rows:
        row['imagen_url'] = BASE_IMAGE_URL + row['imagen_url']
    
    cursor.close()
    conn.close()
    return jsonify(rows), 200

@app.route('/registro', methods=['POST'])
def registro():
    data = request.json
    conn = connection_pool.get_connection()
    cursor = conn.cursor()
    
    try:
        query = "INSERT INTO usuarios (nombre, correo, password) VALUES (%s, %s, %s)"
        cursor.execute(query, (data['nombre'], data['correo'], data['password']))
        conn.commit()
        return jsonify({"status": "success"}), 201
    except mysql.connector.Error as err:
        return jsonify({"status": "error", "message": str(err)}), 400
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)