package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConsignmentViewModel @Inject constructor() : ViewModel() {
    // MutableLiveDatas para manejar el estado de los interruptores
    private val _isEditText1Filled = MutableLiveData<Boolean>()
    private val _isEditText2Filled = MutableLiveData<Boolean>()

    // Exponer los MutableLiveDatas como LiveData para evitar modificaciones desde fuera de este ViewModel
    val isEditText1Filled: LiveData<Boolean> get() = _isEditText1Filled
    val isEditText2Filled: LiveData<Boolean> get() = _isEditText2Filled

    // Variable privada MutableLiveData para manejar el estado del botón de continuar
    private val _isContinueButtonEnabled = MutableLiveData<Boolean>()

    // Exponer el MutableLiveData como LiveData para evitar modificaciones desde fuera de este ViewModel.
    val isContinueButtonEnabled: LiveData<Boolean> get() = _isContinueButtonEnabled

    // Capacidad máxima del camión para testeo
    val truckMaxCapacity: MutableLiveData<Float> = MutableLiveData(10400f)

    // Variable privada MutableLiveData para manejar el estado del modo de edición
    private val _isEditingEnabled = MutableLiveData(false)

    // Exponer el MutableLiveData como LiveData para evitar modificaciones desde fuera de este ViewModel.
    val isEditingEnabled: LiveData<Boolean> get() = _isEditingEnabled

    // Función para cambiar el estado del modo de edición
    fun toggleEditingMode() {
        _isEditingEnabled.value = !(_isEditingEnabled.value ?: false)
    }

    // Funciones para configurar los observadores de los EditTexts
    fun updateEditText1Status(text: CharSequence?) {    _isEditText1Filled.value = !text.isNullOrEmpty()    }
    fun updateEditText2Status(text: CharSequence?) {    _isEditText2Filled.value = !text.isNullOrEmpty()    }

    // Función para comprobar el estado de varios interruptores y habilitar o deshabilitar el botón de continuar.
    fun checkSwitchesAndEnableButton(
        isVentingValveChecked: Boolean,
        isLiterCounterCoverChecked: Boolean,
        isMeasuringMechanismChecked: Boolean
    ) {
        // El botón de continuar será habilitado solo si todos los interruptores, EditText y porcentaje están dentro del rango requerido.
        _isContinueButtonEnabled.value = isVentingValveChecked && isLiterCounterCoverChecked &&
                isMeasuringMechanismChecked && _isEditText1Filled.value == true &&
                _isEditText2Filled.value == true
    }

    fun calculateLitersFromPercentage(currentPercentage: Int, initialLiters: Float, initialPercentage: Int): Float {
        return (initialLiters / initialPercentage) * currentPercentage
    }
}
