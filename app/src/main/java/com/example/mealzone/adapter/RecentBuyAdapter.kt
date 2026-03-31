package com.example.mealzone.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealzone.databinding.RecentBuyItemBinding

class RecentBuyAdapter(
    private val context: Context,
    private val foodNameList: List<String>,
    private val foodImageList: List<String>,
    private val foodPriceList: List<String>,
    private val foodQuantityList: List<Int>
) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = RecentBuyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNameList.size

    inner class RecentViewHolder(private val binding: RecentBuyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val name = foodNameList.getOrNull(position) ?: ""
            val price = foodPriceList.getOrNull(position) ?: ""
            val quantity = foodQuantityList.getOrNull(position)?.toString() ?: "0"
            val imageUri = foodImageList.getOrNull(position)

            binding.foodName.text = name
            binding.foodPrice.text = price
            binding.foodQuantity.text = quantity

            if (!imageUri.isNullOrEmpty()) {
                Glide.with(binding.foodImage.context)
                    .load(Uri.parse(imageUri))
                    .into(binding.foodImage)
            } else {
                binding.foodImage.setImageDrawable(null) // safe null handling
            }
        }
    }
}