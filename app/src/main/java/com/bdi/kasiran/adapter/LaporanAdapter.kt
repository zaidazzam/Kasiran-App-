package com.bdi.kasiran.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.response.order.Order
import java.text.NumberFormat
import java.util.Locale

class LaporanAdapter(private val listTransaksi: List<Order>) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }
        holder.txtTanggalTransaksi.text = transaksi.created_at
        holder.txtNoNota.text = transaksi.order_no
        holder.txtStatus.text = transaksi.status
        holder.txtTransaksiTotal.text = numberFormat.format(transaksi.total_transaksi.toDouble())

        // Ubah warna background txtStatus berdasarkan status
        when (transaksi.status.toLowerCase(Locale.ROOT)) {
            "pending" -> holder.txtStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.yellow)
            "completed" -> holder.txtStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.green)
            "cancel" -> holder.txtStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.red)
            else -> holder.txtStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.abu) // opsional untuk status lain
        }

//        holder.itemView.setOnClickListener {
//            val bundle = Bundle().apply {
//                putParcelable("order", transaksi)
//            }
//            it.findNavController().navigate(R.id.laporanDetailFragment, bundle)
//        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("transaksi", transaksi)
            it.findNavController().navigate(R.id.laporanDetailFragment, bundle)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTanggalTransaksi: TextView = itemView.findViewById(R.id.txtTanggalTransaksi)
        val txtNoNota: TextView = itemView.findViewById(R.id.txtNoNota)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txtTransaksiTotal: TextView = itemView.findViewById(R.id.txtTransaksiTotal)
    }
}