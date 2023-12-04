package cl.abastible.ftgr.app.domain.model

/**
 * La clase `Order` representa un modelo de datos para un pedido de entrega.
 *
 * @property idOrder Identificador único de una orden.
 * @property clientName Nombre del cliente asociado con esta orden.
 * @property address Dirección de entrega o servicio para la orden.
 * @property commune Nombre de la comuna (subdivisión administrativa) asociada con la dirección de la orden. Puede ser null.
 * @property time Tiempo estimado o programado para la entrega o el servicio.
 * @property idDriver Identificador único del conductor asignado para esta orden. Puede ser null.
 * @property status Estado actual de la orden (por ejemplo, "pendiente", "en curso", "completado").
 * @property center Información sobre el centro de distribución o logística asociado con esta orden. Puede ser null.
 * @property isVip Indica si la orden está marcada como VIP.
 */

data class Order(
    val idOrder: Int,
    val clientName: String,
    val address: String,
    val commune: String?,
    val time: String,
    val idDriver: String?,
    var status: String,
    val center: String?,
    val isVip: Boolean = false
)