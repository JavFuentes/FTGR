package cl.abastible.ftgr.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.LeaksProtocolItem

class LeaksProtocolAdapter(
    var leaksProtocolList: List<LeaksProtocolItem>
) : RecyclerView.Adapter<LeaksProtocolAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInstruction: TextView = itemView.findViewById(R.id.tv_instruction)

        fun bind(item: LeaksProtocolItem) {
            tvInstruction.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leaks_protocol_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(leaksProtocolList[position])
    }

    override fun getItemCount(): Int {
        return leaksProtocolList.size
    }
}
