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
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import cl.abastible.ftgr.databinding.FragmentInspectionReportBinding
import cl.abastible.ftgr.app.domain.model.ImageItem
import cl.abastible.ftgr.app.ui.adapter.CapturedImagesAdapter
import cl.abastible.ftgr.app.ui.adapter.InspectionReportAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import cl.abastible.ftgr.app.ui.customviews.DrawingView
import cl.abastible.ftgr.app.ui.viewmodel.InspectionReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InspectionReportFragment : BaseFragment<FragmentInspectionReportBinding>() {
    // Inyecta el ViewModel
    private val inspectionReportViewModel: InspectionReportViewModel by viewModels()

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
        // Configuración de la toolbar
        setUpToolbar()

        // Observa los cambios en la lista de verificaciones
        inspectionReportViewModel.verifications.observe(viewLifecycleOwner) { verifications ->
            val adapter = InspectionReportAdapter(verifications, inspectionReportViewModel, requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Estableciendo el LayoutManager para el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Navegar a otro fragmento cuando se presiona el botón "Continuar"
        binding.btnContinue.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
            val allQuestionsAffirmative = sharedPreferences.getBoolean("AllQuestionsAffirmative", true)

            if (!areAllQuestionsAnswered()) {
                showDialog("PREGUNTAS", "DEBE CONTESTAR TODAS LAS PREGUNTAS", "ACEPTAR")
            } else if (!isDriverSignatureProvided()) {
                showDialog("ERROR FIRMAS", "FIRMA DEL CHOFER ES NECESARIA", "ACEPTAR")
            } else if (isAnyQuestionMarkedYes() && imagesList.isEmpty()) {
                showDialog("ALERTA", "EXISTEN PREGUNTAS CON NO CUMPLE, SE REQUIERE TOMAR UNA FOTOGRAFÍA", "ACEPTAR")
            } else if (isAnyQuestionMarkedYes() && !isObservationAdded()) {
                showDialog("AGREGUE OBSERVACIÓN", "AL NO CUMPLIRSE TODAS LAS VERIFICACIONES DEBE AGREGAR UNA OBSERVACIÓN", "ACEPTAR")
            } else if (isAnyQuestionMarkedYes()) {
                showConfirmationDialog()
            } else {
                if (!allQuestionsAffirmative) {
                    // Si no todas las preguntas fueron afirmativas, navega al fragmento de Devoluciones
                    findNavController().navigate(R.id.action_inspectionReportFragment_to_returnFragment)
                    // Restablece el estado en SharedPreferences
                    with(sharedPreferences.edit()) {
                        putBoolean("AllQuestionsAffirmative", true)
                        apply()
                    }
                } else {
                    // Continúa con el flujo normal
                    navigateBasedOnRiskType()
                }
            }
        }

        inspectionReportViewModel.selectedItem.observe(viewLifecycleOwner) {
            // Puedes manejar la visualización de la selección aquí si lo necesitas, por ejemplo, cambiando el fondo del item seleccionado
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

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.btnClean1.setOnClickListener {
            binding.drawingView1.clearCanvas()
            inspectionReportViewModel.isDrawingView1Drawn.value = false
            inspectionReportViewModel.checkAllConditions()
        }

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.drawingView1.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {
                inspectionReportViewModel.isDrawingView1Drawn.value = isDrawn
                inspectionReportViewModel.checkAllConditions()
            }
        })

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.btnClean2.setOnClickListener {
            binding.drawingView2.clearCanvas()
            inspectionReportViewModel.isDrawingView2Drawn.value = false
            inspectionReportViewModel.checkAllConditions()
        }

        // Configuración de la DrawingView para deshabilitar el desplazamiento cuando se toca
        binding.drawingView2.setOnCanvasDrawnListener(object : DrawingView.OnCanvasDrawnListener {
            override fun onCanvasDrawn(isDrawn: Boolean) {
                // Actualizado a true cuando se dibuja en el canvas
                inspectionReportViewModel.isDrawingView2Drawn.value = isDrawn
                inspectionReportViewModel.checkAllConditions()
            }
        })

        // Observa los cambios en el estado de habilitación del botón "Continuar"
        binding.etDriverObservation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Aquí puedes manejar acciones antes de que el texto cambie si lo necesitas
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aquí puedes manejar acciones mientras el texto está cambiando si lo necesitas
            }

            override fun afterTextChanged(s: Editable?) {
                // Aquí puedes manejar acciones después de que el texto haya cambiado
                inspectionReportViewModel.isEditTextFilled.value = !s.isNullOrEmpty()
                inspectionReportViewModel.checkAllConditions()
            }
        })

        // Estableciendo el LayoutManager para el RecyclerView de imágenes
        binding.include.imagesRecyclerView.layoutManager = LinearLayoutManager(context)
        imagesAdapter = CapturedImagesAdapter(imagesList,
            { position ->
                imagesList.removeAt(position)
                this@InspectionReportFragment.imagesAdapter.notifyItemRemoved(position)
            },
            { imageItem ->
                showImageDialog(imageItem.bitmap)
            }
        )

        // Estableciendo el Adapter para el RecyclerView de imágenes
        binding.include.imagesRecyclerView.adapter = imagesAdapter

        // Maneja el clic en el botón de captura de imágenes
        binding.include.btnCaptureImage.setOnClickListener {
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
    }

    private fun navigateBasedOnRiskType() {
        when (inspectionReportViewModel.getMostSevereRiskType()) {
            "Instalación con fuga" -> findNavController().navigate(R.id.action_inspectionReportFragment_to_leaksProtocol2Fragment)
            "Instalación con riesgo inminente" -> findNavController().navigate(R.id.action_inspectionReportFragment_to_imminentDangerFragment)
            "Instalación con riesgo menor", "Instalación sin riesgo" -> findNavController().navigate(R.id.action_inspectionReportFragment_to_supply2Fragment)
            else -> findNavController().navigate(R.id.action_inspectionReportFragment_to_supply2Fragment)
        }
    }

    // Verifica si se ha agregado una observación
    private fun isObservationAdded(): Boolean {
        return !binding.etDriverObservation.text.isNullOrEmpty()
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Informe de Inspección"

        // Cambiando el color del texto del título a blanco
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Habilitando la creación de opciones de menú en este fragmento
        setHasOptionsMenu(true)

        // Habilitando el botón de navegación hacia atrás
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Cambia el color del icono de navegación
        val navigationIconDrawable = DrawableCompat.wrap(toolbar.navigationIcon!!).mutate()
        DrawableCompat.setTint(
            navigationIconDrawable,
            ContextCompat.getColor(requireContext(), R.color.white)
        )
    }

    private fun showDialog(title: String, message: String, positiveButtonText: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showConfirmationDialog() {
        val message = SpannableString("EXISTEN PREGUNTAS MARCADAS CON NO CUMPLE")
        val start = message.indexOf("NO CUMPLE")
        val end = start + "NO CUMPLE".length
        message.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        AlertDialog.Builder(requireContext())
            .setTitle("¿ESTÁ SEGURO EN CONTINUAR?")
            .setMessage(message)
            .setPositiveButton("SI") { _, _ ->
                navigateBasedOnRiskType()
            }
            .setNegativeButton("NO", null) // Cierra el diálogo sin hacer nada
            .show()
    }

    private fun areAllQuestionsAnswered(): Boolean {
        return inspectionReportViewModel.areAllQuestionsAnswered()
    }

    private fun isDriverSignatureProvided(): Boolean {
        return inspectionReportViewModel.isDrawingView1Drawn.value == true
    }

    private fun isAnyQuestionMarkedYes(): Boolean {
        return inspectionReportViewModel.getMostSevereRiskType().isNotEmpty()
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
        val adapter = binding.recyclerView.adapter as? InspectionReportAdapter
        adapter?.activateAllSwitches()
    }

    // Inicializa el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentInspectionReportBinding.inflate(inflater, container, false)
}