package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.ClosingState
import cl.abastible.ftgr.app.domain.model.LeaksProtocolItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaksProtocol2ViewModel @Inject constructor() : ViewModel() {
    // Mutable LiveData para mantener una lista de instrucciones que puede ser observada
    private val _instructions = MutableLiveData(listOf<LeaksProtocolItem>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val instructions: LiveData<List<LeaksProtocolItem>> get() = _instructions

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

    init {
        _instructions.value = listOf(
            LeaksProtocolItem( 1, "Para detectar fugas en la instalación aplique el siguiente protocolo: "),
            LeaksProtocolItem( 2, "1.- Si detecta olor a gas , verifique mediante la aplicación de agua jabonosa en conexiones y accesorios del tanque."),
            LeaksProtocolItem( 3, "2.- Verifique posible fuga en cañería a la vista y accesorios de la instalación, en un radio de hasta 4,5 metros del tanque."),
            LeaksProtocolItem( 4, "3.- Si detecta fuga, suspender el abastecimiento en forma inmediata."),
            LeaksProtocolItem( 5, "4.- Dar aviso al cliente."),
            LeaksProtocolItem( 6, "5.- Informar al servicio de emergencias de Abastible."),
            LeaksProtocolItem( 7, "6.- El olor a gas puede provenir del camión, por lo que si no detecta fugas en la instalación, revise los accesorios y mangueras del camión."),
            LeaksProtocolItem( 8, "7.- En caso de detectar fuga en el camión, suspenda inmediatamente el abastecimiento, informe al cliente y retorne a la dependencia de Abastible.")
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
