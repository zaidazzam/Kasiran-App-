package com.bdi.kasiran.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.databinding.ItemConfirmBinding
import com.bdi.kasiran.response.cart.Cart
import java.text.NumberFormat
import java.util.Locale

class ConfirmOrderAdapter(val listCart: List<Cart>, val context: Context) :
    RecyclerView.Adapter<ConfirmOrderAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemConfirmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: Cart, context: Context) {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0

            binding.txtNama.text = cartItem.nama
            binding.txtHarga.text = numberFormat.format(cartItem.harga)
            binding.txtStok1.text = context.getString(R.string.qty_confirm, cartItem.qty.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemConfirmBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listCart.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCart[position], context)
    }
}
