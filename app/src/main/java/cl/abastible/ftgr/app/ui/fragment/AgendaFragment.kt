package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentAgendaBinding
import cl.abastible.ftgr.app.domain.model.Order
import cl.abastible.ftgr.app.ui.adapter.OrderAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import  cl.abastible.ftgr.app.ui.viewmodel.AgendaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgendaFragment : BaseFragment<FragmentAgendaBinding>() {
    private val agendaViewModel: AgendaViewModel by viewModels()
    private val menuItems = listOf("REIMPRIMIR CIERRE", "CERRAR RUTA", "LOG", "CERRAR SESIÓN")
    private lateinit var menuAdapter: ArrayAdapter<String>
    private var isMenuVisible = false
    private lateinit var popupWindow: ListPopupWindow

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        agendaViewModel.orders.observe(viewLifecycleOwner) { orders ->
            val adapter = OrderAdapter(orders) { order ->
                navigateToOrderDetailFragment(order)
            }
            binding.recyclerView.adapter = adapter
            menuAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuItems)
        }

        binding.btnCancel.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cancel_orders, null)
            val builder = AlertDialog.Builder(requireContext())
                .setTitle("ANULAR TODOS LOS PEDIDOS")
                .setView(dialogView)
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("ACEPTAR") { _, _ ->
                }
            builder.create().show()
        }

        binding.truckChecklistScrollview.post {
            binding.truckChecklistScrollview.scrollTo(0, 0)
        }

        agendaViewModel.isMenuVisible.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                showPopupMenu()
            } else {
                dismissPopupMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    private fun handlePopupMenuItemClick(position: Int) {
        when (position) {
            0 -> {
                // Acciones para "Reimprimir cierre"
            }
            1 -> {
                findNavController().navigate(R.id.action_agendaFragment_to_completedOrdersFragment)
            }
            2 -> {
                // Acciones para "Log"
            }
            3 -> {
                findNavController().navigate(R.id.action_agendaFragment_to_loginFragment)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                agendaViewModel.toggleMenuVisibility()
                true
            }
            else -> false
        }
    }

    private fun togglePopupMenu() {
        Log.d("MenuToggle", "Toggle Menu called. Current state: $isMenuVisible")
        if (isMenuVisible) {
            dismissPopupMenu()
        } else {
            showPopupMenu()
        }
    }

    private fun dismissPopupMenu() {
        if (::popupWindow.isInitialized) {
            popupWindow.dismiss()
        }
    }

    private fun showPopupMenu() {
        val menuIcon: View = binding.toolbar.findViewById(R.id.menu)

        popupWindow = ListPopupWindow(requireContext())
        popupWindow.anchorView = menuIcon
        popupWindow.width = 250 * resources.displayMetrics.density.toInt()
        popupWindow.setAdapter(menuAdapter)
        popupWindow.setOnItemClickListener { _, _, position, _ ->
            handlePopupMenuItemClick(position)
            dismissPopupMenu()
        }

        // Establece el fondo del popup con el color deseado.
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_popupmenu))

        // Establece el listener antes de mostrar el popup.
        popupWindow.setOnDismissListener {
            isMenuVisible = false
            Log.d("MenuToggle", "Menu dismissed")
        }

        popupWindow.horizontalOffset = -popupWindow.width + menuIcon.width
        popupWindow.show()
        isMenuVisible = true
        Log.d("MenuToggle", "Menu shown")
    }

    private fun navigateToOrderDetailFragment(order: Order) {
        findNavController().navigate(R.id.action_agendaFragment_to_orderDetailFragment)
    }

    private fun setupToolbar(){
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Agenda"
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        setHasOptionsMenu(true)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAgendaBinding.inflate(inflater, container, false)
}
