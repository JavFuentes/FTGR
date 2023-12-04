package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentLeaksProtocolBinding
import cl.abastible.ftgr.app.ui.adapter.LeaksProtocolAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.LeaksProtocolViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaksProtocolFragment : BaseFragment<FragmentLeaksProtocolBinding>() {
    private val viewModel: LeaksProtocolViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Protocolo Ante Fugas"

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Configurar el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Habilitando el botón de navegación hacia atrás
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(navigationIconDrawable, ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        // Inicializa el RecyclerView
        initRecyclerView()

        // Navega hacia el fragmento InspectionReportFragment
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_leaksProtocolFragment_to_inspectionReportFragment)
        }
    }

    private fun initRecyclerView() {
        val adapter = LeaksProtocolAdapter(listOf())
        binding.recyclerView.adapter = adapter

        viewModel.instructions.observe(viewLifecycleOwner) { instructions ->
            adapter.leaksProtocolList = instructions
            adapter.notifyDataSetChanged()
        }
    }

    // Maneja los clics en los ítems del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Manejar clic en el botón de navegación hacia atrás
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // Infla el binding del fragmento
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLeaksProtocolBinding.inflate(inflater, container, false)
 }
