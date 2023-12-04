package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentRouteClosure2Binding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.RouteClosure2ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteClosure2Fragment : BaseFragment<FragmentRouteClosure2Binding>() {
    // Inyección del ViewModel
    private val routeClosure2ViewModel: RouteClosure2ViewModel by viewModels()

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

        disableContinueButton()

        binding.btnCloseRoute.setOnClickListener {
            findNavController().navigate(R.id.action_routeClosure2Fragment_to_completedRouteClosureFragment)
        }

        binding.etFinalMileage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se implementa
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No se implementa
            }

            override fun afterTextChanged(s: Editable?) {
                routeClosure2ViewModel.textFromEditText1.value = s.toString()
            }
        })

        binding.etDischargedLiters.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se implementa
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No se implementa
            }

            override fun afterTextChanged(s: Editable?) {
                routeClosure2ViewModel.textFromEditText2.value = s.toString()
            }
        })

        routeClosure2ViewModel.textFromEditText1.observe(viewLifecycleOwner) {
            checkBothEditTextContent()
        }

        routeClosure2ViewModel.textFromEditText2.observe(viewLifecycleOwner) {
            checkBothEditTextContent()
        }
    }

    private fun checkBothEditTextContent() {
        if(routeClosure2ViewModel.textFromEditText1.value!!.isNotEmpty() &&
            routeClosure2ViewModel.textFromEditText2.value!!.isNotEmpty()) {
            enableContinueButton()
        } else {
            disableContinueButton()
        }
    }

    // Habilita el botón "Continuar" y cambia su color de fondo
    private fun enableContinueButton() {
        binding.btnCloseRoute.isEnabled = true
        binding.btnCloseRoute.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.enabled_button))
    }

    // Deshabilita el botón "Continuar" y cambia su color de fondo
    private fun disableContinueButton() {
        binding.btnCloseRoute.isEnabled = false
        binding.btnCloseRoute.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.disabled_button))
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    // Infla el layout del fragmento
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRouteClosure2Binding.inflate(inflater, container, false)
}