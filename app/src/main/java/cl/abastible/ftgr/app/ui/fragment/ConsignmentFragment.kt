package cl.abastible.ftgr.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentConsignmentBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.ConsignmentViewModel
import cl.abastible.ftgr.app.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsignmentFragment : BaseFragment<FragmentConsignmentBinding>() {
    // Inyecta el ViewModel para el Fragment
    private val viewModel: ConsignmentViewModel by viewModels()

    // Método que se invoca cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurando la Toolbar
        setupToolbar()

        // Inicialmente, el botón "Continuar" está deshabilitado
        binding.btnContinue.isEnabled = false

        // Configurar el color de fondo del botón cuando está deshabilitado
        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.disabled_button))

        // Establecer la SeekBar en el 80% al iniciar el fragmento
        binding.truckRefuelingBar.progress = 80
        binding.tvPercentageTruckRefuelingBar.text = "80%"

        // Configuración de los observadores y listeners
        setupTextWatchers()
        setupButtonListeners()
        setupContinueButton()

        // Observa los cambios en la variable isContinueButtonEnabled del ViewModel y habilita/deshabilita el botón "Continuar" en consecuencia
        viewModel.isContinueButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            if (isEnabled) enableContinueButton() else disableContinueButton()
        }

        // Observa los cambios en la variable truckMaxCapacity del ViewModel y actualiza el porcentaje del SeekBar en consecuencia
        viewModel.isEditText1Filled.observe(viewLifecycleOwner) {
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }

        // Observa los cambios en la variable truckMaxCapacity del ViewModel y actualiza el porcentaje del SeekBar en consecuencia
        viewModel.isEditText2Filled.observe(viewLifecycleOwner) {
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }

        // Observar el LiveData de truckMaxCapacity
        viewModel.truckMaxCapacity.observe(viewLifecycleOwner) { maxCapacity ->
            // Actualiza el texto de tv_gas_consignment_liters con el valor de truckMaxCapacity
            val formattedMaxCapacity = FormatNumber.formatLiters(maxCapacity)
            binding.tvGasConsignmentLiters.text = "$formattedMaxCapacity"
        }

        viewModel.isEditingEnabled.observe(viewLifecycleOwner) { isEditing ->
            if (isEditing) {
                enableSeekBar()
            } else {
                disableSeekBar()
            }
        }

        binding.btnEditTruckRefueling.setOnClickListener {
            viewModel.toggleEditingMode()
        }
    }

    private fun enableSeekBar() {
        DrawableCompat.setTint(binding.truckRefuelingBar.progressDrawable, ContextCompat.getColor(requireContext(), R.color.green1))
        DrawableCompat.setTint(binding.truckRefuelingBar.thumb!!, ContextCompat.getColor(requireContext(), R.color.green1))

        val initialLiters = 10400f // Litros iniciales que representan el 80%
        val initialPercentage = 80   // Porcentaje inicial

        binding.truckRefuelingBar.apply {
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.tvPercentageTruckRefuelingBar.text = "$progress%"
                    val liters = viewModel.calculateLitersFromPercentage(progress, initialLiters, initialPercentage)
                    binding.tvGasConsignmentLiters.text = FormatNumber.formatLiters(liters)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        // Permitir que el thumb sea movible
        binding.truckRefuelingBar.setOnTouchListener { _, _ -> false }
    }

    private fun disableSeekBar() {
        // Restablece el color original cuando no está en modo edición
        DrawableCompat.setTint(binding.truckRefuelingBar.progressDrawable, ContextCompat.getColor(requireContext(), R.color.blue1))
        DrawableCompat.setTint(binding.truckRefuelingBar.thumb!!, ContextCompat.getColor(requireContext(), R.color.blue1))

        // Deshabilitar la interacción del usuario con la SeekBar
        binding.truckRefuelingBar.isClickable = false
        binding.truckRefuelingBar.isFocusable = false

        // Remover OnSeekBarChangeListener
        binding.truckRefuelingBar.setOnSeekBarChangeListener(null)

        // Impedir que el thumb sea movible
        binding.truckRefuelingBar.setOnTouchListener { _, _ -> true }
    }

    private fun setupToolbar(){
        // Configuración de la barra de herramientas o Toolbar
        val toolbar = binding.toolbar

        // Establecer la Toolbar como la ActionBar de la actividad
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Establecer el color del título de la Toolbar
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Establecer el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Propuesta Consignación"

        // Mostrar el botón de navegación hacia atrás en la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(navigationIconDrawable, ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)
    }

    private fun setupTextWatchers() {
        binding.etLiterCounter.addTextChangedListener {
            // Actualiza el estado del EditText en el ViewModel
            viewModel.updateEditText1Status(it)

            // Actualiza el porcentaje del SeekBar y el estado del botón Continuar
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }

        // TextWatcher para el segundo EditText (etMileage)
        binding.etMileage.addTextChangedListener {
            // Actualiza el estado del segundo EditText en el ViewModel
            viewModel.updateEditText2Status(it)
        }
    }

    // Método para configurar los listeners para los botones
    private fun setupButtonListeners() {
        // Configurar el listener para el botón btnVentingValve
        binding.btnVentingValve.setOnClickListener {
            // Alternar el estado seleccionado del botón
            it.isSelected = !it.isSelected

            // Lógica para habilitar/deshabilitar el botón Continuar
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }

        // Repetir la misma lógica para los otros botones
        binding.btnLiterCounterCover.setOnClickListener {
            it.isSelected = !it.isSelected
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }

        binding.btnMeasuringMechanism.setOnClickListener {
            it.isSelected = !it.isSelected
            viewModel.checkSwitchesAndEnableButton(
                binding.btnVentingValve.isSelected,
                binding.btnLiterCounterCover.isSelected,
                binding.btnMeasuringMechanism.isSelected
            )
        }
    }

    // Método para actualizar el color del SeekBar en base al porcentaje
    private fun adjustSeekBarColorByPercentage(percentage: Int) {
        val newColor = if (percentage > 80) {
            ContextCompat.getColor(requireContext(), R.color.exceeded_seekbar)
        } else {
            ContextCompat.getColor(requireContext(), R.color.normal_seekbar)
        }
        DrawableCompat.setTint(
            binding.truckRefuelingBar.progressDrawable,
            newColor
        )
        binding.truckRefuelingBar.thumb?.let {
            DrawableCompat.setTint(it, newColor)
        }
    }

    // Configuración del botón "Continuar", navega a otro fragmento cuando se presiona
    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_consignmentFragment_to_printingConsignmentFragment)
        }
    }

    // Habilita el botón "Continuar" y cambia su color de fondo
    private fun enableContinueButton() {
        binding.btnContinue.isEnabled = true
        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.enabled_button))
    }

    // Deshabilita el botón "Continuar" y cambia su color de fondo
    private fun disableContinueButton() {
        binding.btnContinue.isEnabled = false
        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.disabled_button))
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    // Manejar la selección de los items del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navegar hacia atrás cuando se presiona el botón de navegación de la ActionBar
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // Inicialización del binding del fragmento
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentConsignmentBinding.inflate(inflater, container, false)
}
