package com.example.mealzone.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzone.PayOutActivity
import com.example.mealzone.adapter.CartAdapter
import com.example.mealzone.databinding.FragmentCartBinding
import com.example.mealzone.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        setupCartRecyclerView()
        setupProceedButton()

        return binding.root
    }

    private fun setupCartRecyclerView() {
        val foodReference = database.reference.child("user").child(userId).child("CartItems")

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodNames = mutableListOf<String>()
                val foodPrices = mutableListOf<String>()
                val foodImages = mutableListOf<String>()
                val foodDescriptions = mutableListOf<String>()
                val foodIngredients = mutableListOf<String>()
                val foodQuantities = mutableListOf<Int>()
                val cartKeys = mutableListOf<String>()

                for (foodSnapshot in snapshot.children) {
                    val item = foodSnapshot.getValue(CartItems::class.java)
                    val key = foodSnapshot.key ?: continue

                    cartKeys.add(key)
                    foodNames.add(item?.foodName ?: "")
                    foodPrices.add(item?.foodPrice ?: "0")
                    foodImages.add(item?.foodImage ?: "")
                    foodDescriptions.add(item?.foodDescription ?: "")
                    foodIngredients.add(item?.foodIngredient ?: "")
                    foodQuantities.add(item?.foodQuantity ?: 1)
                }

                cartAdapter = CartAdapter(
                    requireContext(),
                    foodNames,
                    foodPrices,
                    foodImages,
                    foodDescriptions,
                    foodQuantities,
                    foodIngredients,
                    cartKeys
                )

                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let {
                    Toast.makeText(it, "Data Not Fetch", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupProceedButton() {
        binding.proceedbutton.setOnClickListener {
            if (!::cartAdapter.isInitialized || cartAdapter.itemCount == 0) {
                context?.let { Toast.makeText(it, "Cart is empty", Toast.LENGTH_SHORT).show() }
                return@setOnClickListener
            }

            // Adapter ki internal list hi use karenge, Firebase snapshot se duplicate mat banao
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", ArrayList(cartAdapter.getItemNames()))
            intent.putExtra("FoodItemPrice", ArrayList(cartAdapter.getItemPrices()))
            intent.putExtra("FoodItemImage", ArrayList(cartAdapter.getItemImages()))
            intent.putExtra("FoodItemDescription", ArrayList(cartAdapter.getItemDescriptions()))
            intent.putExtra("FoodItemIngredient", ArrayList(cartAdapter.getItemIngredients()))
            intent.putExtra("FoodItemQuantities", ArrayList(cartAdapter.getItemQuantities()))

            startActivity(intent)
        }
    }
}