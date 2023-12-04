package cl.abastible.ftgr.app.ui.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.SupplyVerification
import cl.abastible.ftgr.app.ui.viewmodel.Supply2ViewModel

class Supply2Adapter(
    private val verificationList: List<SupplyVerification>,
    private val viewModel: Supply2ViewModel,
    private val context: Context
) : RecyclerView.Adapter<SupplyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.supply_verification_cardview, parent, false)
        return SupplyAdapter.ViewHolder(view)
    }

    // Este método vincula los datos del elemento de verificación actual con un ViewHolder
    override fun onBindViewHolder(holder: SupplyAdapter.ViewHolder, position: Int) {
        val verification = verificationList[position]
        holder.tvVerification.text = verification.description

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
                holder.spinnerSymptoms.visibility = View.VISIBLE
                holder.tvSymptomsInfo.visibility = View.VISIBLE
            }
        }

        // Manejar el clic en el botón de imagen
        holder.btnImage.setOnClickListener {
            showImageDialog()
        }
    }

    // Este método retorna el número total de elementos en la lista de verificación
    override fun getItemCount(): Int {
        return verificationList.size
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