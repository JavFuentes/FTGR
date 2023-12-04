package cl.abastible.ftgr.app.domain.model

/**
 * La clase `InspectionReportVerification` es un modelo de datos que representa un ítem de verificación en un informe de inspección.
 *
 * @property idVerification Identificador único de un ítem de verificación en la inspección.
 * @property question Pregunta o declaración que necesita ser verificada durante la inspección.
 * @property isChecked Estado que indica si el ítem de verificación ha sido marcado o no. Por defecto, su valor es false.
 */

data class InspectionReportVerification(
    val idVerification: Int,
    val question: String,
    val typeOfRisk: String,
    var isChecked: Int = 0
)
