package cl.abastible.ftgr.app.domain.model

/**
 * Data class que representa una verificación de suministro.
 *
 * @property idVerification Identificador único de la verificación.
 * @property description Descripción detallada de la verificación.
 * @property stage Etapa o fase en la que se realiza esta verificación.
 * @property isChecked Estado que indica si la verificación ha sido realizada (true) o no (false).
 */

data class SupplyVerification(
    val idVerification: Int,
    val description: String,
    val stage: Int,
    var isChecked: Int = 0
)
