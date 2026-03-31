package mx.itson.edu.proyecto

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UsuarioLocal.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_PERFIL = "perfil"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_TARJETA = "tarjeta"

        const val TABLE_CARRITO = "carrito_compras"
        const val COLUMN_C_NOMBRE = "nombre"
        const val COLUMN_C_PRECIO = "precio"
        const val COLUMN_C_DESC = "descripcion"
        const val COLUMN_C_CAT = "categoria"
        const val COLUMN_C_IMG = "imagen"
        const val COLUMN_C_CANT = "cantidad"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_PERFIL (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, correo TEXT, password TEXT, tarjeta TEXT)")

        db?.execSQL("CREATE TABLE $TABLE_CARRITO (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_C_NOMBRE TEXT, " +
                "$COLUMN_C_PRECIO REAL, " +
                "$COLUMN_C_DESC TEXT, " +
                "$COLUMN_C_CAT TEXT, " +
                "$COLUMN_C_IMG INTEGER, " +
                "$COLUMN_C_CANT INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("CREATE TABLE $TABLE_CARRITO (id INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_C_NOMBRE TEXT, $COLUMN_C_PRECIO REAL, $COLUMN_C_DESC TEXT, $COLUMN_C_CAT TEXT, $COLUMN_C_IMG INTEGER, $COLUMN_C_CANT INTEGER)")
        }
    }

    fun guardarCarrito(lista: List<Regalo>) {
        val db = this.writableDatabase
        db.delete(TABLE_CARRITO, null, null)
        for (regalo in lista) {
            val values = ContentValues().apply {
                put(COLUMN_C_NOMBRE, regalo.nombre)
                put(COLUMN_C_PRECIO, regalo.precio)
                put(COLUMN_C_DESC, regalo.descripcion)
                put(COLUMN_C_CAT, regalo.categoria)
                put(COLUMN_C_IMG, regalo.imagenRes)
                put(COLUMN_C_CANT, regalo.cantidad)
            }
            db.insert(TABLE_CARRITO, null, values)
        }
        db.close()
    }

    fun obtenerCarrito(): MutableList<Regalo> {
        val lista = mutableListOf<Regalo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CARRITO", null)
        if (cursor.moveToFirst()) {
            do {
                val regalo = Regalo(
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
                )
                lista.add(regalo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun vaciarCarritoDB() {
        val db = this.writableDatabase
        db.delete(TABLE_CARRITO, null, null)
        db.close()
    }

    fun registrarUsuario(nombre: String, correo: String, pass: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo)
            put("password", pass)
            put("tarjeta", "")
        }
        val id = db.insert(TABLE_PERFIL, null, values)
        db.close()
        return id
    }

    fun actualizarTarjeta(correo: String, tarjeta: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_TARJETA, tarjeta) }
        val filas = db.update(TABLE_PERFIL, values, "$COLUMN_CORREO = ?", arrayOf(correo))
        db.close()
        return filas
    }

    fun obtenerTarjeta(correo: String): String {
        val db = this.readableDatabase
        var tarjeta = ""
        val cursor = db.rawQuery("SELECT $COLUMN_TARJETA FROM $TABLE_PERFIL WHERE $COLUMN_CORREO = ?", arrayOf(correo))
        if (cursor.moveToFirst()) { tarjeta = cursor.getString(0) ?: "" }
        cursor.close()
        db.close()
        return tarjeta
    }
}