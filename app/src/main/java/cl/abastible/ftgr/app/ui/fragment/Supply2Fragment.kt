package cl.abastible.ftgr.app.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentSupply2Binding
import cl.abastible.ftgr.app.ui.adapter.Supply2Adapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.Supply2ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Supply2Fragment : BaseFragment<FragmentSupply2Binding>() {
    // Inyecta el ViewModel
    private val supply2ViewModel: Supply2ViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Configuración de la toolbar
        setUpToolbar()

        // Observa los cambios en la lista de verificaciones
        supply2ViewModel.verifications.observe(viewLifecycleOwner) { verifications ->
            val adapter = Supply2Adapter(verifications, supply2ViewModel, requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Configurar el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.btnContinue.setOnClickListener {
            // Comprobar si alguna pregunta no ha sido respondida
            if (supply2ViewModel.verifications.value!!.any { it.isChecked == 0 }) {
                showQuestionsNotAnsweredDialog()
            } else if (supply2ViewModel.verifications.value!!.all { it.isChecked == 1 }) {
                // Navegar al siguiente fragmento si todas las verificaciones son afirmativas
                findNavController().navigate(R.id.action_supply2Fragment_to_orderDetail2Fragment)
            } else {
                // Mostrar un dialog si alguna verificación es negativa
                showWarningDialog()
            }
        }
    }

    // Función para mostrar el diálogo de preguntas no respondidas
    private fun showQuestionsNotAnsweredDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("PREGUNTAS")
            .setMessage("DEBE CONTESTAR TODAS LAS PREGUNTAS")
            .setPositiveButton("ACEPTAR", null)
            .show()
    }

    private fun showWarningDialog() {
        val message = SpannableString("NO PODRÁ ABASTECER AL CLIENTE\nEXISTEN PREGUNTAS MARCADAS CON NO CUMPLE")
        val start = message.indexOf("NO CUMPLE")
        val end = start + "NO CUMPLE".length
        message.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        AlertDialog.Builder(requireContext())
            .setTitle("¿ESTÁ SEGURO EN CONTINUAR?")
            .setMessage(message)
            .setPositiveButton("SI") { _, _ ->
                // Navegar al fragmento de Devolución
                findNavController().navigate(R.id.action_supply2Fragment_to_returnFragment)
            }
            .setNegativeButton("NO", null) // Cierra el diálogo sin hacer nada
            .show()
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Pasos seguros de abastecimiento (2)"

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

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
        inflater.inflate(R.menu.menu, menu)
    }

    // Maneja las selecciones del menú de opciones
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Manejar clic en el botón de navegación hacia atrás
                findNavController().navigateUp()
                true
            }
            R.id.menu -> {
                ///// Activa todos los switches. (ONLY TESTING) /////
                activateAllSwitches()
                true
            }
            else -> false
        }
    }

    ///// Activa todos los switches. (ONLY TESTING) /////
    private fun activateAllSwitches() {
        val adapter = binding.recyclerView.adapter as? Supply2Adapter
        adapter?.activateAllSwitches()
    }

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSupply2Binding.inflate(inflater, container, false)
}
