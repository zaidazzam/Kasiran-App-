package com.bdi.kasiran.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.response.diskon.Diskon
import java.text.NumberFormat
import java.util.Locale

class DiskonAdapter(val listDiskon: List<Diskon>): RecyclerView.Adapter<DiskonAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiskonAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diskon, parent,false)
        return DiskonAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDiskon.size
    }

    override fun onBindViewHolder(holder: DiskonAdapter.ViewHolder, position: Int) {
        val diskon = listDiskon[position]
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.setMaximumFractionDigits(0);
        holder.txtDiskonCode.text = diskon.diskon_code
//        holder.txtlevel.text = diskon.diskon_level
        holder.txtnominal.text = numberFormat.format(diskon.nominal.toDouble()).toString()
//        holder.itemView.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable("diskon",diskon )
//            it.findNavController().navigate(R.id.laporanDetailFragment, bundle)
//        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDiskonCode = itemView.findViewById<TextView>(R.id.txtDiskonCode)
//        val txtlevel = itemView.findViewById<TextView>(R.id.leveldiskon)
        val txtnominal = itemView.findViewById<TextView>(R.id.nominalDiskon)
//        val txtTransaksiTotal = itemView.findViewById<TextView>(R.id.txtTransaksiTotal)
    }
}