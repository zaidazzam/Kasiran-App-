package com.bdi.kasiran.ui.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.adapter.LaporanDetailAdapter
import com.bdi.kasiran.response.order.Order

class LaporanDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan_detail, container, false)
        val args = arguments
        var order: Order? = null

        if (args != null) {
            order = args.getParcelable<Order>("transaksi")
            if (order != null) {
                displayOrderDetails(view, order)
            }
        }

        if (order?.status == "completed") {
            view.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
            view.findViewById<Button>(R.id.btn_complete).visibility = View.GONE
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_detail_Laporan)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (order != null) {
            recyclerView.adapter = LaporanDetailAdapter(order.order_list)
        }

        return view
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
