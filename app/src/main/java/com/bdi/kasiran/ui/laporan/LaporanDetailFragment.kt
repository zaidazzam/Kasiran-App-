package com.bdi.kasiran.ui.laporan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdi.kasiran.R
import com.bdi.kasiran.response.order.Order

class LaporanDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan_detail, container, false)
        val args = arguments
        if (args != null) {
            val order = args.getParcelable<Order>("transaksi")
            if (order != null) {
                displayMenuDetails(view, order)
            }
        }
        return view
    }

    private fun displayMenuDetails(view: View, order: Order) {
        // Periksa ID TextView yang digunakan
        Log.d("MenuDetailFragment", "Menu Name: ${order.order_no}")

//        view.findViewById<TextView>(R.id.name_product_detail).text = menu.menu_name
//        view.findViewById<TextView>(R.id.price_product_detail).text = menu.menu_price
//        view.findViewById<TextView>(R.id.stokmenu).text = menu.menu_qty
//        view.findViewById<TextView>(R.id.menu_type).text = menu.menu_type
//        view.findViewById<TextView>(R.id.desc_product).text = menu.menu_desc
//
//        // Load image into ImageView using Glide
//        Glide.with(view)
//            .load(menu.menu_image)
//            .into(view.findViewById<ImageView>(R.id.img_product_detail))
    }
}
