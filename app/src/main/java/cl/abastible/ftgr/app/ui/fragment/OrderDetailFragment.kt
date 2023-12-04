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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentOrderDetailBinding
import cl.abastible.ftgr.app.domain.model.ClosingState
import cl.abastible.ftgr.app.domain.model.ImageItem
import cl.abastible.ftgr.app.ui.adapter.CapturedImagesAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>() {
    // Inyecta el ViewModel
    private val orderDetailViewModel: OrderDetailViewModel by viewModels()

    // Lista para almacenar los estados de cierre filtrados
    private var filteredClosingStates: List<ClosingState> = listOf()

    // Resultado del lanzador para solicitud de permiso de llamada
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permiso otorgado. Puedes realizar llamadas.",
                    Toast.LENGTH_SHORT
                ).show()
                makePhoneCall(orderDetailViewModel.clientPhoneNumber.value!!)
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
        // Configuración de la Toolbar
        setUpToolbar()

        // Navegar al SupplyFragment cuando se presiona el botón "Start"
        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_supplyFragment)
        }

        // Observando cambios en las coordenadas de ubicación del cliente
        orderDetailViewModel.locationCoordinates.observe(viewLifecycleOwner) { coordinates ->
            binding.btnLocation.setOnClickListener {
                openWaze(coordinates.first, coordinates.second)
            }
        }

        // Observando cambios en los estados de cierre filtrados
        orderDetailViewModel.getFilteredClosingStates().observe(viewLifecycleOwner) { states ->
            filteredClosingStates = states
        }

        // Observando cambios en el botón de devolución de pedido
        binding.btnReturn.setOnClickListener {
            showOrderReturnDialog()
        }

        // Observando cambios en el botón de editar referencias del conductor
        binding.btnEdit.setOnClickListener {
            showReferenceInputDialog()
        }

        // Observando cambios en el número de teléfono del cliente
        orderDetailViewModel.clientPhoneNumber.observe(viewLifecycleOwner) { phoneNumber ->
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
            // Opcional: Puedes abrir Google Maps o alguna otra aplicación aquí
        }
    }

    private fun setUpToolbar() {
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando el botón de navegación hacia atrás
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(
            navigationIconDrawable,
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)
    }

    // Realiza una llamada telefónica
    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
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

    // Muestra un diálogo para ingresar una referencia
    private fun showReferenceInputDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reference_input, null)
        val editTextReference = dialogView.findViewById<EditText>(R.id.editTextReference)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("GUARDAR") { _, _ ->
                // Acción al presionar el botón Guardar
                val referenceText = editTextReference.text.toString()

                // Establecer el texto en el TextView
                binding.tvDriverReferences.text = referenceText
            }
            .setNegativeButton("CANCELAR", null)
            .create()
            .show()
    }

    // Muestra un diálogo para realizar una devolución de pedido
    private fun showOrderReturnDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_return, null)
        val spinnerCancellationType = dialogView.findViewById<Spinner>(R.id.spinnerCancellationType)

        // Referencia al layout incluido
        val includedLayout = dialogView.findViewById<View>(R.id.include)

        // Ahora, dentro del layout incluido, buscas el botón btn_capture_image
        val btnCaptureImage = includedLayout.findViewById<Button>(R.id.btn_capture_image)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filteredClosingStates.map { it.description })
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
                this@OrderDetailFragment.imagesAdapter.notifyItemRemoved(position)
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
            findNavController().navigate(R.id.action_orderDetailFragment_to_agendaFragment)
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

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrderDetailBinding.inflate(inflater, container, false)
}
