package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.SupplyVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Supply2ViewModel @Inject constructor() : ViewModel() {

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

    fun checkAllConditions() {
        val allButtonsAreOn = _verifications.value!!.all { it.isChecked == 1 }
        isContinueButtonEnabled.value = allButtonsAreOn
    }

    private fun initVerifications() {
        _verifications.value = listOf(
            SupplyVerification(idVerification = 10, description = "Si es Supercañon, abrir indicador de máximo nivel y comprobar que emita sonido. Si no sale gas, cerrar válvula de servicio y cargar gas. Solicitar al cliente que abra válvula de servicio y pruebe los artefactos (durante 10 segundos)", stage = 2,0),
            SupplyVerification(idVerification = 11, description = "Abrir imagen e indicar si cumplió los pasos", stage = 2,0),
            SupplyVerification(idVerification = 12, description = "No debe haber fuegos abiertos (llama a la vista una distancia menor a 4,5 metros del camión, de la manguera ni del tanque del cliente.", stage = 2,0),
            SupplyVerification(idVerification = 13, description = "Asegurar que no haya otras igniciones peligrosas, como soldaduras al arco, calefactores, herramientas eléctricas, etc.", stage = 2,0),
            SupplyVerification(idVerification = 14, description = "Instruir al cliente sobre NO FUMAR y NO UTILIZAR teléfonos celulares mientras dure la carga de gas.", stage = 2,0),
            SupplyVerification(idVerification = 15, description = "Conectar boquilla en válvula de llenado", stage = 2,0),
            SupplyVerification(idVerification = 16, description = "Transferir gas", stage = 2,0),
            SupplyVerification(idVerification = 17, description = "NO sobrepasar 80% de máximo nivel. Mantener indicador de máximo nivel abierto para comprobar la correcta operación del instrumento indicador de nivel.", stage = 2),
            SupplyVerification(idVerification = 18, description = "Una vez terminado. DESPICHAR mínimo 15 segundos.", stage = 2,0),
            SupplyVerification(idVerification = 19, description = "Desconectar Boquilla.", stage = 2,0),
            SupplyVerification(idVerification = 20, description = "Asegurarse que no haya escape de gas en válvula de llenado ni otros accesorios del Tanque. USE agua jabonosa.", stage = 2,0),
            SupplyVerification(idVerification = 21, description = "Cerrar todas la válvulas del sistema de gas del camión que fueron abiertas para realizar la entrega de gas.", stage = 2,0),
        )
    }
}