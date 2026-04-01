from flask import Flask, request, jsonify
from mysql.connector import pooling

app = Flask(__name__)

db_config = {
    "database": "sistema_bancario",
    "user": "root",
    "password": "xrapayel", 
    "host": "localhost"
}

bank_pool = pooling.MySQLConnectionPool(pool_name="bankpool", pool_size=5, **db_config)

@app.route('/procesar_pago', methods=['POST'])
def procesar_pago():
    data = request.json
    tarjeta = data.get('tarjeta')
    cvv = data.get('cvv')
    monto = data.get('monto')
    correo = data.get('correo')

    conn = bank_pool.get_connection()
    cursor = conn.cursor(dictionary=True)

    try:
        query = """SELECT saldo FROM tarjetas 
                   WHERE numero_tarjeta = %s AND cvv = %s AND correo_propietario = %s"""
        cursor.execute(query, (tarjeta, cvv, correo))
        cuenta = cursor.fetchone()

        if not cuenta:
            return jsonify({"status": "error", "message": "Datos de tarjeta no válidos para este usuario"}), 401

        if cuenta['saldo'] < monto:
            return jsonify({"status": "error", "message": "Saldo insuficiente"}), 400
        cursor.execute("UPDATE tarjetas SET saldo = saldo - %s WHERE numero_tarjeta = %s", (monto, tarjeta))
        conn.commit()

        return jsonify({"status": "success", "message": "Pago autorizado"}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({"status": "error", "message": str(e)}), 500
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)