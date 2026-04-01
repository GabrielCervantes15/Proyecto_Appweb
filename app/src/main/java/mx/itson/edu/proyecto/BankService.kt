package mx.itson.edu.proyecto

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BankService {
    @POST("procesar_pago")
    fun autorizarPago(@Body request: BankRequest): Call<BankResponse>
}

data class BankRequest(
    val tarjeta: String,
    val cvv: String,
    val monto: Double,
    val correo: String
)

data class BankResponse(
    val status: String,
    val message: String
)