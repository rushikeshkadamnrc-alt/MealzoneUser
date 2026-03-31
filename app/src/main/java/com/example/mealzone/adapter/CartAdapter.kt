package com.example.mealzone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealzone.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartImageList: MutableList<String>,
    private var cartDescriptions: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredient: MutableList<String>,
    private val cartKeys: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun getItemNames(): MutableList<String> = cartItems.toMutableList()
    fun getItemPrices(): MutableList<String> = cartItemPrices.toMutableList()
    fun getItemImages(): MutableList<String> = cartImageList.toMutableList()
    fun getItemDescriptions(): MutableList<String> = cartDescriptions.toMutableList()
    fun getItemIngredients(): MutableList<String> = cartIngredient.toMutableList()
    fun getItemQuantities(): MutableList<Int> = cartQuantity.toMutableList()

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userId = auth.currentUser?.uid ?: ""
    private val cartItemsReference: DatabaseReference =
        database.reference.child("user").child(userId).child("CartItems")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun getUpdatedItemsQuantities(): MutableList<Int> {
        return cartQuantity.toMutableList()
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                cartfoodname.text = cartItems[position]
                cartPrices.text = cartItemPrices[position]
                cartitemQuantity.text = cartQuantity[position].toString()

                Glide.with(context)
                    .load(cartImageList[position])
                    .into(cartImage)
            }

            binding.minusbutton.setOnClickListener {
                decreaseQuantity(position, binding)
            }

            binding.plusebutton.setOnClickListener {
                increaseQuantity(position, binding)
            }

            binding.deletebutton.setOnClickListener {
                deleteItem(position)
            }
        }
    }

    private fun increaseQuantity(position: Int, binding: CartItemBinding) {
        if (cartQuantity[position] < 10) {
            cartQuantity[position]++
            binding.cartitemQuantity.text = cartQuantity[position].toString()
        }
    }

    private fun decreaseQuantity(position: Int, binding: CartItemBinding) {
        if (cartQuantity[position] > 1) {
            cartQuantity[position]--
            binding.cartitemQuantity.text = cartQuantity[position].toString()
        }
    }

    private fun deleteItem(position: Int) {
        if (position < 0 || position >= cartKeys.size) return

        val key = cartKeys[position]

        cartItemsReference.child(key).removeValue()
            .addOnSuccessListener {
                // ✅ Remove all lists safely
                cartItems.removeAt(position)
                cartItemPrices.removeAt(position)
                cartImageList.removeAt(position)
                cartDescriptions.removeAt(position)
                cartQuantity.removeAt(position)
                cartIngredient.removeAt(position)
                cartKeys.removeAt(position)

                // ✅ Notify adapter safely
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)

                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
            }
    }
}