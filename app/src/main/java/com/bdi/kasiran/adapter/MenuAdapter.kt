package com.bdi.kasiran.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class MenuAdapter(val listmenu: List<Menu>): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

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
        numberFormat.setMaximumFractionDigits(0);
        holder.txtNamaMenu.text = menu.menu_name
        holder.txtHargaMenu.text = numberFormat.format(menu.menu_price.toDouble()).toString()
        holder.txtStok.text = menu.menu_qty

        // Load image into ImageView using Gliden
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


//        holder.btnDelete.setOnClickListener {
//            Toast.makeText(holder.itemView.context, menu.nama,Toast.LENGTH_LONG).show()
//            val token = LoginActivity.sessionManager.getString("TOKEN")
//            val admin_id = LoginActivity.sessionManager.getString("ADMIN_ID")
//
//            api.deleteProduk(token.toString(),produk.id.toInt()).enqueue(object :Callback<MenuResponse>{
//                override fun onResponse(
//                    call: Call<MenuResponsePost>,
//                    response: Response<MenuResponsePost>
//                ) {
//                    Toast.makeText(holder.itemView.context, "Delete " +produk.nama.toString()+ " Success", Toast.LENGTH_LONG).show()
//
//                    holder.itemView.findNavController().navigate(R.id.menuFragment)
//                }
//
//                override fun onFailure(call: Call<MenuResponsePost>, t: Throwable) {
//                    Log.e("Error", t.toString())
//                }
//
//            })
//        }
//
//        holder.btnEdit.setOnClickListener {
//            Toast.makeText(holder.itemView.context, produk.menu_name, Toast.LENGTH_LONG).show()
//
//            val bundle = Bundle()
//            bundle.putParcelable("produk", produk)
//
//
//            holder.itemView.findNavController().navigate(R.id.produkFromEditFragment, bundle)
//        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMenu = itemView.findViewById<TextView>(R.id.txt_nama)
        val txtHargaMenu = itemView.findViewById<TextView>(R.id.txt_harga)
        val txtStok = itemView.findViewById<TextView>(R.id.txt_stok)
        val txtGambar = itemView.findViewById<ImageView>(R.id.img_gambar)


//        val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteProduk)
//        val btnEdit = itemView.findViewById<ImageButton>(R.id.btnEditProduk)
    }

}
