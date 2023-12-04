package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor() : ViewModel() {
    // MutableLiveData que almacena el valor total de la venta
    private val _totalValue = MutableLiveData<Float>()

    // LiveData que expone el valor total al fragmento
    val totalValue: LiveData<Float> = _totalValue

    // Función que calcula el valor total de la venta multiplicando el valor unitario
    // por los litros descargados y actualiza el valor de _totalValue con el resultado
    fun calculateTotalValue(unitValue: Float, dischargedLiters: Float) {
        _totalValue.value = unitValue * dischargedLiters
    }

    // Para la condición de pago
    private val _selectedPaymentCondition = MutableLiveData<String>(null)
    val selectedPaymentCondition: LiveData<String> get() = _selectedPaymentCondition

    // Para el método de pago
    private val _selectedPaymentMethod = MutableLiveData<String>(null)

    // Exposición de _selectedPaymentMethod como LiveData inmutable para la observación de cambios en el método de pago seleccionado
    val selectedPaymentMethod: LiveData<String> get() = _selectedPaymentMethod

    // Función para seleccionar y asignar la condición de pago
    fun selectPaymentCondition(condition: String) {
        _selectedPaymentCondition.value = condition
    }

    // Función para seleccionar y asignar el método de pago
    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }
}
