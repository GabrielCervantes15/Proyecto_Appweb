package mx.itson.edu.proyecto

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StockService {
    @POST("actualizar_stock")
    fun validarYActualizar(@Body request: StockRequest): Call<StockResponse>
}

data class StockRequest(
    val productos: List<Regalo>
)

data class StockResponse(
    val status: String,
    val message: String
)