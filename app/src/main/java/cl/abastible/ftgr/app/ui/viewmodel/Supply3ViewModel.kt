package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.SupplyVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Supply3ViewModel @Inject constructor() : ViewModel() {
    // Mutable LiveData para mantener una lista de verificaciones que puede ser observada
    private val _verifications = MutableLiveData(listOf<SupplyVerification>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val verifications: LiveData<List<SupplyVerification>> get() = _verifications

    // Elemento seleccionado
    val selectedItem = MutableLiveData<SupplyVerification?>()

    // Mutable LiveData para manejar el estado de habilitación del botón "Continuar"
    val isContinueButtonEnabled = MutableLiveData(false)

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
        isContinueButtonEnabled.value = allButtonsAreOn
    }

    private fun initVerifications() {
        _verifications.value = listOf(
            SupplyVerification(idVerification = 12, description = "Asegurese de que no haya escape de gas en válvula de llenado ni otros accesorios del tanque", stage = 3, 0),
            SupplyVerification(idVerification = 13, description = "Cerrar todas la válvulas del sistema de gas del camión que fueron abiertas para realizar la entrega de gas.", stage = 3, 0)
        )
    }
}