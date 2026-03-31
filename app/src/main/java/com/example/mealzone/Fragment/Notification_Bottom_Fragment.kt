package com.example.mealzone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzone.adapter.NotificationAdapter
import com.example.mealzone.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notification_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notification = listOf("Your order has been Canceled Succ...","Order has been taken by the driver","Congrats Your Order Placed")
        val notificationImage = listOf(R.drawable.sademoji_svg,R.drawable.truck,R.drawable.congratulation)
        val adapter = NotificationAdapter(ArrayList(notification),ArrayList(notificationImage))
        binding.notificationRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecycleView.adapter = adapter
        return binding.root

    }

    companion object {

    }
}