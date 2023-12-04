package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.ClosingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // Lista para almacenar las descargas
    val dischargedLitersList = MutableLiveData<MutableList<Float>>(mutableListOf())

    // LiveData para los estados de cierre
    private val _closingState = MutableLiveData<List<ClosingState>>()

    // LiveData para las condiciones de pago
    private val _paymentConditions = MutableLiveData<List<String>>()
    val paymentConditions: LiveData<List<String>> = _paymentConditions

    // LiveData para los métodos de pago
    private val _paymentMethods = MutableLiveData<List<String>>()
    val paymentMethods: LiveData<List<String>> = _paymentMethods

    // LiveData para los tipos de documento
    private val _documentTypes = MutableLiveData<List<String>>()
    val documentTypes: LiveData<List<String>> = _documentTypes

    // LiveData para las coordenadas
    private val _locationCoordinates = MutableLiveData<Pair<Double, Double>>()
    val locationCoordinates: LiveData<Pair<Double, Double>> = _locationCoordinates

    // LiveData para el número de teléfono del cliente
    private val _clientPhoneNumber = MutableLiveData<String>()
    val clientPhoneNumber: LiveData<String> get() = _clientPhoneNumber

    // MutableLiveData que almacena el valor total de la venta
    private val _totalValue = MutableLiveData<Float>()
    val totalValue: LiveData<Float> = _totalValue

    // Para la condición de pago
    private val _selectedPaymentCondition = MutableLiveData<String>(null)
    val selectedPaymentCondition: LiveData<String> get() = _selectedPaymentCondition

    // Para el método de pago
    private val _selectedPaymentMethod = MutableLiveData<String>(null)
    val selectedPaymentMethod: LiveData<String> get() = _selectedPaymentMethod

    // Para el tipo de documento
    private val _selectedDocumentType = MutableLiveData<String?>(null)
    val selectedDocumentType: LiveData<String?> get() = _selectedDocumentType

    // Para el porcentaje inicial
    val initialPercentage = MutableLiveData<String>()

    // Para el porcentaje final
    val finalPercentage = MutableLiveData<String>()

    // Función para obtener los estados de cierre filtrados
    fun getFilteredClosingStates(): LiveData<List<ClosingState>> {
        val filteredStates = MutableLiveData<List<ClosingState>>()
        _closingState.observeForever { states ->
            filteredStates.value = states.filter { it.customerVisit }
        }
        return filteredStates
    }

    fun addDischarge(liters: Float) {
        val currentList = dischargedLitersList.value ?: mutableListOf()
        currentList.add(liters)
        dischargedLitersList.value = currentList
        // Aquí podrías calcular el total y actualizar otra LiveData si es necesario
    }

    fun updateDischarge(position: Int, newAmount: Float) {
        dischargedLitersList.value?.let {
            if (it.size > position) {
                it[position] = newAmount
                dischargedLitersList.value = it
            }
        }
    }

    fun removeDischarge(position: Int) {
        dischargedLitersList.value?.let {
            if (it.size > position) {
                it.removeAt(position)
                dischargedLitersList.value = it
            }
        }
    }

    fun selectDocumentType(type: String) {
        _selectedDocumentType.value = type
    }

    fun selectPaymentCondition(condition: String) {
        _selectedPaymentCondition.value = condition
    }

    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    init {
        // Inicializar los datos de ejemplo
        _paymentConditions.value = listOf("30 días", "Contado", "45 días")
        _paymentMethods.value = listOf("Efectivo", "Débito", "Crédito")
        _documentTypes.value = listOf("Guía de despacho")

        // Establecer teléfono de prueba
        _clientPhoneNumber.value = "1234567890"

        // Establecer coordenadas de prueba
        _locationCoordinates.value = Pair(-33.51150750330, -70.78601599241)

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

