package cl.abastible.ftgr.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.abastible.ftgr.R
import cl.abastible.ftgr.app.domain.model.ImageItem

class CapturedImagesAdapter(
    private val images: MutableList<ImageItem>,
    private val onDeleteClicked: (position: Int) -> Unit,
    private val onImageClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<CapturedImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onImageClick(images[adapterPosition])
            }
        }

        val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val imageName: TextView = itemView.findViewById(R.id.imageName)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item_cardview, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = images[position]
        holder.imageThumbnail.setImageBitmap(imageItem.bitmap)
        holder.imageName.text = imageItem.name
        holder.deleteIcon.setOnClickListener {
            val currentPosition = holder.adapterPosition
            onDeleteClicked(currentPosition)
        }
    }

    override fun getItemCount(): Int = images.size
}
