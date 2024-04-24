package com.bdi.kasiran.ui.laporan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.adapter.LaporanDetailAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.MenuResponsePost
import com.bdi.kasiran.response.order.Order
import com.bdi.kasiran.response.order.OrderCompleteResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaporanDetailFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }  // Make sure this instance correctly provides your ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan_detail, container, false)
        val args = arguments
        var order: Order? = null

        if (args != null) {
            order = args.getParcelable("transaksi")
            if (order != null) {
                displayOrderDetails(view, order)
            }
        }

        setupButtons(view, order)

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_detail_Laporan)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (order != null) {
            recyclerView.adapter = LaporanDetailAdapter(order.order_list)
        }

        return view
    }

    private fun setupButtons(view: View, order: Order?) {
        view.findViewById<Button>(R.id.btn_complete).apply {
            visibility = if (order?.status == "completed") View.GONE else View.VISIBLE
            setOnClickListener {
                order?.order_no?.let { orderId ->
                    completeOrder(orderId)
                }
            }
        }
    }

    private fun completeOrder(orderId: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        token?.let {
            val authToken = "Bearer $it"
            api.getCompleteOrder(authToken, orderId).enqueue(object : Callback<OrderCompleteResponse> {
                override fun onResponse(call: Call<OrderCompleteResponse>, response: Response<OrderCompleteResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Order completed successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.menuOrderFragment)  // Assuming you want to navigate away
                    } else {
                        Toast.makeText(requireContext(), "Failed to complete the order", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OrderCompleteResponse>, t: Throwable) {
                    Log.e("ERROR", "Network error or API failure", t)
                    Toast.makeText(requireContext(), "Network error or API failure: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } ?: run {
            Toast.makeText(requireContext(), "Authentication token is not available", Toast.LENGTH_LONG).show()
        }
    }


    private fun displayOrderDetails(view: View, order: Order) {
        view.findViewById<TextView>(R.id.txt_invoice).text = order.order_no
        view.findViewById<TextView>(R.id.txt_metode_pembayaran).text = order.payment_type
        view.findViewById<TextView>(R.id.status_order).text = order.status
        view.findViewById<TextView>(R.id.hasil_tgl_order).text = order.updated_at
        view.findViewById<TextView>(R.id.txt_diskon).text = order.total_diskon?.toString() ?: "0"
        view.findViewById<TextView>(R.id.hasil_total_order).text = order.total_transaksi.toString()
        view.findViewById<TextView>(R.id.hasil_order_note).text = order.order_note
    }
}
