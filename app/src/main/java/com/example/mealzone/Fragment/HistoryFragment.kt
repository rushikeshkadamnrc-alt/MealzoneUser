package com.example.mealzone.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mealzone.R
import com.example.mealzone.adapter.BuyAgainAdapter
import com.example.mealzone.databinding.FragmentHistoryBinding
import com.example.mealzone.RecentOrderItems
import com.example.mealzone.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        retrieveBuyHistory()

        binding.recentbuyitem.setOnClickListener {
            listOfOrderItem.firstOrNull()?.let { recentBuy ->
                val intent = Intent(requireContext(), RecentOrderItems::class.java)
                intent.putExtra("RecentBuyOrderItem", recentBuy) // single OrderDetails
                startActivity(intent)
            }
        }

        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
    }

    private fun retrieveBuyHistory() {
        binding.recentbuyitem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val sortingQuery = buItemReference.orderByChild("currentTime")

        sortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let { listOfOrderItem.add(it) }
                }

                listOfOrderItem = listOfOrderItem.reversed().toMutableList()

                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                }

                if (listOfOrderItem.size > 1) {
                    setPreviouslyBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setDataInRecentBuyItem() {
        binding.recentbuyitem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull() ?: return

        val itemCount = recentOrderItem.foodNames?.size ?: 0
        binding.buyAgainFoodName.text = "Items: $itemCount"
        binding.buyAgainFoodPrice.text = recentOrderItem.totalPrice ?: ""

        val image = recentOrderItem.foodImages?.firstOrNull()
        if (!image.isNullOrEmpty()) {
            Glide.with(binding.root)
                .load(Uri.parse(image))
                .into(binding.buyAgainFoodImage)

            val isOrderIsAccepted = listOfOrderItem.firstOrNull()?.orderAccepted ?: false
            if (isOrderIsAccepted) {
                binding.orderedStatus.background.setTint(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.textColor)
                )
                binding.receivedButton.visibility = View.VISIBLE
            }
        }
    }

    private fun setPreviouslyBuyItemsRecyclerView() {
        val names = mutableListOf<String>()
        val prices = mutableListOf<String>()
        val images = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {
            names.add(listOfOrderItem[i].foodNames?.firstOrNull() ?: "")
            prices.add(listOfOrderItem[i].foodPrices?.firstOrNull() ?: "")
            images.add(listOfOrderItem[i].foodImages?.firstOrNull() ?: "")
        }

        buyAgainAdapter = BuyAgainAdapter(names, prices, images, requireContext())
        binding.buyAgainRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.buyAgainRecycleView.adapter = buyAgainAdapter
    }
}