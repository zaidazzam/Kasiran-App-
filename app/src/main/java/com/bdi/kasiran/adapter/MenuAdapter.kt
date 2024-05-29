package com.bdi.kasiran.adapter

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
        numberFormat.maximumFractionDigits = 0
        holder.txtNamaMenu.text = menu.menu_name
        holder.txtHargaMenu.text = numberFormat.format(menu.menu_price.toDouble()).toString()
        holder.txtStok.text = menu.menu_qty

        // Load image into ImageView using Glide
        Glide.with(holder.itemView.context)
            .load(menu.menu_image)
            .apply(RequestOptions().placeholder(R.drawable.sample_photo)) // Placeholder image while loading
            .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
            .into(holder.txtGambar)

//        holder.itemView.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable("menu", menu)
//            it.findNavController().navigate(R.id.menuDetailFragment, bundle)
//        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView, menu, position)
        }

        holder.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("menuUuid", menu.menu_uuid)
            }
            holder.itemView.findNavController().navigate(R.id.action_menuFragment_to_menuEditFragment, bundle)
        }
    }

    private fun showDeleteConfirmationDialog(view: View, menu: Menu, position: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus menu ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                listener.onDelete(menu, position)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMenu: TextView = itemView.findViewById(R.id.txt_nama)
        val txtHargaMenu: TextView = itemView.findViewById(R.id.txt_harga)
        val txtStok: TextView = itemView.findViewById(R.id.txt_stok)
        val txtGambar: ImageView = itemView.findViewById(R.id.img_gambar)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteProduk)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditProduk)
    }

    interface OnItemClickListener {
        fun onDelete(item: Menu, position: Int)
    }
}
