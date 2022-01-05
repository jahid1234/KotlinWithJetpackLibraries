package com.example.android.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty

class PhotoGridAdapter(private val listener : OnItemClickListenerCustom) : ListAdapter<MarsProperty, PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback){

    class MarsPropertyViewHolder(val binding: GridViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val imageItem: ImageView = binding.marsImage

        fun bind(marsProperty: MarsProperty,listener: OnItemClickListenerCustom) {
            val imgUri = marsProperty.imgSrcUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(imageItem.context)
                .load(imgUri)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
                .into(imageItem)

            binding.root.setOnClickListener{
                if (marsProperty != null) {
                    listener.onItemClickCustom(marsProperty)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MarsPropertyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
                //use view binding
                val binding = GridViewItemBinding.inflate(layoutInflater, parent, false)

                // return TextItemViewHolder(view)
                return MarsPropertyViewHolder(binding)
            }
        }


    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MarsPropertyViewHolder {
       // return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
        return MarsPropertyViewHolder.from(parent)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.bind(marsProperty,listener)
    }

    interface OnItemClickListenerCustom {
        fun onItemClickCustom(marsProperty: MarsProperty)
    }

}