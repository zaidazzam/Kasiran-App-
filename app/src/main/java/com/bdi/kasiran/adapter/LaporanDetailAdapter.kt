package com.bdi.kasiran.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.response.order.OrderDetail
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.NumberFormat
import java.util.Locale

class LaporanDetailAdapter(private val listOrderDetail: List<OrderDetail>) :
    RecyclerView.Adapter<LaporanDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_laporan_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listOrderDetail.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderDetail = listOrderDetail[position]
        holder.bind(orderDetail)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val menuName: TextView = itemView.findViewById(R.id.txt_nama)
        private val menuPrice: TextView = itemView.findViewById(R.id.txt_harga)
        private val menuQty: TextView = itemView.findViewById(R.id.txt_stok)
        private val menuImage: ImageView = itemView.findViewById(R.id.img_gambar)

        fun bind(orderDetail: OrderDetail) {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID).apply {
                maximumFractionDigits = 0 // Menghilangkan digit pecahan
            }

            // Memformat harga ke dalam mata uang lokal
            val formattedPrice = numberFormat.format(orderDetail.menu_price)

            menuName.text = orderDetail.menu_name
            menuPrice.text = "Harga: $formattedPrice"
            menuQty.text = "${orderDetail.menu_qty} Item"

            // Base URL untuk gambar
            val baseUrl = "https://be.pos-kasiran.my.id/order" // Ganti dengan base URL server Anda

            // URL lengkap gambar
            val imageUrl = baseUrl + orderDetail.menu_image

            // Gunakan Glide untuk menampilkan gambar dengan placeholder dan transisi yang halus
            Glide.with(itemView.context)
                .load(orderDetail.menu_image)
                .placeholder(R.drawable.sample_photo) // Placeholder sementara gambar dimuat
                .transition(DrawableTransitionOptions.withCrossFade()) // Transisi halus
                .into(menuImage)

        }

    }
}
