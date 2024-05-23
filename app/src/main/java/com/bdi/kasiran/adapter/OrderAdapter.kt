package com.bdi.kasiran.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.CallBackInterface
import com.bdi.kasiran.R
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.Menu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(val context: Context, val listmenu: List<Menu>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }
    var callBackInterface: CallBackInterface? = null
    var total: Double = 0.0
    var cart: ArrayList<Cart> = arrayListOf<Cart>()
    private val itemQtyMap: MutableMap<String, Int> = mutableMapOf()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        // Retrieve saved data from SharedPreferences
        total = sharedPreferences.getFloat("total", 0.0f).toDouble()
        val cartJson = sharedPreferences.getString("cart", "")
        val qtyJson = sharedPreferences.getString("itemQtyMap", "")

        if (!cartJson.isNullOrEmpty()) {
            val type: Type = object : TypeToken<ArrayList<Cart>>() {}.type
            cart = gson.fromJson(cartJson, type)
        }

        if (!qtyJson.isNullOrEmpty()) {
            val type: Type = object : TypeToken<MutableMap<String, Int>>() {}.type
            itemQtyMap.putAll(gson.fromJson(qtyJson, type))
        }
    }

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
            .apply(RequestOptions().placeholder(R.drawable.sample_photo))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.txtGambar)

        // Set the saved quantity
        val savedQty = itemQtyMap[menu.menu_uuid] ?: 0
        holder.txtQty.text = savedQty.toString()

//        holder.itemView.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable("menu", menu)
//            it.findNavController().navigate(R.id.menuDetailFragment, bundle)
//        }

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

                itemQtyMap[menu.menu_uuid] = currentQty + 1
                saveData()
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

                    itemQtyMap[menu.menu_uuid] = currentQty - 1
                    saveData()
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

    private fun saveData() {
        val editor = sharedPreferences.edit()
        editor.putFloat("total", total.toFloat())
        val cartJson = gson.toJson(cart)
        editor.putString("cart", cartJson)
        val qtyJson = gson.toJson(itemQtyMap)
        editor.putString("itemQtyMap", qtyJson)
        editor.apply()
    }

    fun clearData() {
        total = 0.0
        cart.clear()
        itemQtyMap.clear()
        val editor = sharedPreferences.edit()
        editor.putFloat("total", total.toFloat())
        editor.putString("cart", "")
        editor.putString("itemQtyMap", "")
        editor.apply()
    }
    fun clearItemQtyMap() {
        itemQtyMap.clear()
        saveData() // Simpan perubahan ke SharedPreferences
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMenu: TextView = itemView.findViewById(R.id.txt_nama)
        val txtHargaMenu: TextView = itemView.findViewById(R.id.txt_harga)
        val txtStok: TextView = itemView.findViewById(R.id.txt_stok)
        val txtQty: TextView = itemView.findViewById(R.id.txtQty)
        val txtGambar: ImageView = itemView.findViewById(R.id.img_gambar)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
    }
}
