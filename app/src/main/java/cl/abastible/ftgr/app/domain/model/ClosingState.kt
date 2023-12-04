package cl.abastible.ftgr.app.domain.model

data class ClosingState(
    val id: Int,
    val description: String,
    val customerVisit: Boolean,
    val takeAPicture: Boolean,
    val rescheduleDispatch: Boolean
)
