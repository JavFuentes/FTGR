package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentCompletedRouteClosureBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment

class CompletedRouteClosureFragment : BaseFragment<FragmentCompletedRouteClosureBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estableciendo la Toolbar como ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Cambiando el color del texto del título a blanco
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Cierre de Ruta"

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_completedRouteClosureFragment_to_loginFragment)
        }
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCompletedRouteClosureBinding.inflate(inflater, container, false)
}