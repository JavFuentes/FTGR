package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentSaleBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.SaleViewModel
import cl.abastible.ftgr.app.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleFragment : BaseFragment<FragmentSaleBinding>() {

    // Inyección del ViewModel
    private val viewModel: SaleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración inicial de la Toolbar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Venta"
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Configuración del menú y navegación
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Personalización del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(
            navigationIconDrawable,
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        //todo: El precio unitario se debe recibir desde API
        val unitValue = binding.tvUnitValue.text.toString().replace("$", "").trim().toFloatOrNull()
        if (unitValue != null) {
            binding.tvUnitValue.text = FormatNumber.formatPrice(unitValue)
        }

        // Deshabilitación inicial del botón "Pago"
        binding.btnPayment.isEnabled = false
        binding.btnPayment.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.disabled_button
            )
        )

//        binding.btnPayment.setOnClickListener {
//            val dischargedLiters = binding.etDischargedLiters.text.toString()
//            val paymentCondition = viewModel.selectedPaymentCondition.value ?: return@setOnClickListener
//            val paymentMethod = viewModel.selectedPaymentMethod.value ?: return@setOnClickListener
//
//            val action = SaleFragmentDirections
//                .actionSaleFragmentToSalesConfirmationFragment(
//                    dischargedLiters,
//                    paymentCondition,
//                    paymentMethod
//                )
//            findNavController().navigate(action)
//        }

        // Observando cambios en el valor total y actualizando la UI
        viewModel.totalValue.observe(viewLifecycleOwner) { totalValue ->
            binding.tvTotalValue.text = FormatNumber.formatPrice(totalValue)
        }

        // Configuración del TextWatcher para et_discharged_liters
        binding.etDischargedLiters.addTextChangedListener {
            val unitValueText = binding.tvUnitValue.text.toString()
            val unitValue = unitValueText.replace("$", "").trim().toFloatOrNull()
                ?: return@addTextChangedListener
            val dischargedLiters = it.toString().toFloatOrNull() ?: return@addTextChangedListener
            viewModel.calculateTotalValue(unitValue, dischargedLiters)
        }

        // Configuración de grupos de botones de condición de pago y método de pago
        val paymentConditionButtons = listOf(
            binding.btnPaymentCondition1,
            binding.btnPaymentCondition2,
            binding.btnPaymentCondition3
        )
        handleButtonGroupSelection(paymentConditionButtons, viewModel::selectPaymentCondition)

        val paymentMethodButtons =
            listOf(binding.btnPaymentMethod1, binding.btnPaymentMethod2, binding.btnPaymentMethod3)
        handleButtonGroupSelection(paymentMethodButtons, viewModel::selectPaymentMethod)

        // Observando cambios en la condición y método de pago para habilitar el botón
        viewModel.selectedPaymentCondition.observe(viewLifecycleOwner) {
            checkAndEnablePaymentButton()
        }
        viewModel.selectedPaymentMethod.observe(viewLifecycleOwner) {
            checkAndEnablePaymentButton()
        }
        binding.etDischargedLiters.addTextChangedListener {
            checkAndEnablePaymentButton()
        }
    }

    // Verificando condiciones y habilitando el botón de pago si se cumplen
    private fun checkAndEnablePaymentButton() {
        val dischargedLitersText = binding.etDischargedLiters.text.toString()
        val dischargedLiters = dischargedLitersText.toFloatOrNull() ?: return
        val isPaymentConditionSelected = viewModel.selectedPaymentCondition.value != null
        val isPaymentMethodSelected = viewModel.selectedPaymentMethod.value != null

        if (dischargedLiters > 1 && isPaymentConditionSelected && isPaymentMethodSelected) {
            binding.btnPayment.isEnabled = true
            binding.btnPayment.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.enabled_button
                )
            )
        } else {
            binding.btnPayment.isEnabled = false
            binding.btnPayment.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.disabled_button
                )
            )
        }
    }

    // Configuración de la creación del menú
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    // Gestión de las opciones del menú seleccionadas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            R.id.menu -> true
            else -> false
        }
    }

    // Manejo de la selección en los grupos de botones
    private fun handleButtonGroupSelection(buttons: List<Button>, onSelect: (String) -> Unit) {
        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(it as Button, buttons)
                onSelect(button.text.toString())
            }
        }
    }

    // Cambio de aspecto en botón seleccionado y no seleccionados
    private fun selectButton(selectedButton: Button, otherButtons: List<Button>) {
        selectedButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.selected_color_button
            )
        )
        selectedButton.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.selected_color_text
            )
        )
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_selected)
        selectedButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        otherButtons.filter { it != selectedButton }.forEach { button ->
            button.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.default_color_button
                )
            )
            button.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.default_color_text
                )
            )
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    // Inicialización del Binding del Fragment
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSaleBinding.inflate(inflater, container, false)
}
