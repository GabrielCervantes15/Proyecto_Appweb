from flask import Flask, request, jsonify
import firebase_admin
from firebase_admin import credentials, db

app = Flask(__name__)

cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://proyecto-54e6c-default-rtdb.firebaseio.com/'
})

@app.route('/actualizar_stock', methods=['POST'])
def actualizar_stock():
    data = request.get_json()
    productos_carrito = data.get('productos', [])
    
    try:
        ref_productos = db.reference('productos')
        productos_db = ref_productos.get()

        if not productos_db:
            return jsonify({"status": "error", "message": "No hay productos en la base de datos"}), 404

        for item_carrito in productos_carrito:
            encontrado = False
            for i, p_db in enumerate(productos_db):
                if p_db['nombre'] == item_carrito['nombre']:
                    encontrado = True
                    if p_db['stock'] < item_carrito['cantidad']:
                        return jsonify({"status": "error", "message": f"Stock insuficiente para {item_carrito['nombre']}"}), 400
                    break
            
            if not encontrado:
                return jsonify({"status": "error", "message": f"Producto {item_carrito['nombre']} no encontrado"}), 404

        for item_carrito in productos_carrito:
            for i, p_db in enumerate(productos_db):
                if p_db['nombre'] == item_carrito['nombre']:
                    nuevo_stock = p_db['stock'] - item_carrito['cantidad']
                    ref_productos.child(str(i)).update({'stock': nuevo_stock})
                    break

        return jsonify({"status": "success", "message": "Compra procesada en Firebase"}), 200

    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)