package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.abastible.ftgr.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    // Mutable LiveData para mantener una lista de usuarios que puede ser observada
    private val _users = MutableLiveData(listOf<User>())

    // LiveData expuesta para ser observada por otros componentes (solo lectura)
    val users: LiveData<List<User>> get() = _users

    init {
        // Inicializa la lista de usuarios al crear la instancia del ViewModel
        initUsers()
    }

    private fun initUsers(){
        // Asigna una lista de usuarios inicial a _users
        _users.value = listOf(
            User(1, "David Funes", "00166963214","123456"),
            User(2, "Mario Juarez", "00554112563","123456"),
            User(3, "Myriam Ojeda", "00969554521","123456"),
            User(4, "Lisa Morin", "00155477484","123456"),
            User(5, "Roberto Palma", "00116599831","123456")
        )
    }
}
