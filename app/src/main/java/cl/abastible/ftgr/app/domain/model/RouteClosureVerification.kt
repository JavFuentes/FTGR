package cl.abastible.ftgr.app.domain.model

data class RouteClosureVerification(
    val idVerification: Int,
    val description: String,
    var isChecked: Int = 0,
    val criticality: String,
    val symptomsList: List<String>? = null,
    val imageResId: Int? = null
)