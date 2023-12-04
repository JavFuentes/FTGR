package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.databinding.FragmentSynchronizationBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import cl.abastible.ftgr.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SynchronizationFragment : BaseFragment<FragmentSynchronizationBinding>() {
    //Variable que conoce el estado de la sincronización
    private var isSynchronizationComplete = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Estableciendo la Toolbar como ActionBar
        setUpToolbar()

        // Sincronizando
        synchronization()

        // Inhabilitando inicialmente el botón "Continuar"
        binding.btnContinue.isEnabled = false
        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.disabled_button))

        // Estableciendo el escuchador de clics para el botón "Continuar"
        binding.btnContinue.setOnClickListener {
            // Mostrar el AlertDialog
            showChecklistAlertDialog()
        }
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Cambiando el color del texto del título a blanco
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Sincronización"

        // Habilitando el botón de navegación hacia atrás
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(navigationIconDrawable, ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sync_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Manejar clic en el botón de navegación hacia atrás
                findNavController().navigateUp()
                true
            }
            R.id.sync -> {

                    synchronization()

                true
            }
            R.id.menu -> {
                // Manejar clic en el ítem "Menu"
                true
            }
            else -> false
        }
    }

    private fun showChecklistAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("CHECKLIST CAMIÓN")
        builder.setMessage("CHECKLIST CAMIÓN DE INICIO DE RUTA")

        // Botón para iniciar
        builder.setPositiveButton("INICIAR") { _, _ ->
            // Acción que se ejecutará cuando se haga clic en "Iniciar"
            findNavController().navigate(R.id.action_synchronizationFragment_to_truckCheckListFragment)
        }

        // Botón para cerrar
        builder.setNegativeButton("CERRAR") { dialog, _ ->
            // Acción que se ejecutará cuando se haga clic en "Cerrar"
            dialog.dismiss()
        }

        // Mostrar el AlertDialog
        builder.create().show()
    }

    private fun synchronization() {
        // Verificamos si los TextViews ya son visibles, si no, procedemos con la sincronización
        if(!isSynchronizationComplete) {
            // Haciendo visibles los progress bars
            binding.progressCircularTransport.visibility = View.VISIBLE
            binding.progressCircularOrders.visibility = View.VISIBLE
            binding.progressCircularSheets.visibility = View.VISIBLE
            binding.progressCircularConfiguration.visibility = View.VISIBLE
            binding.progressCircularForms.visibility = View.VISIBLE

            // Crear un Handler y postear un Runnable que se ejecute después de 2000 milisegundos (2 segundos)
            Handler(Looper.getMainLooper()).postDelayed({
                // Haciendo invisibles los progress bars después de 2 segundos
                binding.progressCircularTransport.visibility = View.GONE
                binding.progressCircularOrders.visibility = View.GONE
                binding.progressCircularSheets.visibility = View.GONE
                binding.progressCircularConfiguration.visibility = View.GONE
                binding.progressCircularForms.visibility = View.GONE

                // Hacer visibles los TextViews después de que los progress bars se han hecho invisibles
                binding.tvLoadingTransport.visibility = View.VISIBLE
                binding.tvLoadingOrders.visibility = View.VISIBLE
                binding.tvLoadingSheets.visibility = View.VISIBLE
                binding.tvLoadingConfiguration.visibility = View.VISIBLE
                binding.tvLoadingForms.visibility = View.VISIBLE

                // Habilitar el botón "Continuar" después de que los TextViews se han hecho visibles
                binding.btnContinue.isEnabled = true
                binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.enabled_button))

                // Cambiando el valor de la bandera a true ya que los TextViews ya están visibles
                isSynchronizationComplete = true
            }, 2000)
        }
    }

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSynchronizationBinding.inflate(inflater, container, false)
}
