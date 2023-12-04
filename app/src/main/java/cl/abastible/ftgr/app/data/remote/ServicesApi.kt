package cl.abastible.ftgr.app.data.remote

import cl.abastible.ftgr.app.domain.model.TruckVerification
import retrofit2.http.GET

interface ServicesApi {
    @GET("VTGR/CHECK_CAMION_RPTA")
    suspend fun getCheckCamionRespuesta(): TruckVerification
}