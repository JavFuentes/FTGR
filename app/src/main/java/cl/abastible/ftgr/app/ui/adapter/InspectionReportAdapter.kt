package cl.abastible.ftgr.app.ui.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.InspectionReportVerification
import cl.abastible.ftgr.app.ui.viewmodel.InspectionReportViewModel

class InspectionReportAdapter(
    private val verificationList: List<InspectionReportVerification>,
    private val viewModel: InspectionReportViewModel,
    private val context: Context
) : RecyclerView.Adapter<InspectionReportAdapter.ViewHolder>() {

    /*
    * Este método se llama cuando se crea un nuevo ViewHolder. Infla la vista de la tarjeta de la lista de verificaciones
    * del informe de inspección y crea un ViewHolder para ella
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inspection_report_cardview, parent, false)
        return ViewHolder(view)
    }

    // Este método vincula los datos del elemento de verificación actual con un ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verification = verificationList[position]
        holder.tvVerification.text = verification.question

        holder.btnOn.setImageResource(if (verification.isChecked == 1) R.drawable.ic_on else R.drawable.ic_empty_on)
        holder.btnOff.setImageResource(if (verification.isChecked == 2) R.drawable.ic_off else R.drawable.ic_empty_off)


        holder.btnOn.setOnClickListener {
            if (verification.isChecked == 0 || verification.isChecked == 2) {
                verification.isChecked = 1
                viewModel.updateVerification(verification.idVerification, 1)
                notifyItemChanged(position)
            }
        }

        holder.btnOff.setOnClickListener {
            if (verification.isChecked == 0 || verification.isChecked == 1) {
                verification.isChecked = 2
                viewModel.updateVerification(verification.idVerification, 2)
                notifyItemChanged(position)
            }
        }

        // Manejar el clic en el botón de imagen
        holder.btnImage.setOnClickListener {
            showImageDialog()
        }

        // Maneja el clic en el itemView para actualizar la posición seleccionada
        holder.itemView.setOnClickListener {

        }
    }

    private fun showImageDialog() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.image_dialog)
        val imageView: ImageView = dialog.findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.abastible_test_img)

        // Configurar OnClickListener para la ImageView
        imageView.setOnClickListener {
            // Cierra el dialog cuando la imagen es tocada
            dialog.dismiss()
        }
        dialog.show()
    }

    // Este método retorna el número total de elementos en la lista de verificación
    override fun getItemCount(): Int {
        return verificationList.size
    }

    // Define una clase interna ViewHolder que almacena las vistas que se necesitan para mostrar un solo elemento en la lista de verificaciones
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnOn: ImageButton = itemView.findViewById(R.id.btn_on)
        val btnOff: ImageButton = itemView.findViewById(R.id.btn_off)
        val btnImage: ImageButton = itemView.findViewById(R.id.btn_image)
        val tvVerification: TextView = itemView.findViewById(R.id.tv_verification)
        val spinnerSymptoms: Spinner = itemView.findViewById(R.id.spinner_symptoms)
        val tvSymptomsInfo: TextView = itemView.findViewById(R.id.tv_symptom_info)
    }

    ///// Activa todos los switches. (ONLY TESTING) /////
    fun activateAllSwitches() {
        verificationList.forEach { verification ->
            if (verification.isChecked == 0 || verification.isChecked == 2) {
                verification.isChecked = 1
                viewModel.updateVerification(verification.idVerification, 1)
            }
        }
        notifyDataSetChanged()
        viewModel.checkAllConditions()
    }
}
