package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentSalesConfirmationBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.customviews.DrawingView
import cl.abastible.ftgr.app.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SalesConfirmationFragment: BaseFragment<FragmentSalesConfirmationBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar la toolbar
        setupToolbar()

        //TODO: El precio unitario se debe recibir desde API
        val unitValue = binding.tvUnitValue.text.toString().replace("$", "").trim().toFloatOrNull() ?: 0f

        if (unitValue != null) {
            binding.tvUnitValue.text = FormatNumber.formatPrice(unitValue)
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

        // Configurar el Listener del DrawingView
        binding.drawingView1.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {
                if (isDrawn) {
                    // Si el canvas está dibujado, habilita el botón y cambia su color a habilitado.
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.enabled_button)
                    )
                } else {
                    // Si el canvas está limpio, deshabilita el botón y cambia su color a deshabilitado.
                    binding.btnContinue.isEnabled = false
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.disabled_button)
                    )
                }
            }
        })

        // Obtener el valor de dischargedLiters y convertirlo a Float
        val dischargedLitersString = arguments?.getString("dischargedLitersArgument") ?: ""
        val dischargedLiters = dischargedLitersString.toFloatOrNull() ?: 0f
        val paymentCondition = arguments?.getString("paymentConditionArgument") ?: ""
        val paymentMethod = arguments?.getString("paymentMethodArgument") ?: ""

        // Formatear y establecer el valor de tv_liters, tv_payment_condition y tv_payment_method
        val formattedDischargedLiters = FormatNumber.formatLiters(dischargedLiters)
        binding.tvLiters.text = formattedDischargedLiters
        binding.tvPaymentCondition.text = paymentCondition
        binding.tvPaymentMethod.text = paymentMethod

        // Calcular y establecer el valor de tv_total_value
        val totalValue = dischargedLiters * unitValue
        binding.tvTotalValue.text = FormatNumber.formatPrice(totalValue)

        // Limpiar el canvas cuando se presiona el botón
        binding.btnClean1.setOnClickListener {
            binding.drawingView1.clearCanvas()
        }

        // Establecer el Listener del botón "Continuar"
        binding.btnContinue.setOnClickListener {
            if (!isCanvasDrawn()) {
                // Mostrar diálogo si la pizarra está vacía
                showErrorDialog()
            } else {
                // Navegar al siguiente fragmento si la pizarra tiene una firma
                findNavController().navigate(R.id.action_salesConfirmationFragment_to_generatedDocumentFragment)
            }
        }
    }
    // Método para verificar si la pizarra está dibujada
    private fun isCanvasDrawn(): Boolean {
        return binding.drawingView1.isCanvasDrawn()
    }

    // Método para mostrar el diálogo de error
    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("ERROR FIRMA")
            .setMessage("FIRMA DEL CHOFER ES NECESARIA")
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun setupToolbar() {
        // Establecer la toolbar definida en el layout como la ActionBar de la actividad
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Configurar el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.title = "Confirmación de Venta"

        // Cambiar el color del texto del título de la ActionBar a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        // Habilitar el botón de navegación hacia atrás en la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambiar el color del ícono de navegación de la ActionBar
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(navigationIconDrawable, ContextCompat.getColor(requireContext(), R.color.white))
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Manejar el clic en el botón de navegación hacia atrás
                findNavController().navigateUp()
                true
            }
            R.id.menu -> {
                // Manejar clic en el ítem "Menu" de la ActionBar.
                true
            }
            else -> false
        }
    }

    // Método para inicializar el binding del Fragment
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSalesConfirmationBinding.inflate(inflater, container, false)
}
