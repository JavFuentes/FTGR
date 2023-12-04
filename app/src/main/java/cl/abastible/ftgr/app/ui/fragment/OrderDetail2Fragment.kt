package cl.abastible.ftgr.app.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentOrderDetail2Binding
import cl.abastible.ftgr.app.domain.model.ClosingState
import cl.abastible.ftgr.app.domain.model.ImageItem
import cl.abastible.ftgr.app.ui.adapter.CapturedImagesAdapter
import cl.abastible.ftgr.app.ui.adapter.DischargeListAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.SharedViewModel
import cl.abastible.ftgr.app.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetail2Fragment : BaseFragment<FragmentOrderDetail2Binding>(), DischargeListAdapter.InteractionListener {
    // Inyecta el ViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Lista para almacenar los estados de cierre filtrados
    private var filteredClosingStates: List<ClosingState> = listOf()

    // Adaptador para el RecyclerView
    private lateinit var dischargeAdapter: DischargeListAdapter

    // Resultado del lanzador para solicitud de permiso de llamada
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permiso otorgado. Puedes realizar llamadas.",
                    Toast.LENGTH_SHORT
                ).show()
                makePhoneCall(sharedViewModel.clientPhoneNumber.value!!)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permiso denegado. No se pueden realizar llamadas.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Lista mutable para mantener las imágenes capturadas
    private val imagesList = mutableListOf<ImageItem>()

    // Adapter para el RecyclerView
    private lateinit var imagesAdapter: CapturedImagesAdapter

    // Definir constantes para los códigos de solicitud de permisos
    private val REQUEST_CAMERA_PERMISSION = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    // Inicializa el ActivityResultLauncher para el permiso de la cámara
    private val requestPermissionLauncherCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso denegado. No se pueden capturar imágenes.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Inicializa el ActivityResultLauncher para el resultado de la cámara
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val imageName = "image${imagesList.size + 1}.jpg"
                imagesList.add(ImageItem(it, imageName))
                imagesAdapter.notifyItemInserted(imagesList.size - 1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configuraciones de la Toolbar
        setupToolbar()

        sharedViewModel.dischargedLitersList.observe(viewLifecycleOwner) { list ->
            dischargeAdapter.updateData(list)

            // Actualiza el valor total si es necesario
            val totalLiters = list.sum()
            binding.tvTotalLiters.text = "$totalLiters L"

            binding.tvTotalValue.text = FormatNumber.formatPrice(
                totalLiters * binding.tvUnitValue.text.toString()
                    .replace("$", "").trim().toFloatOrNull()!!
            )

            // Habilitar o deshabilitar el botón de inicio según si la lista tiene descargas
            binding.btnStart.isEnabled = list.isNotEmpty()
            updateStartButtonAppearance()
        }

        // Observando cambios en los estados de cierre filtrados
        sharedViewModel.getFilteredClosingStates().observe(viewLifecycleOwner) { states ->
            filteredClosingStates = states
        }

        // Navegar al Supply3Fragment cuando se presiona el botón "Start"
        binding.btnStart.setOnClickListener {
            val paymentCondition =
                sharedViewModel.selectedPaymentCondition.value ?: return@setOnClickListener
            val paymentMethod =
                sharedViewModel.selectedPaymentMethod.value ?: return@setOnClickListener
            val documentType =
                sharedViewModel.selectedDocumentType.value ?: return@setOnClickListener

            // Guardar datos en el ViewModel
            sharedViewModel.selectPaymentCondition(paymentCondition)
            sharedViewModel.selectPaymentMethod(paymentMethod)
            sharedViewModel.selectDocumentType(documentType)

            // Navegar al Supply3Fragment
            findNavController().navigate(R.id.action_orderDetail2Fragment_to_supply3Fragment)
        }

        // Inicialización del RecyclerView y su adaptador
        dischargeAdapter = DischargeListAdapter(mutableListOf(), this)
        binding.recyclerViewDischarges.adapter = dischargeAdapter

        // Observa cambios en la lista de descargas
        sharedViewModel.dischargedLitersList.observe(viewLifecycleOwner) { list ->
            dischargeAdapter.updateData(list)
            // Actualiza el valor total si es necesario
            val totalLiters = list.sum()
            binding.tvTotalLiters.text = "$totalLiters L"

            binding.tvTotalValue.text = FormatNumber.formatPrice(
                totalLiters * binding.tvUnitValue.text.toString()
                    .replace("$", "").trim().toFloatOrNull()!!
            )
        }

        // Observando cambios en el botón de descarga
        binding.btnDischarge.setOnClickListener {
            showNewDischargeDialog()
        }

        // Observando cambios en las coordenadas de ubicación del cliente
        sharedViewModel.locationCoordinates.observe(viewLifecycleOwner) { coordinates ->
            binding.btnLocation.setOnClickListener {
                openWaze(coordinates.first, coordinates.second)
            }
        }

        sharedViewModel.clientPhoneNumber.observe(viewLifecycleOwner) { phoneNumber ->
            // Actualizar el número de teléfono que se usará para hacer la llamada
            binding.btnCall.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        makePhoneCall(phoneNumber)
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                        // Aquí puedes mostrar un diálogo explicando por qué necesitas el permiso
                    }

                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }
            }
        }

        // Observando cambios en el botón de observación
        binding.btnObservation.setOnClickListener {
            showObservationDialog()
        }

        //todo: El precio unitario se debe recibir desde API
        val unitValue = binding.tvUnitValue.text.toString().replace("$", "").trim().toFloatOrNull()
        if (unitValue != null) {
            binding.tvUnitValue.text = FormatNumber.formatPrice(unitValue)
        }

        // Deshabilitación inicial del botón "Pago"
        binding.btnStart.isEnabled = false
        binding.btnStart.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.disabled_button
            )
        )

        // Observando cambios en el valor total y actualizando la UI
        sharedViewModel.totalValue.observe(viewLifecycleOwner) { totalValue ->
            binding.tvTotalValue.text = FormatNumber.formatPrice(totalValue)
        }

        binding.btnAnnul.setOnClickListener {
            showOrderAnnulDialog()
        }

        // Observa los datos de ejemplo del ViewModel
        sharedViewModel.paymentConditions.observe(viewLifecycleOwner) { conditions ->
            setupSpinners(binding.spinnerPaymentCondition, conditions)
        }

        sharedViewModel.paymentMethods.observe(viewLifecycleOwner) { methods ->
            setupSpinners(binding.spinnerPaymentMethod, methods)
        }

        sharedViewModel.documentTypes.observe(viewLifecycleOwner) { types ->
            setupSpinners(binding.spinnerDocumentType, types)
        }

        binding.spinnerPaymentCondition.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCondition = parent?.getItemAtPosition(position) as String
                    sharedViewModel.selectPaymentCondition(selectedCondition)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerPaymentMethod.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedMethod = parent?.getItemAtPosition(position) as String
                    sharedViewModel.selectPaymentMethod(selectedMethod)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerDocumentType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedType = parent?.getItemAtPosition(position) as String
                    sharedViewModel.selectDocumentType(selectedType)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    // Función para abrir la cámara
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            startForResult.launch(takePictureIntent)
        }
    }

    // Método para abrir Waze
    private fun openWaze(latitude: Double, longitude: Double) {
        val wazeUri = Uri.parse("https://waze.com/ul?ll=$latitude,$longitude&navigate=yes")
        val intent = Intent(Intent.ACTION_VIEW, wazeUri)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Waze no está instalado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEdit(position: Int, currentAmount: Float) {
        // Mostrar un diálogo para editar la cantidad
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_discharge, null)
        val editText = dialogView.findViewById<EditText>(R.id.et_liters_to_discharge)
        editText.setText(currentAmount.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("EDITAR DESCARGA")
            .setView(dialogView)
            .setPositiveButton("ACTUALIZAR") { dialog, _ ->
                val newAmount = editText.text.toString().toFloatOrNull()
                if (newAmount != null) {
                    // Aquí actualizamos el ViewModel y la lista
                    sharedViewModel.updateDischarge(position, newAmount)
                    dischargeAdapter.updateItem(position, newAmount)
                }
                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun onDelete(position: Int) {
        // Confirmar la acción de eliminación primero
        AlertDialog.Builder(requireContext())
            .setTitle("CONFIRMACIÓN")
            .setMessage("¿Estás seguro de que quieres eliminar esta descarga?")
            .setPositiveButton("ELIMINAR") { dialog, _ ->
                sharedViewModel.removeDischarge(position)
                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    // Método para configurar un spinner con un adaptador
    private fun setupSpinners(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Realiza una llamada telefónica
    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

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

    private fun setupToolbar() {
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título y la navegación de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Pedido"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(
            navigationIconDrawable,
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)
    }

    private fun showObservationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // Configuración del layout para el diálogo
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_observation, null)
        dialogBuilder.setView(dialogView)

        val etComment = dialogView.findViewById<EditText>(R.id.tv_comment)
        dialogBuilder.setTitle("OBSERVACIÓN PEDIDO")
        // Configuración de botones
        dialogBuilder.setPositiveButton("GUARDAR") { _, _ ->
            val comment = etComment.text.toString()
            // Aquí puedes guardar o procesar el comentario
        }

        dialogBuilder.setNegativeButton("CANCELAR") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    // Esta función se podría llamar desde showNewDischargeDialog() en tu Fragment
    private fun showNewDischargeDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_discharge, null)
        val editTextNewDischarge = dialogView.findViewById<EditText>(R.id.et_liters_to_discharge)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                val dischargeAmount = editTextNewDischarge.text.toString().toFloatOrNull()
                if (dischargeAmount != null) {
                    sharedViewModel.addDischarge(dischargeAmount)
                }
                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Muestra un diálogo para realizar una anulación de pedido
    private fun showOrderAnnulDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_return, null)
        val spinnerCancellationType = dialogView.findViewById<Spinner>(R.id.spinnerCancellationType)

        // Referencia al layout incluido
        val includedLayout = dialogView.findViewById<View>(R.id.include)

        // Ahora, dentro del layout incluido, buscas el botón btn_capture_image
        val btnCaptureImage = includedLayout.findViewById<Button>(R.id.btn_capture_image)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            filteredClosingStates.map { it.description })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCancellationType.adapter = adapter

        val editTextComment = dialogView.findViewById<EditText>(R.id.editTextComment)
        val btnDialogCancel = dialogView.findViewById<Button>(R.id.btnDialogCancel)
        val btnDialogAccept = dialogView.findViewById<Button>(R.id.btnDialogAccept)

        val imagesRecyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.images_recycler_view)

        // Estableciendo el LayoutManager para el RecyclerView de imágenes
        imagesRecyclerView.layoutManager = LinearLayoutManager(context)
        imagesAdapter = CapturedImagesAdapter(imagesList,
            { position ->
                imagesList.removeAt(position)
                this@OrderDetail2Fragment.imagesAdapter.notifyItemRemoved(position)
            },
            { imageItem ->
                showImageDialog(imageItem.bitmap)
            }
        )

        // Estableciendo el Adapter para el RecyclerView de imágenes
        imagesRecyclerView.adapter = imagesAdapter

        btnCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncherCamera.launch(Manifest.permission.CAMERA)
            } else {
                openCamera()
            }
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("DEVOLUCIÓN PEDIDO")
            .setView(dialogView)
            .create()

        btnDialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnDialogAccept.setOnClickListener {
            // TODO: Procesar la información del diálogo
            findNavController().navigate(R.id.action_orderDetail2Fragment_to_agendaFragment)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    // Muestra un diálogo con la imagen seleccionada
    private fun showImageDialog(bitmap: Bitmap) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.layout_image_preview)
        val imageView: ImageView = dialog.findViewById(R.id.imagePreview)
        imageView.setImageBitmap(bitmap)

        // Cierra el dialog al tocar la imagen
        imageView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    // Actualiza la apariencia del botón de inicio
    private fun updateStartButtonAppearance() {
        binding.btnStart.setBackgroundColor(
            if (binding.btnStart.isEnabled)
                ContextCompat.getColor(requireContext(), R.color.enabled_button) // Coloca el color correspondiente para el botón habilitado
            else
                ContextCompat.getColor(requireContext(), R.color.disabled_button) // Coloca el color correspondiente para el botón deshabilitado
        )
    }

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrderDetail2Binding.inflate(inflater, container, false)
}
