package cl.abastible.ftgr.app.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.fragment.findNavController
import cl.abastible.ftgr.R
import cl.abastible.ftgr.databinding.FragmentPrintingConsignmentBinding
import cl.abastible.ftgr.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class PrintingConsignmentFragment : BaseFragment<FragmentPrintingConsignmentBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurando la Toolbar
        setUpToolbar()

        // Agrega un listener a un botón u otra vista para crear el PDF
        binding.btnSave.setOnClickListener {
            createPdf(binding.dispatchGuideLayout)
            findNavController().navigate(R.id.action_printingConsignmentFragment_to_createdConsignmentFragment)
        }
    }

    private fun createPdf(viewContent: LinearLayout) {
        // Convierte 3 pulgadas a píxeles basado en la densidad de pantalla
        val inches = 3f
        val density = resources.displayMetrics.densityDpi
        val widthPixels = (inches * density).toInt()

        // Mide y haz layout de la vista
        viewContent.measure(
            View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val heightPixels = viewContent.measuredHeight
        viewContent.layout(0, 0, widthPixels, heightPixels)

        if (heightPixels > 0) {
            val bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            viewContent.draw(canvas)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(widthPixels, heightPixels, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val pdfCanvas = page.canvas
            pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            // Guarda el PdfDocument en un archivo
            val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "GuiaDeDespacho.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(filePath))
                Toast.makeText(context, "PDF guardado en ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al guardar PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                pdfDocument.close()
            }
        } else {
            Toast.makeText(context, "Error: La vista no tiene contenido.", Toast.LENGTH_LONG).show()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        // Asegúrate de llamar a esto después de que la vista haya tenido tiempo de hacer layout y renderizado
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun setUpToolbar(){
        // Estableciendo la Toolbar como ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Estableciendo el título de la Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Previsualización"

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

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPrintingConsignmentBinding.inflate(inflater, container, false)
}