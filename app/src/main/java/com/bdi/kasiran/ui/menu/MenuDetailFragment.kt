package com.bdi.kasiran.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bdi.kasiran.R
import com.bdi.kasiran.response.menu.Menu
import com.bumptech.glide.Glide

class MenuDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu_detail, container, false)
        val args = arguments
        if (args != null) {
            val menu = args.getParcelable<Menu>("menu")
            if (menu != null) {
                displayMenuDetails(view, menu)
            }
        }
        return view
    }

    private fun displayMenuDetails(view: View, menu: Menu) {
        // Periksa ID TextView yang digunakan
        Log.d("MenuDetailFragment", "Menu Name: ${menu.menu_name}")

        view.findViewById<TextView>(R.id.name_product_detail).text = menu.menu_name
        view.findViewById<TextView>(R.id.price_product_detail).text = menu.menu_price
        view.findViewById<TextView>(R.id.stokmenu).text = menu.menu_qty
        view.findViewById<TextView>(R.id.menu_type).text = menu.menu_type
        view.findViewById<TextView>(R.id.desc_product).text = menu.menu_desc

        // Load image into ImageView using Glide
        Glide.with(view)
            .load(menu.menu_image)
            .into(view.findViewById<ImageView>(R.id.img_product_detail))
    }
}
