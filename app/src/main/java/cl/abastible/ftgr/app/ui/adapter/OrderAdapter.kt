package cl.abastible.ftgr.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.Order

class OrderAdapter(
    private var orderList: List<Order>,
    private val onOrderClickListener: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    // Este método se llama cuando se crea un nuevo ViewHolder. Infla la vista de la tarjeta de la lista de pedidos y crea un ViewHolder para ella
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_cardview, parent, false)
        return ViewHolder(view)
    }

    // Este método vincula los datos del pedido actual con un ViewHolder
    override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {

        // Obtén la orden de la posición actual
        val order = orderList[position]

        // Ajusta el texto del nombre del cliente
        holder.tvClientName.text = order.clientName

        // Ajusta el texto de la dirección, si existe comuna, concatenarla
        holder.tvAddress.text = order.address + ", " + order.commune

        // Ajusta el texto de la hora
        holder.tvTime.text = order.time

        // Ajusta la visibilidad del icono de estrella VIP
        if (order.isVip) {
            holder.vipStar.visibility = View.VISIBLE
        } else {
            holder.vipStar.visibility = View.INVISIBLE
        }

        // Ajusta el color de fondo de la tarjeta según el estado de la orden
        when (order.status) {
            "pendiente" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_pending_cardview)
            "en curso" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_in_progress_cardview)
            "completado" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_completed_cardview)
            "atrasado" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_delayed_cardview)
            "cancelado" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_canceled_cardview)
            "rechazado" -> holder.itemView.setBackgroundResource(R.drawable.bg_order_rejected_cardview)
            else -> holder.itemView.setBackgroundResource(R.drawable.bg_order_pending_cardview)
        }
    }

    // Este método retorna el número total de elementos en la lista de pedidos
    override fun getItemCount(): Int {
        return orderList.size
    }

    //Actualiza la lista de órdenes del adaptador
    fun updateOrders(newOrders: List<Order>) {
        orderList = newOrders
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tv_client_name)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val vipStar: ImageView = itemView.findViewById(R.id.vip_star)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Invoca la función lambda con el pedido seleccionado
                    onOrderClickListener.invoke(orderList[position])
                }
            }
        }
    }
}