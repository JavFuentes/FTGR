package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentCreatedConsignmentBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatedConsignmentFragment : BaseFragment<FragmentCreatedConsignmentBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurando la Toolbar
        setUpToolbar()

        binding.btnPrintConsignment.setOnClickListener {
            showPrintDialog()
        }
    }

    private fun showStartRouteDialog() {
        val routeDialogBuilder = AlertDialog.Builder(requireContext())
        routeDialogBuilder.setTitle("¿DESEA INICIAR RUTA?")

        routeDialogBuilder.setPositiveButton("ACEPTAR") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_createdConsignmentFragment_to_agendaFragment)
        }

        routeDialogBuilder.show()
    }

    private fun showPrintDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("IMPRESIÓN")
        dialogBuilder.setMessage("¿La impresión se ha realizado correctamente?")

        dialogBuilder.setPositiveButton("SÍ") { dialog, _ ->
            dialog.dismiss()
            showStartRouteDialog()
        }

        dialogBuilder.setNegativeButton("REINTENTAR") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(requireContext(), "Reintentando impresión", Toast.LENGTH_SHORT).show()
        }

        dialogBuilder.show()
    }

    private fun setUpToolbar() {
        // Estableciendo la Toolbar como ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Cambiando el color del texto del título a blanco
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Consignación"

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                // Manejar clic en el ítem "Menu"
                true
            }
            else -> false
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreatedConsignmentBinding.inflate(inflater, container, false)
}