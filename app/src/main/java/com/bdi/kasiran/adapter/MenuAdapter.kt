package com.bdi.kasiran.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.Menu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.Locale

class MenuAdapter(private var listmenu: List<Menu>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listmenu.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = listmenu[position]
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0 // Menggunakan setter untuk maximumFractionDigits
        holder.txtNamaMenu.text = menu.menu_name
        holder.txtHargaMenu.text = numberFormat.format(menu.menu_price.toDouble()).toString()
        holder.txtStok.text = menu.menu_qty

        // Load image into ImageView using Glide
        Glide.with(holder.itemView.context)
            .load(menu.menu_image)
            .apply(RequestOptions().placeholder(R.drawable.sample_photo)) // Placeholder image while loading
            .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
            .into(holder.txtGambar)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("menu", menu)
            it.findNavController().navigate(R.id.menuDetailFragment, bundle)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(menu, position, holder.itemView.context) // Memanggil fungsi dengan menyertakan context
        }
    }

    private fun showDeleteConfirmationDialog(menu: Menu, position: Int, context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_custom_dialog, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            dialog.dismiss()
            listener.onDelete(menu, position)
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMenu = itemView.findViewById<TextView>(R.id.txt_nama)
        val txtHargaMenu = itemView.findViewById<TextView>(R.id.txt_harga)
        val txtStok = itemView.findViewById<TextView>(R.id.txt_stok)
        val txtGambar = itemView.findViewById<ImageView>(R.id.img_gambar)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteProduk)
    }

    interface OnItemClickListener {
        fun onDelete(item: Menu, position: Int)
    }
}

