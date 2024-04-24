package com.bdi.kasiran.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.CallBackInterface
import com.bdi.kasiran.R
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.Menu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(val listmenu: List<Menu>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }
    var callBackInterface: CallBackInterface? = null
    var total: Double = 0.0
    var cart: ArrayList<Cart> = arrayListOf<Cart>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listmenu.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = listmenu[position]
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        holder.txtNamaMenu.text = menu.menu_name
        holder.txtHargaMenu.text = numberFormat.format(menu.menu_price.toDouble())
        updateStockDisplay(holder, menu)

        // Load image into ImageView using Glide
        Glide.with(holder.itemView.context)
            .load(menu.menu_image)
            .apply(RequestOptions().placeholder(R.drawable.iv_sample_product))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.txtGambar)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("menu", menu)
            it.findNavController().navigate(R.id.menuDetailFragment, bundle)
        }

        holder.btnPlus.setOnClickListener {
            val currentQty = holder.txtQty.text.toString().toInt()
            holder.txtQty.text = (currentQty + 1).toString()

            val currentStock = menu.menu_qty.toInt()
            if (currentStock > 0) {
                menu.menu_qty = (currentStock - 1).toString()
                updateStockDisplay(holder, menu)

                total += menu.menu_price.toDouble()

                val index = cart.indexOfFirst { it.id == menu.menu_uuid }
                if (index != -1) {
                    cart[index].qty += 1
                } else {
                    val itemCart = Cart(menu.menu_uuid, menu.menu_name, menu.menu_price.toDouble(), 1)
                    cart.add(itemCart)
                }
            }

            callBackInterface?.passResultCallback(total.toString(), cart)
        }

        holder.btnMinus.setOnClickListener {
            val currentQty = holder.txtQty.text.toString().toInt()
            if (currentQty > 0) {
                holder.txtQty.text = (currentQty - 1).toString()
                menu.menu_qty = (menu.menu_qty.toInt() + 1).toString()
                updateStockDisplay(holder, menu)

                total -= menu.menu_price.toDouble()

                val index = cart.indexOfFirst { it.id == menu.menu_uuid }
                if (index != -1) {
                    if (cart[index].qty > 1) {
                        cart[index].qty -= 1
                    } else {
                        cart.removeAt(index)
                    }
                }
            }

            callBackInterface?.passResultCallback(total.toString(), cart)
        }


    }
    private fun updateStockDisplay(holder: ViewHolder, menu: Menu) {
        val currentStock = menu.menu_qty.toInt()
        holder.txtStok.text = currentStock.toString()
        holder.btnPlus.isEnabled = currentStock > 0
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMenu = itemView.findViewById<TextView>(R.id.txt_nama)
        val txtHargaMenu = itemView.findViewById<TextView>(R.id.txt_harga)
        val txtStok = itemView.findViewById<TextView>(R.id.txt_stok)
        val txtQty = itemView.findViewById<TextView>(R.id.txtQty)

        val txtGambar = itemView.findViewById<ImageView>(R.id.img_gambar)

        val btnPlus = itemView.findViewById<ImageButton>(R.id.btnPlus)
        val btnMinus = itemView.findViewById<ImageButton>(R.id.btnMinus)
    }
}
