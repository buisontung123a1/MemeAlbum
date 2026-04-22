package com.memealbum.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.memealbum.app.R
import com.memealbum.app.data.model.MemeItem
import com.memealbum.app.databinding.ItemMemeBinding
import com.memealbum.app.ui.PhotoViewActivity

class MemeAdapter(
    private val onItemClick: (MemeItem, Int, View) -> Unit,
    private val onItemLongClick: (MemeItem) -> Unit
) : ListAdapter<MemeItem, MemeAdapter.MemeViewHolder>(MemeDiffCallback()) {

    private var selectedIds: Set<String> = emptySet()
    private var lastAnimatedPosition = -1

    fun setSelectedIds(ids: Set<String>) {
        selectedIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val binding = ItemMemeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        // Make each item square based on screen width / 3 columns
        val size = parent.measuredWidth / 3
        binding.root.layoutParams = ViewGroup.LayoutParams(size, size)
        return MemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        holder.bind(getItem(position), position)
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastAnimatedPosition) {
            val anim = AnimationUtils.loadAnimation(view.context, R.anim.item_animation_fall_down)
            view.startAnimation(anim)
            lastAnimatedPosition = position
        }
    }

    inner class MemeViewHolder(private val binding: ItemMemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meme: MemeItem, position: Int) {
            // Set transition name for shared element
            binding.ivMeme.transitionName = PhotoViewActivity.TRANSITION_NAME + "_$position"

            Glide.with(binding.root.context)
                .load(meme.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .placeholder(R.drawable.placeholder_meme)
                .error(R.drawable.placeholder_meme)
                .centerCrop()
                .into(binding.ivMeme)

            // Selection overlay
            val isSelected = selectedIds.contains(meme.id)
            binding.overlaySelected.visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.ivCheckmark.visibility = if (isSelected) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                if (selectedIds.isNotEmpty()) {
                    onItemLongClick(meme)
                } else {
                    onItemClick(meme, position, binding.ivMeme)
                }
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(meme)
                true
            }
        }
    }

    class MemeDiffCallback : DiffUtil.ItemCallback<MemeItem>() {
        override fun areItemsTheSame(oldItem: MemeItem, newItem: MemeItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MemeItem, newItem: MemeItem) =
            oldItem == newItem
    }
}
