package cl.abastible.ftgr.app.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cl.abastible.ftgr.databinding.FragmentCapturedImagesBinding
import cl.abastible.ftgr.app.domain.model.ImageItem
import cl.abastible.ftgr.app.ui.adapter.CapturedImagesAdapter
import cl.abastible.ftgr.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CapturedImagesFragment : BaseFragment<FragmentCapturedImagesBinding>() {
    // Lista mutable para mantener las imágenes capturadas
    private val imagesList = mutableListOf<ImageItem>()

    // Adapter para el RecyclerView
    private lateinit var imagesAdapter: CapturedImagesAdapter

    // Definir constantes para los códigos de solicitud de permisos
    private val REQUEST_CAMERA_PERMISSION = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estableciendo el LayoutManager para el RecyclerView
        binding.imagesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializando el adapter
        imagesAdapter = CapturedImagesAdapter(
            imagesList,
            onDeleteClicked = { position ->
                imagesList.removeAt(position)
                this@CapturedImagesFragment.imagesAdapter.notifyItemRemoved(position)
            },
            onImageClick = { imageItem ->
                // Handle image click here
            }
        )


        // Estableciendo el adapter para el RecyclerView
        binding.imagesRecyclerView.adapter = imagesAdapter

        // Manejar clic en el botón de captura de imagen
        binding.btnCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                openCamera()
            }
        }
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para abrir la cámara
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Manejar la respuesta de la cámara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val imageName = "image${imagesList.size + 1}.jpg"
                imagesList.add(ImageItem(it, imageName))
                imagesAdapter.notifyItemInserted(imagesList.size - 1)
            }
        }
    }

    // Manejar clic en el botón de navegación hacia atrás
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

    // Inicializar el binding
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCapturedImagesBinding.inflate(inflater, container, false)
}