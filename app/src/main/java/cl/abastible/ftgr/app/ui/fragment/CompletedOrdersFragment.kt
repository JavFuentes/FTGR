package cl.abastible.ftgr.app.ui.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentCompletedOrdersBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment

class CompletedOrdersFragment : BaseFragment<FragmentCompletedOrdersBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estableciendo la Toolbar como ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Cambiando el color del texto del título a blanco
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Agenda"

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        binding.btnCloseRoute.setOnClickListener {
            findNavController().navigate(R.id.action_completedOrdersFragment_to_routeClosureFragment)
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCompletedOrdersBinding.inflate(inflater, container, false)
}