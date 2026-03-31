package com.example.mealzone.adapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import com.example.mealzone.databinding.MenuItemBinding
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealzone.model.MenuItem
import com.example.mealzone.DetailsActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MenuAdapter(
    private val menuItems: MutableList<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    // ✅ 👇 YAHAN HONA CHAHIYE (IMPORTANT)
    fun updateData(newItems: List<MenuItem>) {
        menuItems.clear()
        menuItems.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]

            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredients)
                putExtra("MenuItemPrice", menuItem.foodPrice)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice

                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext)
                    .load(uri)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // cache
                    .skipMemoryCache(false) // memory me store
                    .centerCrop() // proper fit (optional)
                    .into(menuImage)
            }
        }
    }
}