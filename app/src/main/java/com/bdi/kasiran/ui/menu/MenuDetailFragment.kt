    package com.bdi.kasiran.ui.menu

    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.navigation.fragment.findNavController
    import com.bdi.kasiran.R
    import com.bdi.kasiran.response.menu.Menu
    import com.bumptech.glide.Glide
    import com.google.android.material.floatingactionbutton.FloatingActionButton
    import java.text.NumberFormat
    import java.util.Locale

    class MenuDetailFragment : Fragment() {

        private var menu: Menu? = null // Deklarasi di level class

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_menu_detail, container, false)
            val args = arguments
            menu = args?.getParcelable("menu") // Assign nilai ke variabel class
            menu?.let {
                displayMenuDetails(view, it)
            }
            val btnEdit = view.findViewById<FloatingActionButton>(R.id.btn_edit)
            btnEdit.setOnClickListener {
                menu?.menu_uuid?.let { menuUuid ->
                    val action = MenuDetailFragmentDirections.actionMenuDetailFragmentToMenuEditFragment(menuUuid)
                    findNavController().navigate(action)
                }
            }
            val btnAddStok = view.findViewById<ImageButton>(R.id.add_stock)
            btnAddStok.setOnClickListener {
                menu?.menu_uuid?.let { menuUuid ->
                    val action = MenuDetailFragmentDirections.actionMenunuDetailFragmentToMenuAddStokFragment(menuUuid)
                    findNavController().navigate(action)
                }
            }
            return view
        }

        private fun displayMenuDetails(view: View, menu: Menu) {
            // Periksa ID TextView yang digunakan
            Log.d("MenuDetailFragment", "Menu Name: ${menu.menu_name}")
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            view.findViewById<TextView>(R.id.name_product_detail).text = menu.menu_name
            view.findViewById<TextView>(R.id.price_product_detail).text = numberFormat.format(menu.menu_price.toDouble()).toString()
            view.findViewById<TextView>(R.id.stokmenu).text = menu.menu_qty
            view.findViewById<TextView>(R.id.menu_type).text = menu.menu_type
            view.findViewById<TextView>(R.id.desc_product).text = menu.menu_desc

            // Load image into ImageView using Glide
            Glide.with(view)
                .load(menu.menu_image)
                .into(view.findViewById<ImageView>(R.id.img_product_detail))
        }
    }
