package com.bdi.kasiran.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.response.order.Order
import java.text.NumberFormat
import java.util.Locale

class LaporanAdapter(val listTransaksi: List<Order>): RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent,false)
        return LaporanAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: LaporanAdapter.ViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.setMaximumFractionDigits(0);
        holder.txtTanggalTransaksi.text = transaksi.created_at
        holder.txtNoNota.text = "#0000" + transaksi.order_no
        holder.txtStatus.text = transaksi.status
        holder.txtTransaksiTotal.text = numberFormat.format(transaksi.total_transaksi.toDouble()).toString()
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("order",transaksi )
            it.findNavController().navigate(R.id.laporanDetailFragment, bundle)
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTanggalTransaksi = itemView.findViewById<TextView>(R.id.txtTanggalTransaksi)
        val txtNoNota = itemView.findViewById<TextView>(R.id.txtNoNota)
        val txtStatus = itemView.findViewById<TextView>(R.id.txtStatus)
        val txtTransaksiTotal = itemView.findViewById<TextView>(R.id.txtTransaksiTotal)
    }
}