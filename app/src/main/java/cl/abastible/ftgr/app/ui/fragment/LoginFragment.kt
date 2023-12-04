package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentLoginBinding
import cl.abastible.ftgr.app.data.local.SharedPreferencesManager
import cl.abastible.ftgr.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Utilizando la anotación AndroidEntryPoint para indicar a Hilt dónde puede empezar a inyectar dependencias.
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    // Inyectando la instancia de SharedPreferencesManager con la ayuda de Hilt.
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener los valores almacenados en SharedPreferences y establecerlos en los TextViews
        binding.tvName.text = sharedPreferencesManager.name ?: "Elvira Petrova"
        binding.tvUserName.text = sharedPreferencesManager.username ?: "001165254"

        // Estableciendo el listener para el botón de login. Este listener verifica la última fecha de sincronización y navega a la pantalla correspondiente dependiendo de si la fecha actual coincide con la última fecha de sincronización o no.
        binding.btnLogin.setOnClickListener {
            val enteredPassword = binding.etPassword.text.toString()

            // Verificando la contraseña
            if (enteredPassword == "1234") {
                // Contraseña correcta
                binding.tvPasswordError.visibility = View.GONE

                // Aquí va tu lógica existente para navegar al siguiente fragmento
                val currentDate = sharedPreferencesManager.getCurrentDate()
                val lastSyncDate = sharedPreferencesManager.getLastSyncDate()

                if (currentDate == lastSyncDate) {
                    findNavController().navigate(R.id.action_loginFragment_to_agendaFragment)
                } else {
                    sharedPreferencesManager.setLastSyncDate(currentDate)
                    findNavController().navigate(R.id.action_loginFragment_to_synchronizationFragment)
                }
            } else {
                // Contraseña incorrecta
                binding.tvPasswordError.visibility = View.VISIBLE

                // Limpiar el EditText de la contraseña
                binding.etPassword.setText("")
            }
        }

        // Estableciendo el listener para el botón de cambiar usuario, que navega al fragmento para cambiar de usuario cuando se hace clic en él, además de almacenar los datos del usuario en las preferencias compartidas.
        binding.btnSwitchUser.setOnClickListener {

            // Aquí, necesitas obtener los valores de tv_name y tv_user_name.
            val name = binding.tvName.text.toString()
            val userName = binding.tvUserName.text.toString()

            // Almacenando los valores en SharedPreferences
            sharedPreferencesManager.name = name
            sharedPreferencesManager.username = userName

            // Navegando al fragmento de cambio de usuario
            findNavController().navigate(R.id.action_loginFragment_to_switchUserFragment)
        }
    }

    // Inicializando el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)
}
