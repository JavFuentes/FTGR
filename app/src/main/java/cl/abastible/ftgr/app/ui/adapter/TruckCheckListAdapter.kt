package cl.abastible.ftgr.app.ui.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.TruckVerification
import cl.abastible.ftgr.app.ui.viewmodel.TruckCheckListViewModel

class TruckCheckListAdapter(
    private val verificationList: List<TruckVerification>,
    private val viewModel: TruckCheckListViewModel,
    private val context: Context
) : RecyclerView.Adapter<TruckCheckListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.truck_checklist_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verification = verificationList[position]
        holder.tvVerification.text = verification.description

        holder.btnOn.setImageResource(if (verification.isChecked == 1) R.drawable.ic_on else R.drawable.ic_empty_on)
        holder.btnOff.setImageResource(if (verification.isChecked == 2) R.drawable.ic_off else R.drawable.ic_empty_off)

        // Asignar el mensaje y el color basado en la criticidad
        val symptomsInfoColor = when (verification.criticality) {
            "Crítico" -> {
                holder.tvSymptomsInfo.text = context.getString(R.string.critical_message)
                ContextCompat.getColor(context, R.color.critical)
            }
            "No Crítico" -> {
                holder.tvSymptomsInfo.text = context.getString(R.string.not_critical_message)
                ContextCompat.getColor(context, R.color.not_critical)
            }
            "Solución rápida" -> {
                holder.tvSymptomsInfo.text = context.getString(R.string.quick_solution_message)
                ContextCompat.getColor(context, R.color.quick_solution)
            }
            "Intrascendente" -> {
                holder.tvSymptomsInfo.text = context.getString(R.string.inconsequential_message)
                ContextCompat.getColor(context, R.color.inconsequential)
            }
            else -> {
                holder.tvSymptomsInfo.text = ""
                ContextCompat.getColor(context, R.color.black)
            }
        }
        holder.tvSymptomsInfo.setTextColor(symptomsInfoColor)

        // Configurar visibilidad de los síntomas y spinner basado en el estado de isChecked
        holder.spinnerSymptoms.visibility = if (verification.isChecked == 2) View.VISIBLE else View.GONE
        holder.tvSymptomsInfo.visibility = if (verification.isChecked == 2) View.VISIBLE else View.GONE

        // Poblar el spinner con los síntomas si están disponibles
        if (verification.symptomsList != null) {
            val symptomsAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, verification.symptomsList)
            holder.spinnerSymptoms.adapter = symptomsAdapter
        }

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
                holder.spinnerSymptoms.visibility = View.VISIBLE
                holder.tvSymptomsInfo.visibility = View.VISIBLE
            }
        }

        // Manejar el clic en el botón de imagen
        holder.btnImage.setOnClickListener {
            showImageDialog()
        }
    }

    override fun getItemCount(): Int {
        return verificationList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnOn: ImageButton = itemView.findViewById(R.id.btn_on)
        val btnOff: ImageButton = itemView.findViewById(R.id.btn_off)
        val btnImage: ImageButton = itemView.findViewById(R.id.btn_image)
        val tvVerification: TextView = itemView.findViewById(R.id.tv_verification)
        val spinnerSymptoms: Spinner = itemView.findViewById(R.id.spinner_symptoms)
        val tvSymptomsInfo: TextView = itemView.findViewById(R.id.tv_symptom_info)
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
