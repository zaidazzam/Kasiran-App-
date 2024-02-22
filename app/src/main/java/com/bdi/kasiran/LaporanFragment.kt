package com.bdi.kasiran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.LaporanAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.order.OrderResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaporanFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var recyclerView: RecyclerView  // Tambahkan ini

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan, container, false)
        recyclerView = view.findViewById(R.id.rcv_listlaporan) // Initialize recyclerView here
        recyclerView.layoutManager = LinearLayoutManager(context)

        getLaporanTransaksi()
        return view
    }

    private fun getLaporanTransaksi(){
        val token = LoginActivity.sessionManager.getString("TOKEN")
        api.getOrder(token.toString()).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {
                if (response.isSuccessful) {
                    handleOrderDataResponse(response.body())
                } else {
                    Log.e("Error", "Failed to get menu data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("Error", "Failed to get menu data", t)
            }
        })
    }
    private fun handleOrderDataResponse(orderResponse: OrderResponse?) {
        if (orderResponse != null && orderResponse.success) {
            val orderList = orderResponse.data
            val rvAdapter = LaporanAdapter(orderList)

            // Set adapter to RecyclerView
            recyclerView.adapter = rvAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager

        } else {
            // Handle the case when the response is not successful or data is null
            Log.e("Error", "Failed to get menu data.")
        }
    }


}