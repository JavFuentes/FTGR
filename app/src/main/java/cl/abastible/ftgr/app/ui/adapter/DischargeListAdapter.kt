package cl.abastible.ftgr.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R

class DischargeListAdapter(
    private var litersList: MutableList<Float>,
    private val interactionListener: InteractionListener
) : RecyclerView.Adapter<DischargeListAdapter.ViewHolder>() {

    interface InteractionListener {
        fun onEdit(position: Int, currentAmount: Float)
        fun onDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.discharged_liters_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(litersList[position])
    }

    override fun getItemCount(): Int = litersList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLiters: TextView = itemView.findViewById(R.id.tv_liters)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(liters: Float) {
            tvLiters.text = liters.toString()
            btnEdit.setOnClickListener {
                interactionListener.onEdit(adapterPosition, liters)
            }
            btnDelete.setOnClickListener {
                interactionListener.onDelete(adapterPosition)
            }
        }
    }

    fun updateData(newLitersList: MutableList<Float>) {
        litersList = newLitersList
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, newAmount: Float) {
        if (position >= 0 && position < litersList.size) {
            litersList[position] = newAmount
            notifyItemChanged(position)
        }
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < litersList.size) {
            litersList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, litersList.size)
        }
    }

}
