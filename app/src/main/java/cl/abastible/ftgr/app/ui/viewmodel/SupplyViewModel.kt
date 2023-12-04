package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.ClosingState
import cl.abastible.ftgr.app.domain.model.SupplyVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupplyViewModel @Inject constructor() : ViewModel() {
    // Mutable LiveData para mantener una lista de verificaciones que puede ser observada
    private val _verifications = MutableLiveData(listOf<SupplyVerification>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val verifications: LiveData<List<SupplyVerification>> get() = _verifications

    // Mutable LiveData para manejar el estado de habilitación del botón "Continuar"
    private val isContinueButtonEnabled = MutableLiveData(false)

    // LiveData para los estados de cierre
    private val _closingState = MutableLiveData<List<ClosingState>>()


    // LiveData para los estados de cierre filtrados
    fun getFilteredClosingStates(): LiveData<List<ClosingState>> {
        val filteredStates = MutableLiveData<List<ClosingState>>()
        _closingState.observeForever { states ->
            filteredStates.value = states.filter { it.customerVisit }
        }
        return filteredStates
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

    fun checkAllConditions() {
        val allButtonsAreOn = _verifications.value!!.all { it.isChecked == 1 }
        isContinueButtonEnabled.value = allButtonsAreOn
    }

    init {
        // Inicializa la lista de verificaciones
        _verifications.value = listOf(
            SupplyVerification(idVerification = 1, description = "Estacionar en lugar y forma apropiado", stage = 1, isChecked =  0),
            SupplyVerification(idVerification = 2, description = "Accionar el Freno de Mano", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 3, description = "Instalar cuña en la rueda", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 4, description = "Instalar Cono 1", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 5, description = "Instalar Cono 2, asegurar puertas de la caja de gas", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 6, description = "Instalar Letrero No fumar en el cono más visible", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 7, description = "Bajar e instalar extintor N°1 en el costado de la caja de gas del camión", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 8, description = "Bajar e instalar extintor N°2 en el costado del tanque a cargar", stage = 1,isChecked =  0),
            SupplyVerification(idVerification = 9, description = "Comprobar visualmente el estado exterior del tanque del cliente (buscar abolladuras, elementos extraños, etc.)", stage = 1,isChecked =  0)
        )

        // Establecer estados de cierre de prueba
        _closingState.value = listOf(
            ClosingState(1, "Creado (Dummy)", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(2, "Atendido por otra Compañía", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(3, "Camión en panne", customerVisit = false, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(4, "Camión sin gas", customerVisit = false, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(5, "Dirección no encontrada", customerVisit = false, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(6, "Estanque lleno", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(7, "Instalación en Metrogas", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(8, "Pedido anulado", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(9, "Pedido para otro tipo de camión", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(10, "Pedido cumplido o repetido", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(11, "Persona dice que no pidió gas", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(12, "Persona no apta para recibir", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(13, "Problema acceso a instalación", customerVisit = true, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(14, "Problema en instalación", customerVisit = true, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(15, "Problema con crédito", customerVisit = true, takeAPicture = true, rescheduleDispatch = false),
            ClosingState(16, "Sin dinero", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(17, "Sin estacionamiento", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(18, "Sin moradores", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(19, "Solicita entrega para otro día", customerVisit = true, takeAPicture = true, rescheduleDispatch = true),
            ClosingState(20, "Turno terminado", customerVisit = false, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(21, "Cerrado entregado", customerVisit = true, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(22, "Cambio de Transporte", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(23, "Problemas de Comunicación", customerVisit = false, takeAPicture = false, rescheduleDispatch = true),
            ClosingState(24, "Pedido Liberado Tardíamente", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(25, "Fecha errónea según calendario", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(26, "Problema de acceso vial", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(27, "Problema de embarque", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(28, "Tripulación reporta Problema de Salud", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(29, "Problema en equipo móvil", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(30, "Fuera de ventana horaria", customerVisit = false, takeAPicture = false, rescheduleDispatch = false),
            ClosingState(31, "Falta de tripulación disponible", customerVisit = false, takeAPicture = false, rescheduleDispatch = false)
        )
    }
}
