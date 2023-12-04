package cl.abastible.ftgr.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.User

class UserAdapter(
    private val userList: List<User>,
    private val onUserClickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    // Este método se llama cuando se crea un nuevo ViewHolder. Infla la vista de la tarjeta de la lista de verificación de camiones y crea un ViewHolder para ella
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_cardview, parent, false)
        return ViewHolder(view)
    }

    // Este método vincula los datos del elemento de verificación actual con un ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Obtén el objeto de verificación de la posición actual
        val user = userList[position]

        // Ajusta el texto del nombre del usuario
        holder.tvName.text = user.name

        // Ajusta el texto del nombre de usuario del usuario
        holder.tvUserName.text = user.username
    }

    // Este método retorna el número total de elementos en la lista de verificación
    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    // Invoca la función lambda con el usuario seleccionado
                    onUserClickListener.invoke(userList[position])
                }
            }
        }
    }
}