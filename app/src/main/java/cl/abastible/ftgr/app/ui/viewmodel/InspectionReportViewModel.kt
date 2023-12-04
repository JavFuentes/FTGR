package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.InspectionReportVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InspectionReportViewModel @Inject constructor() : ViewModel() {
    // Mutable LiveData para manejar el estado de dibujo de los DrawingView
    val isDrawingView1Drawn = MutableLiveData<Boolean?>(null)
    val isDrawingView2Drawn = MutableLiveData<Boolean?>(null)

    // Mutable LiveData para manejar el estado de llenado del EditText
    val isEditTextFilled = MutableLiveData(false)

    // Mutable LiveData para mantener una lista de usuarios que puede ser observada
    private val _verifications = MutableLiveData(listOf<InspectionReportVerification>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val verifications: LiveData<List<InspectionReportVerification>> get() = _verifications

    // Mutable LiveData para manejar el estado de habilitación del botón "Continuar"
    val isContinueButtonEnabled = MutableLiveData(false)

    // Elemento seleccionado
    val selectedItem = MutableLiveData<InspectionReportVerification?>()

    init {
        // Inicializa la lista de verificaciones al crear la instancia del ViewModel
        initVerificaciones()
    }

    // Método para actualizar el estado de una verificación
    fun updateVerification(id: Int, isChecked: Int) {
        val updatedList = _verifications.value?.map {
            if (it.idVerification == id) it.copy(isChecked = isChecked) else it
        }
        _verifications.value = updatedList

        // Verifica todas las condiciones después de cada cambio
        checkAllConditions()
    }

    // Función que verifica si todas las condiciones están satisfechas
    fun checkAllConditions() {
        val allButtonsAreOn = _verifications.value!!.all { it.isChecked == 1 }
        val atLeastDriverSignature = isDrawingView1Drawn.value == true

        isContinueButtonEnabled.value = allButtonsAreOn && atLeastDriverSignature
    }

    fun areAllQuestionsAnswered(): Boolean {
        return _verifications.value?.none { it.isChecked == 0 } == true
    }

    fun getMostSevereRiskType(): String {
        return _verifications.value
            ?.filter { it.isChecked == 2 }
            ?.maxByOrNull { it.typeOfRisk }
            ?.typeOfRisk ?: ""
    }

    private fun initVerificaciones() {
        _verifications.value = listOf(
            InspectionReportVerification(idVerification = 1, question = "Fugas evidentes en la instalación (tanque, válvulas).", typeOfRisk = "Instalación con fuga", isChecked = 0),
            InspectionReportVerification(idVerification = 2, question = "CONEXIONES DE TANQUE INTERVENIDAS", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 3, question = "Fuentes de ignición peligrosas: procesos de soldadura, proyección de partículas o fuego abierto a una distancia menor a 4,5 metros, que no puedan ser controladas.", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 4, question = "Tanque desplazado de su base, sin pernos en la base, o con peligro de volcamiento.", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 5, question = "Instalaciones con tanque confinado.", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 6, question = "Tanque NO propiedad de Abastible.", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 7, question = "Acceso a tanque con obstáculos que puedan causar daños en manguera (protecciones metálicas con bordes filosos).", typeOfRisk = "Instalación con riesgo inminente", isChecked = 0),
            InspectionReportVerification(idVerification = 8, question = "Central sin barrera de contención en caso de tanques ubicados en sector de estacionamientos.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 9, question = "Material combustible alrededor del Tanque a una distancia inferior a 3 metros, maleza o pasto seco.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 10, question = "Domo o candado inexistente o deteriorado significativamente.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 11, question = "Reja perimetral o canastillo inexistente o deteriorado significativamente.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 12, question = "Marcador % del tanque en mal estado", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 13, question = "Tanque ubicado a distancia menor a 2 metros de fosas o cámaras.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 14, question = "Tanque ubicado directamente bajo tendido eléctrico.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 15, question = "Tanque con corrosión extensa.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 16, question = "Tanque con fecha de re inspección vencida", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 17, question = "Tanque con pernos de anclaje faltantes.", typeOfRisk = "Instalación con riesgo menor", isChecked = 0),
            InspectionReportVerification(idVerification = 18, question = "Modificaciones no autorizadas a la instalación (techos, muros, aleros, tanques desplazados de su ubicación original, etc)", typeOfRisk = "Instalación con riesgo menor", isChecked = 0)
        )
    }
}