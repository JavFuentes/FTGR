package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
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
import cl.abastible.ftgr.databinding.FragmentTruckChecklistBinding
import cl.abastible.ftgr.app.ui.adapter.TruckCheckListAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.customviews.DrawingView
import cl.abastible.ftgr.app.ui.viewmodel.TruckCheckListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TruckCheckListFragment : BaseFragment<FragmentTruckChecklistBinding>() {
    // Inyecta el ViewModel
    private val truckCheckListViewModel: TruckCheckListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurando la Toolbar
        setUpToolbar()

        // Observa el estado de las verificaciones
        truckCheckListViewModel.verifications.observe(viewLifecycleOwner) { verifications ->
            // Aquí pasamos requireContext() como argumento al adaptador
            val adapter = TruckCheckListAdapter(verifications, truckCheckListViewModel, requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Configurar el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Navegar a otro fragmento cuando se presiona el botón "Continuar"
        binding.btnContinue.setOnClickListener {
            if (!truckCheckListViewModel.areAllQuestionsAnswered()) {
                showAllQuestionsMustBeAnsweredDialog()
            } else if (truckCheckListViewModel.isDrawingView1Drawn.value != true) {
                showErrorDialog("ERROR FIRMAS", "FIRMA DEL CHOFER ES NECESARIA")
            } else if (truckCheckListViewModel.verifications.value!!.any { it.isChecked == 2 } && binding.etDriverObservation.text.isNullOrEmpty()) {
                showErrorDialog("AGREGUE OBSERVACIÓN", "AL NO CUMPLIRSE TODAS LAS VERIFICACIONES DEBE AGREGAR UNA OBSERVACIÓN")
            } else if (truckCheckListViewModel.verifications.value!!.any { it.isChecked == 2 }) {
                showConfirmationDialog()
            } else {
                findNavController().navigate(R.id.action_truckCheckListFragment_to_consignmentFragment)
            }
        }

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.drawingView1.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    // Deshabilitar el desplazamiento cuando se toca la DrawingView
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Habilitar el desplazamiento cuando se deja de tocar la DrawingView
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            // Devolver false para que el evento se maneje en la propia DrawingView
            false
        }

        binding.drawingView2.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    // Deshabilitar el desplazamiento cuando se toca la DrawingView
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Habilitar el desplazamiento cuando se deja de tocar la DrawingView
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            // Devolver false para que el evento se maneje en la propia DrawingView
            false
        }

        binding.etDriverObservation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Aquí puedes manejar acciones antes de que el texto cambie si lo necesitas.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aquí puedes manejar acciones mientras el texto está cambiando si lo necesitas.
            }

            override fun afterTextChanged(s: Editable?) {
                // Aquí puedes manejar acciones después de que el texto haya cambiado.
                truckCheckListViewModel.isEditTextFilled.value = !s.isNullOrEmpty()
                truckCheckListViewModel.checkAllConditions()
            }
        })

        binding.btnClean1.setOnClickListener {
            binding.drawingView1.clearCanvas()

            // Actualizado a "false" cuando se borra el canvas
            truckCheckListViewModel.isDrawingView1Drawn.value = false
            truckCheckListViewModel.checkAllConditions()
        }

        binding.drawingView1.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {

                // Actualizado a true cuando se dibuja en el canvas
                truckCheckListViewModel.isDrawingView1Drawn.value = isDrawn
                truckCheckListViewModel.checkAllConditions()
            }
        })

        binding.btnClean2.setOnClickListener {
            binding.drawingView2.clearCanvas()

            // Actualizado a "false" cuando se borra el canvas
            truckCheckListViewModel.isDrawingView2Drawn.value = false
            truckCheckListViewModel.checkAllConditions()
        }

        binding.drawingView2.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {

                // Actualizado a true cuando se dibuja en el canvas
                truckCheckListViewModel.isDrawingView2Drawn.value = isDrawn
                truckCheckListViewModel.checkAllConditions()
            }
        })
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("¿ESTÁ SEGURO EN CONTINUAR?")
            .setMessage("EXISTEN PREGUNTAS MARCADAS CON NO CUMPLE")
            .setPositiveButton("SI") { _, _ ->
                handleCriticalVerifications()
            }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun showAllQuestionsMustBeAnsweredDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("PREGUNTAS")
            .setMessage("DEBE CONTESTAR TODAS LAS PREGUNTAS")
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleCriticalVerifications() {
        val criticalVerifications = truckCheckListViewModel.verifications.value!!.filter { it.isChecked != 1 }
        val hasCritical = criticalVerifications.any { it.criticality == "Crítico" }
        val hasQuickSolution = criticalVerifications.any { it.criticality == "Solución rápida" }

        when {
            hasCritical -> showErrorDialog("NO SE PUEDE CONTINUAR", "SE DEBE ACERCAR AL JEFE DE LA OFICINA DE DISTRIBUCIÓN O ENCARGADO DE FLOTA")
            hasQuickSolution -> showErrorDialog("NO SE PUEDE CONTINUAR", "HAY PREGUNTAS QUE SON DE SOLUCIÓN RÁPIDA.")
            else -> findNavController().navigate(R.id.action_truckCheckListFragment_to_consignmentFragment)
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                dialog.dismiss() // Esto cierra el diálogo
            }
            .show()
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Check List Camión"

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
        val adapter = binding.recyclerView.adapter as? TruckCheckListAdapter
        adapter?.activateAllSwitches()
    }

    // Infla el layout para este fragmento
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTruckChecklistBinding.inflate(inflater, container, false)
}
