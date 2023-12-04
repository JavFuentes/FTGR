package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.LeaksProtocolItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaksProtocolViewModel @Inject constructor() : ViewModel() {
    // Mutable LiveData para mantener una lista de instrucciones que puede ser observada
    private val _instructions = MutableLiveData(listOf<LeaksProtocolItem>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val instructions: LiveData<List<LeaksProtocolItem>> get() = _instructions

    init {
        // Inicializa la lista de verificaciones al crear la instancia del ViewModel
        initVerifications()
    }
    private fun initVerifications() {
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
    }
}
