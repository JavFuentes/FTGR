package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.TruckVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TruckCheckListViewModel @Inject constructor() : ViewModel() {
    val isDrawingView1Drawn = MutableLiveData<Boolean?>(null)
    val isDrawingView2Drawn = MutableLiveData<Boolean?>(null)
    val isEditTextFilled = MutableLiveData(false)

    // Mutable LiveData para mantener una lista de verificaciones que puede ser observada
    private val _verifications = MutableLiveData(listOf<TruckVerification>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val verifications: LiveData<List<TruckVerification>> get() = _verifications

    // Mutable LiveData para manejar el estado de habilitación del botón "Continuar"
    val isContinueButtonEnabled = MutableLiveData(0)

    init {
        // Inicializa la lista de verificaciones al crear la instancia del ViewModel
        initVerifications()
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

    // Función que verifica si todas las condiciones están satisfechas para habilitar el botón
    fun checkAllConditions() {
        val allButtonsAreOn = _verifications.value!!.all { it.isChecked == 1 }
        val atLeastDriverSignature = isDrawingView1Drawn.value == true

        isContinueButtonEnabled.value = if (allButtonsAreOn && atLeastDriverSignature) 1 else 0
    }

    // Función que verifica si todas las preguntas han sido respondidas
    fun areAllQuestionsAnswered(): Boolean {
        return _verifications.value?.none { it.isChecked == 0 } == true
    }


    private fun initVerifications() {
        _verifications.value = listOf(
            TruckVerification(1, "Revisar Estado de Mangueras. (No presenta daños evidentes a la vista, desgaste anormal evidente, sin presencia de fugas)", 0, symptomsList = listOf("Balatas Cristalizadas", "Golpe en el motor"), criticality = "Crítico"),
            TruckVerification(2, "Revisar estado de Boquilla de descarga. (No presenta daños evidentes en sus componentes que impidan el correcto funcionamiento ni fugas)", 0, symptomsList = listOf("Fugas de GLP", "Quebrado", "Trizado"), criticality = "Crítico"),
            TruckVerification(3, "Revisar Válvula de extracción (Venteo) se encuentra bien cerrada y con sello.", 0, symptomsList = listOf("Fugas de GLP", "Quebrado", "Válvulas pegadas", "Válvula no abre"), criticality = "Crítico"),
            TruckVerification(4, "Revisar operación Válvula principal (activar sistema neumático, verificar visualmente que el pistón del sistema neumático opere correctamente).", 0, symptomsList = listOf("Cortocircuito", "Ruido anormal", "Fuga de aire", "Mangueras cortadas", "Válvulas pegadas"), criticality = "Crítico"),
            TruckVerification(5, "Verificar funcionamiento de control remoto BASE a 3 metros de distancia. (Cambiar pilas si se requiere) Verificar funcionamiento de cortacorriente: detención del camión y cierre de válvula principal.", 0, symptomsList = listOf("Cortocircuito", "Ruido anormal"), criticality = "Crítico"),
            TruckVerification(6, "Verificar funcionamiento de sistema de seguridad de puertas traseras. Al quedar abiertas, cumplen la función de freno de mano. (para camiones que cuente con este sistema)", 0, symptomsList = listOf("Cortocircuito", "Mangueras cortadas"), criticality = "Crítico"),
            TruckVerification(7, "Verificar que todas las válvulas del sistema de gas se encuentren correctamente cerradas y no presentan fugas.", 0, symptomsList = listOf("Fugas de GLP", "Válvulas pegadas"), criticality = "Crítico"),
            TruckVerification(8, "Verificar que camión cuenta con destrabador, copla de seguridad y solución jabonosa para realizar pruebas.", 0, symptomsList = listOf("Cortocircuito"), criticality = "Crítico"),
            TruckVerification(9, "Verificar vigencia de documentación normativa del vehículo (Revisión Técnica, Permiso de Circulación, Seguro, HDS, Inscripción SEC camión, certificación tanque y válvulas) y autorización de ayudante y conductor.", 0, symptomsList = listOf("Cortocircuito"), criticality = "No Crítico"),
            TruckVerification(10, "Verificar niveles dentro del rango adecuado de aceites hidráulicos, lubricantes, combustible y agua.", 0, symptomsList = listOf("Fuga de aceite", "Fuga de petroleo", "Luz check engine", "Perdida de agua"), criticality = "Solución rápida"),
            TruckVerification(11, "Revisar estado neumáticos delanteros y traseros (desgaste anormal, daños evidentes a la vista, desformaciones).", 0, symptomsList = listOf("Deformado", "Desgaste excesivo", "Liso", "Pinchado"), criticality = "Solución rápida"),
            TruckVerification(12, "Revisar estado Extintores (2 unidades). Verificar certificado, mantención vigente, estado de mangueras y nivel de carga.", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            TruckVerification(13, "Corroborar conexión batería (bornes, tapa batería, soporte de batería)", 0, symptomsList = listOf("Cortocircuito", "Ayuda de partida"), criticality = "Solución rápida"),
            TruckVerification(14, "Revisar que vehículo cuenta con cuñas (2)", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            TruckVerification(15, "Revisar en caja de herramientas:(Gata, barra, llave de rueda).", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            TruckVerification(16, "Verificar que vehículo cuenta con Conos (2 unidades).", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            TruckVerification(17, "Verificar operatividad del tacógrafo.", 0, symptomsList = listOf("Memoria llena", "No marca"), criticality = "Solución rápida"),
            TruckVerification(18, "Verificar Stock Tapas amarillas.", 0, symptomsList = listOf("Piquete", "Quebrado", "Trizado"), criticality = "No Crítico"),
            TruckVerification(19, "Revisar estado de parabrisas", 0, symptomsList = listOf("Piquete", "Quebrado", "Trizado"), criticality = "No Crítico"),
            TruckVerification(20, "Revisar espejos retrovisores", 0, criticality = "No Crítico"),
            TruckVerification(21, "Verificar Aseo Exterior vehículo", 0, criticality = "No Crítico"),
            TruckVerification(22, "Verificar Aseo Interior vehículo", 0, symptomsList = listOf("Deformado","Desgaste excesivo","Liso","Pinchado"), criticality = "No Crítico"),
            TruckVerification(23, "Verificar neumático de repuesto (que no esté suelto de su soporte).", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            TruckVerification(24, "Verificar funcionamiento de luces delanteras", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            TruckVerification(25, "Verificar funcionamiento de luces de emergencia", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
        )
    }
}
