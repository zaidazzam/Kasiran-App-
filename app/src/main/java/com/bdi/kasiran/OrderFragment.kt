package com.bdi.kasiran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.OrderAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.Menu
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class OrderFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var viewModel: OrderViewModel
    private lateinit var totalBayar: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        viewModel.getTransaksi(api, view, activity).observe(viewLifecycleOwner) { data ->
            val btnBayar = view.findViewById<Button>(R.id.btnBayar)
            btnBayar.setOnClickListener{
                val bundle = Bundle()
                bundle.putParcelableArrayList(CART_DATA, data)
                bundle.putString(TOTAL, totalBayar)

                findNavController().navigate(R.id.confirmOrderFragment, bundle)
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) {
            totalBayar = it
            val txtTotalBayar = view.findViewById<TextView>(R.id.txtTotalPembayaran)
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.setMaximumFractionDigits(0);

            txtTotalBayar?.text = numberFormat.format(it.toDouble()).toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.totalPrice.value = 0.toString()
    }

    companion object {
        const val TOTAL = "TOTAL"
        const val CART_DATA = "MY CART"
    }
}