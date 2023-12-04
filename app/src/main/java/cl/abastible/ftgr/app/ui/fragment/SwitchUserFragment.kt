package cl.abastible.ftgr.app.ui.fragment

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.data.local.SharedPreferencesManager
import cl.abastible.ftgr.databinding.FragmentSwitchUserBinding
import cl.abastible.ftgr.app.ui.adapter.UserAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SwitchUserFragment : BaseFragment<FragmentSwitchUserBinding>() {

    // Inyección de ViewModel y SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    // Callback para escuchar los cambios de destino del NavController
    private val callback: NavController.OnDestinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if (destination.id == R.id.loginFragment) {
            // Guardar la información del usuario seleccionado en SharedPreferences cuando se navega a LoginFragment
            saveSelectedUserInfoToSharedPreferences()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de la barra de herramientas
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Cambiar de usuario"
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            // Navegar de regreso a LoginFragment cuando se presiona el icono de navegación
            findNavController().navigate(R.id.action_switchUserFragment_to_loginFragment)
        }

        // Habilitar la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        // Configurar el LayoutManager para el RecyclerView
        binding.userRecyclerView.layoutManager = LinearLayoutManager(context)

        // Cargar la información del usuario actual desde SharedPreferences
        loadCurrentUser()

        // Observar los cambios en la lista de usuarios
        userViewModel.users.observe(viewLifecycleOwner, { users ->
            val adapter = UserAdapter(users) { selectedUser ->
                // Configurar la vista del usuario seleccionado cuando se selecciona un usuario
                val selectedUserCardView: View = binding.nestedScrollView.findViewById(R.id.selected_user_cardview)
                val tvSelectedName: TextView = selectedUserCardView.findViewById(R.id.tv_selected_name)
                val tvSelectedUsername: TextView = selectedUserCardView.findViewById(R.id.tv_selected_user_name)

                tvSelectedName.text = selectedUser.name
                tvSelectedUsername.text = selectedUser.username
            }
            binding.userRecyclerView.adapter = adapter
        })

        // Añadir un listener para los cambios de destino del NavController
        val navController = findNavController()
        navController.addOnDestinationChangedListener(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_menu, menu)
        // Configuración de los íconos del menú
        menu.findItem(R.id.add_user)?.icon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_ATOP)
        menu.findItem(R.id.search)?.icon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_ATOP)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar las selecciones de los ítems del menú
        return when (item.itemId) {
            R.id.add_user -> {
                // Manejar la selección del ítem "Agregar usuario"
                true
            }
            R.id.search -> {
                // Manejar la selección del ítem "Buscar"
                true
            }
            else -> false
        }
    }

    private fun loadCurrentUser() {
        // Cargar y configurar la vista del usuario seleccionado desde SharedPreferences
        val selectedUserCardView: View = binding.nestedScrollView.findViewById(R.id.selected_user_cardview)
        val tvSelectedName: TextView = selectedUserCardView.findViewById(R.id.tv_selected_name)
        val tvSelectedUsername: TextView = selectedUserCardView.findViewById(R.id.tv_selected_user_name)

        tvSelectedName.text = sharedPreferencesManager.name
        tvSelectedUsername.text = sharedPreferencesManager.username
    }

    private fun saveSelectedUserInfoToSharedPreferences() {
        // Guardar la información del usuario seleccionado en SharedPreferences
        val selectedUserCardView: View = binding.nestedScrollView.findViewById(R.id.selected_user_cardview)
        val tvSelectedName: TextView = selectedUserCardView.findViewById(R.id.tv_selected_name)
        val tvSelectedUsername: TextView = selectedUserCardView.findViewById(R.id.tv_selected_user_name)

        val name = tvSelectedName.text.toString()
        val username = tvSelectedUsername.text.toString()

        sharedPreferencesManager.name = name
        sharedPreferencesManager.username = username
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSwitchUserBinding.inflate(inflater, container, false)

    override fun onDestroyView() {
        super.onDestroyView()
        // Remover el listener de cambios de destino del NavController al destruir la vista
        findNavController().removeOnDestinationChangedListener(callback)
    }
}
