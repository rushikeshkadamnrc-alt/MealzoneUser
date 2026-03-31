package com.example.mealzone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzone.adapter.RecentBuyAdapter
import com.example.mealzone.databinding.ActivityRecentOrderItemsBinding
import com.example.mealzone.model.OrderDetails

class RecentOrderItems : AppCompatActivity() {

    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodQuantites: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }


        val recentOrder = intent.getSerializableExtra("RecentBuyOrderItem") as? OrderDetails

        recentOrder?.let { order ->
            // Null-safe handling for food lists
            val names = order.foodNames ?: mutableListOf()
            val images = order.foodImages ?: mutableListOf()
            val prices = order.foodPrices ?: mutableListOf()
            // Agar quantities null hai to default 1 assign karo
            val quantities = order.foodQuantities ?: MutableList(names.size) { 1 }

            allFoodNames = ArrayList(names)
            allFoodImages = ArrayList(images)
            allFoodPrices = ArrayList(prices)
            allFoodQuantites = ArrayList(quantities)

            setAdapter()
        } ?: run {
            // Crash avoid
            allFoodNames = arrayListOf()
            allFoodImages = arrayListOf()
            allFoodPrices = arrayListOf()
            allFoodQuantites = arrayListOf()
        }
    }

    private fun setAdapter() {
        binding.recyclerViewRecentBuy.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(
            this,
            allFoodNames,
            allFoodImages,
            allFoodPrices,
            allFoodQuantites
        )
        binding.recyclerViewRecentBuy.adapter = adapter
    }
}