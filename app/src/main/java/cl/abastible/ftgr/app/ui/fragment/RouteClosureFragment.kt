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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentRouteClosureBinding
import cl.abastible.ftgr.app.ui.adapter.RouteClosureAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.customviews.DrawingView
import cl.abastible.ftgr.app.ui.viewmodel.RouteClosureViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteClosureFragment : BaseFragment<FragmentRouteClosureBinding>() {
    // Inyecta el ViewModel
    private val routeClosureViewModel: RouteClosureViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        // Inhabilitando inicialmente el botón "Continuar"
        binding.btnContinue.isEnabled = false
        binding.btnContinue.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.disabled_button
            )
        )

        // Observa los cambios en la lista de verificaciones
        routeClosureViewModel.verifications.observe(viewLifecycleOwner) { verifications ->
            val adapter = RouteClosureAdapter(verifications, routeClosureViewModel, requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Observa el estado del botón "Continuar"
        routeClosureViewModel.isContinueButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnContinue.isEnabled = isEnabled == true
            binding.btnContinue.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isEnabled == true) R.color.enabled_button else R.color.disabled_button
                )
            )
        }

        // Configurar el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Navegar a otro fragmento cuando se presiona el botón "Continuar"
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_routeClosureFragment_to_routeClosure2Fragment)
        }

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.drawingView1.setOnTouchListener { v, event ->
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
        binding.drawingView2.setOnTouchListener { v, event ->
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
                routeClosureViewModel.isEditTextFilled.value = !s.isNullOrEmpty()
                routeClosureViewModel.checkAllConditions()
            }
        })

        binding.btnClean1.setOnClickListener {
            binding.drawingView1.clearCanvas()
            // Actualizado a falso cuando se borra el canvas
            routeClosureViewModel.isDrawingView1Drawn.value = false
            routeClosureViewModel.checkAllConditions()
        }

        binding.drawingView1.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {
                // Actualizado a true cuando se dibuja en el canvas
                routeClosureViewModel.isDrawingView1Drawn.value = isDrawn
                routeClosureViewModel.checkAllConditions()
            }
        })

        binding.btnClean2.setOnClickListener {
            binding.drawingView2.clearCanvas()
            // Actualizado a falso cuando se borra el canvas
            routeClosureViewModel.isDrawingView2Drawn.value = false
            routeClosureViewModel.checkAllConditions()
        }

        binding.drawingView2.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {
                // Actualizado a true cuando se dibuja en el canvas
                routeClosureViewModel.isDrawingView2Drawn.value = isDrawn
                routeClosureViewModel.checkAllConditions()
            }
        })
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Cierre de Ruta"

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
        val adapter = binding.recyclerView.adapter as? RouteClosureAdapter
        adapter?.activateAllSwitches()
    }

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRouteClosureBinding.inflate(inflater, container, false)
}