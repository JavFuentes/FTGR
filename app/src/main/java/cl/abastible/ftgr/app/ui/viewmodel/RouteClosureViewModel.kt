package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.RouteClosureVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteClosureViewModel @Inject constructor() : ViewModel() {
    val isDrawingView1Drawn = MutableLiveData<Boolean?>(null)
    val isDrawingView2Drawn = MutableLiveData<Boolean?>(null)
    val isEditTextFilled = MutableLiveData(false)

    // Mutable LiveData para mantener una lista de verificaciones que puede ser observada
    private val _verifications = MutableLiveData(listOf<RouteClosureVerification>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val verifications: LiveData<List<RouteClosureVerification>> get() = _verifications

    // Mutable LiveData para manejar el estado de habilitación del botón "Continuar"
    val isContinueButtonEnabled = MutableLiveData(false)

    init {
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

    // Función que verifica si todas las condiciones están satisfechas
    fun checkAllConditions() {
        val allButtonsAreOn = _verifications.value!!.all { it.isChecked == 1 }
        val atLeastDriverSignature = isDrawingView1Drawn.value == true

        isContinueButtonEnabled.value = allButtonsAreOn && atLeastDriverSignature
    }

    private fun initVerifications() {
        _verifications.value = listOf(
            RouteClosureVerification(1, "Revisar Estado de Mangueras. (No presenta daños evidentes a la vista, desgaste anormal evidente, sin presencia de fugas)", 0, symptomsList = listOf("Balatas Cristalizadas", "Golpe en el motor"), criticality = "Crítico"),
            RouteClosureVerification(2, "Revisar estado de Boquilla de descarga. (No presenta daños evidentes en sus componentes que impidan el correcto funcionamiento ni fugas)", 0, symptomsList = listOf("Fugas de GLP", "Quebrado", "Trizado"), criticality = "Crítico"),
            RouteClosureVerification(3, "Revisar Válvula de extracción (Venteo) se encuentra bien cerrada y con sello.", 0, symptomsList = listOf("Fugas de GLP", "Quebrado", "Válvulas pegadas", "Válvula no abre"), criticality = "Crítico"),
            RouteClosureVerification(4, "Revisar operación Válvula principal (activar sistema neumático, verificar visualmente que el pistón del sistema neumático opere correctamente).", 0, symptomsList = listOf("Cortocircuito", "Ruido anormal", "Fuga de aire", "Mangueras cortadas", "Válvulas pegadas"), criticality = "Crítico"),
            RouteClosureVerification(5, "Verificar funcionamiento de control remoto BASE a 3 metros de distancia. (Cambiar pilas si se requiere) Verificar funcionamiento de cortacorriente: detención del camión y cierre de válvula principal.", 0, symptomsList = listOf("Cortocircuito", "Ruido anormal"), criticality = "Crítico"),
            RouteClosureVerification(6, "Verificar funcionamiento de sistema de seguridad de puertas traseras. Al quedar abiertas, cumplen la función de freno de mano. (para camiones que cuente con este sistema)", 0, symptomsList = listOf("Cortocircuito", "Mangueras cortadas"), criticality = "Crítico"),
            RouteClosureVerification(7, "Verificar que todas las válvulas del sistema de gas se encuentren correctamente cerradas y no presentan fugas.", 0, symptomsList = listOf("Fugas de GLP", "Válvulas pegadas"), criticality = "Crítico"),
            RouteClosureVerification(8, "Verificar que camión cuenta con destrabador, copla de seguridad y solución jabonosa para realizar pruebas.", 0, symptomsList = listOf("Cortocircuito"), criticality = "Crítico"),
            RouteClosureVerification(9, "Verificar vigencia de documentación normativa del vehículo (Revisión Técnica, Permiso de Circulación, Seguro, HDS, Inscripción SEC camión, certificación tanque y válvulas) y autorización de ayudante y conductor.", 0, symptomsList = listOf("Cortocircuito"), criticality = "No Crítico"),
            RouteClosureVerification(10, "Verificar niveles dentro del rango adecuado de aceites hidráulicos, lubricantes, combustible y agua.", 0, symptomsList = listOf("Fuga de aceite", "Fuga de petroleo", "Luz check engine", "Perdida de agua"), criticality = "Solución rápida"),
            RouteClosureVerification(11, "Revisar estado neumáticos delanteros y traseros (desgaste anormal, daños evidentes a la vista, desformaciones).", 0, symptomsList = listOf("Deformado", "Desgaste excesivo", "Liso", "Pinchado"), criticality = "Solución rápida"),
            RouteClosureVerification(12, "Revisar estado Extintores (2 unidades). Verificar certificado, mantención vigente, estado de mangueras y nivel de carga.", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            RouteClosureVerification(13, "Corroborar conexión batería (bornes, tapa batería, soporte de batería)", 0, symptomsList = listOf("Cortocircuito", "Ayuda de partida"), criticality = "Solución rápida"),
            RouteClosureVerification(14, "Revisar que vehículo cuenta con cuñas (2)", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            RouteClosureVerification(15, "Revisar en caja de herramientas:(Gata, barra, llave de rueda).", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            RouteClosureVerification(16, "Verificar que vehículo cuenta con Conos (2 unidades).", 0, symptomsList = listOf("Cortocircuito"), criticality = "Solución rápida"),
            RouteClosureVerification(17, "Verificar operatividad del tacógrafo.", 0, symptomsList = listOf("Memoria llena", "No marca"), criticality = "Solución rápida"),
            RouteClosureVerification(18, "Verificar Stock Tapas amarillas.", 0, symptomsList = listOf("Piquete", "Quebrado", "Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(19, "Revisar estado de parabrisas", 0, symptomsList = listOf("Piquete", "Quebrado", "Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(20, "Revisar espejos retrovisores", 0, criticality = "No Crítico"),
            RouteClosureVerification(21, "Verificar Aseo Exterior vehículo", 0, criticality = "No Crítico"),
            RouteClosureVerification(22, "Verificar Aseo Interior vehículo", 0, symptomsList = listOf("Deformado","Desgaste excesivo","Liso","Pinchado"), criticality = "No Crítico"),
            RouteClosureVerification(23, "Verificar neumático de repuesto (que no esté suelto de su soporte).", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(24, "Verificar funcionamiento de luces delanteras", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(25, "Verificar funcionamiento de luces de emergencia", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(26, "Verificar tablero de instrumentos", 0, symptomsList = listOf("Cortocircuito","Intermitentes pegados","Luces quemadas","Piquete","Quebrado","Trizado"), criticality = "No Crítico"),
            RouteClosureVerification(27, "Verificar operación de frenos de servicio", 0, symptomsList = listOf("Balatas cristalizadas","Camión no frena","Ruido anormal","Vibración excesiva","Tambores sobre medida"), criticality = "No Crítico"),
            RouteClosureVerification(28, "Verificar operación de tacómetro.", 0, symptomsList = listOf("No marca","No transmite"), criticality = "No Crítico"),
            RouteClosureVerification(29, "Comprobar estado de la dirección.", 0, symptomsList = listOf("Juego excesivo","Perdida de aceite","Se carga a la derecha","Se carga a la izquierda","Vibración excesiva"), criticality = "No Crítico")
        )
    }
}