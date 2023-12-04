package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor() : ViewModel() {


    val menuItems = listOf("Reimprimir cierre", "Cerrar ruta", "Log", "Cerrar sesión")
    val isMenuVisible = MutableLiveData<Boolean>(false)

    fun toggleMenuVisibility() {
        isMenuVisible.value = !(isMenuVisible.value ?: false)
    }

    // Mutable LiveData para mantener una lista de pedidos que puede ser observada
    private val _orders = MutableLiveData(listOf<Order>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val orders: LiveData<List<Order>> get() = _orders

    /////Función que asigna el estado completa a todos los pedidos automáticamente (TEST ONLY) /////
    fun completeAllOrders() {
        val updatedOrders = _orders.value?.map { order ->
            order.copy(status = "completado")
        }
        _orders.value = updatedOrders
    }

    // Verifica si no hay estados pendientes
    fun areAllOrdersFinalized(): Boolean {
        val finalizedStates = listOf("completado", "cancelado", "rechazado")
        return _orders.value?.all { it.status in finalizedStates } == true
    }

    // Inicializa la lista de pedidos al crear la instancia del ViewModel
    init {
        initOrders()
    }

    private fun initOrders() {
        _orders.value = listOf(
            Order(
                1,
                "Jorge Alejandro",
                "Pasaje Niquel Cromado",
                "Maipu",
                "07:00 - 22:00",
                "001162654",
                "en curso",
                "Planta Maipu",
                false
            ),
            Order(
                2,
                "María José",
                "Calle Los Alpes",
                "Las Condes",
                "09:00 - 18:00",
                "001162655",
                "completado",
                "Centro de Distribución",
                false
            ),
            Order(
                3,
                "Carlos Rodriguez",
                "Av. Libertador",
                "Providencia",
                "10:00 - 20:00",
                "001162656",
                "pendiente",
                "Planta Vitacura",
                true
            ),
            Order(
                4,
                "Ana González",
                "Calle Los Jazmines",
                "La Reina",
                "11:00 - 21:00",
                "001162657",
                "atrasado",
                "Planta La Reina",
                false
            ),
            Order(
                5,
                "Patricio Herrera",
                "Av. Los Presidentes",
                "Peñalolén",
                "12:00 - 22:00",
                "001162658",
                "pendiente",
                "Planta Peñalolén",
                false
            ),
            Order(
                6,
                "Lucía Fernández",
                "Calle San Martín",
                "Ñuñoa",
                "13:00 - 23:00",
                "001162659",
                "cancelado",
                "Centro de Distribución",
                true
            ),
            Order(
                7,
                "Rodrigo Soto",
                "Av. Providencia",
                "Providencia",
                "14:00 - 00:00",
                "001162660",
                "pendiente",
                "Planta Providencia",
                false
            ),
            Order(
                8,
                "Cecilia Vargas",
                "Calle Santa María",
                "Las Condes",
                "15:00 - 01:00",
                "001162661",
                "rechazado",
                "Centro de Distribución",
                false
            ),
            Order(
                9,
                "Miguel Ángel",
                "Calle Los Carrera",
                "Maipu",
                "16:00 - 02:00",
                "001162662",
                "pendiente",
                "Planta Maipu",
                false
            ),
            Order(
                10,
                "Valentina Muñoz",
                "Av. La Florida",
                "La Florida",
                "17:00 - 03:00",
                "001162663",
                "pendiente",
                "Planta La Florida",
                false
            )
        )
    }
}