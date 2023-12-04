package cl.abastible.ftgr.app.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
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
import cl.abastible.ftgr.databinding.FragmentSupplyBinding
import cl.abastible.ftgr.app.domain.model.ClosingState
import cl.abastible.ftgr.app.domain.model.ImageItem
import cl.abastible.ftgr.app.ui.adapter.CapturedImagesAdapter
import cl.abastible.ftgr.app.ui.adapter.SupplyAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.viewmodel.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplyFragment : BaseFragment<FragmentSupplyBinding>() {
    // Inyecta el ViewModel
    private val supplyViewModel: SupplyViewModel by viewModels()

    // Lista para almacenar los estados de cierre filtrados
    private var filteredClosingStates: List<ClosingState> = listOf()

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
        //Configuración de la toolbar
        setUpToolbar()

        // Observa los cambios en la lista de verificaciones
        supplyViewModel.verifications.observe(viewLifecycleOwner) { verifications ->
            val adapter = SupplyAdapter(verifications, supplyViewModel, requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Configurar el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Observando cambios en los estados de cierre filtrados
        supplyViewModel.getFilteredClosingStates().observe(viewLifecycleOwner) { states ->
            filteredClosingStates = states
        }

        binding.btnReturn.setOnClickListener {
            showOrderReturnDialog()
        }

        binding.btnContinue.setOnClickListener {
            val allQuestionsAffirmative = supplyViewModel.verifications.value!!.all { it.isChecked == 1 }

            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("AllQuestionsAffirmative", allQuestionsAffirmative)
                apply()
            }

            if (allQuestionsAffirmative) {
                // Navegar al siguiente fragmento si todas las verificaciones son afirmativas
                findNavController().navigate(R.id.action_supplyFragment_to_leaksProtocolFragment)
            } else {
                // Mostrar un dialog si alguna verificación es negativa y/o navegar al fragmento correspondiente
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
                this@SupplyFragment.imagesAdapter.notifyItemRemoved(position)
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
            findNavController().navigate(R.id.action_supplyFragment_to_agendaFragment)
            alertDialog.dismiss()
        }

        alertDialog.show()
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
                // Acciones al presionar "SI", navegar al siguiente fragmento
                findNavController().navigate(R.id.action_supplyFragment_to_leaksProtocolFragment)
            }
            .setNegativeButton("NO", null) // Cierra el diálogo sin hacer nada
            .show()
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Pasos seguros de abastecimiento (1)"

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        // Habilitando el botón de navegación hacia atrás
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(navigationIconDrawable, ContextCompat.getColor(requireContext(), R.color.white))
    }

    // Función para abrir la cámara
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            startForResult.launch(takePictureIntent)
        }
    }

    // Infla el menú en la ActionBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    ///// Activa todos los switches. (ONLY TESTING) /////
    private fun activateAllSwitches() {
        val adapter = binding.recyclerView.adapter as? SupplyAdapter
        adapter?.activateAllSwitches()
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
    ) = FragmentSupplyBinding.inflate(inflater, container, false)
}
