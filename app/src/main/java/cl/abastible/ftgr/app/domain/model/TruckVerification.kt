package cl.abastible.ftgr.app.domain.model

/**
 * La clase `TruckVerification` sirve como modelo de datos para las verificaciones relacionadas con los camiones en la aplicación.
 *
 * @property idVerification Identificador único que representa una instancia particular de verificación de camión. Este valor es inmutable y sirve como una referencia única a cada objeto `TruckVerification` creado.
 * @property description Cadena que contiene una descripción detallada de lo que está siendo verificado respecto al camión. Puede utilizarse para proporcionar información adicional o contexto sobre lo que se está verificando.
 * @property isChecked Propiedad mutable que indica si esta particular verificación de camión ha sido marcada como completada (true) o no (false). Puede ser utilizado para controlar el estado de la verificación durante el ciclo de vida de la aplicación.
 * @property symptomsList Lista opcional y mutable de strings que puede contener los síntomas relacionados con la verificación del camión. Puede ser `null` si no hay síntomas asociados. Útil para mantener una lista de comprobación de posibles problemas o condiciones a verificar.
 * @property imageResId Propiedad opcional que, si se proporciona, contiene el identificador de un recurso de imagen asociado con esta verificación de camión. Puede ser utilizado para mostrar imágenes representativas o iconos en la interfaz de usuario.
 */

data class TruckVerification(
    val idVerification: Int,
    val description: String,
    var isChecked: Int = 0,
    val criticality: String,
    val symptomsList: List<String>? = null,
    val imageResId: Int? = null
)
