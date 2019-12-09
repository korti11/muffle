package io.korti.muffle.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.korti.muffle.EditMufflePointActivity
import io.korti.muffle.R
import io.korti.muffle.entity.MufflePoint
import kotlinx.android.synthetic.main.card_muffle.view.*

class MuffleCardAdapter : RecyclerView.Adapter<MuffleCardAdapter.MuffleCardHolder>() {

    private val data = listOf(
        MufflePoint(0f, 0f, "JKU Universität"),
        MufflePoint(0f, 0f, "Home", active = true),
        MufflePoint(0f, 0f, "Work", enable = false),
        MufflePoint(0f, 0f, "Cinema"),
        MufflePoint(0f, 0f, "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    )

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuffleCardHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_muffle, parent, false) as CardView
        return MuffleCardHolder(cardView)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MuffleCardHolder, position: Int) {
        val mufflePoint = data[position]
        holder.cardView.apply {
            val image = BitmapFactory.decodeResource(resources, R.drawable.map_default)
            mapsImage.setImageBitmap(image)
            muffleName.text = mufflePoint.name
            muffleStatus.text = context.getString(R.string.muffle_status, mufflePoint.getStatus())
            edButton.apply {
                text = if (mufflePoint.enable) {
                    context.getString(R.string.btn_disable)
                } else {
                    context.getString(R.string.btn_enable)
                }
            }

            edButton.setOnClickListener {
                if (mufflePoint.enable) {
                    mufflePoint.enable = false
                    edButton.text = context.getString(R.string.btn_enable)
                } else {
                    mufflePoint.enable = true
                    edButton.text = context.getString(R.string.btn_disable)
                }
                muffleStatus.text =
                    context.getString(R.string.muffle_status, mufflePoint.getStatus())
            }
            editButton.setOnClickListener {
                Intent(context, EditMufflePointActivity::class.java).also {
                    context.startActivity(it)
                }
            }
        }
    }

    class MuffleCardHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

}