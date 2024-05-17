package com.bdi.kasiran

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdi.kasiran.OrderFragment.Companion.CART_DATA
import com.bdi.kasiran.OrderFragment.Companion.TOTAL
import com.bdi.kasiran.adapter.ConfirmOrderAdapter
import com.bdi.kasiran.adapter.OrderAdapter
import com.bdi.kasiran.databinding.FragmentConfirmOrderBinding
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.diskon.Diskon
import com.bdi.kasiran.response.order.OrderItem
import com.bdi.kasiran.response.order.OrderStore
import java.text.NumberFormat
import java.util.Locale

class ConfirmOrderFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private val viewModel: ConfirmOrderViewModel by viewModels()
    private lateinit var binding: FragmentConfirmOrderBinding
    private lateinit var cartData: List<Cart>
    private var discountNominal = 0.0
    private var total = 0.0
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
    }

    // private lateinit var orderAdapter: OrderAdapter // Tidak diperlukan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            cartData = bundle.getParcelableArrayList<Cart>(CART_DATA)!!.toList()
            total = bundle.getString(TOTAL)!!.toDouble()
            setItemData(cartData.toList())
        }

        viewModel.getDiskon(api).observe(viewLifecycleOwner) { setDiscountSpinner(it) }
        setPaymentSpinner()

        binding.valTotal.text = requireContext().getString(R.string.total_price, total.toString())
        binding.btnProcess.setOnClickListener { onSubmit() }
    }

    private fun setItemData(listCart: List<Cart>) {
        val adapter = ConfirmOrderAdapter(listCart, requireContext()) // Menukar posisi parameter
        binding.rvOrderDetail.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }
    }


    private fun setDiscountSpinner(list: List<Diskon>) {
        val items = arrayListOf<String>()
        items.add("Pilih kode")
        list.forEach() {
            items.add(it.diskon_code)
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = binding.spinDiscount
        spinner.adapter = adapter
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        var newTotal = 0.0
                        discountNominal = list[position-1].nominal.toDouble()
                        if (discountNominal < total) {
                            newTotal = total - discountNominal
                        }
                        val localeID = Locale("in", "ID")
                        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                        numberFormat.maximumFractionDigits = 0
                        val formattedTotal = numberFormat.format(newTotal)
                        Log.d(TAG, "setDiscountSpinner total: $formattedTotal")
                        binding.valTotal.text = formattedTotal
                    } else {
                        val localeID = Locale("in", "ID")
                        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                        numberFormat.maximumFractionDigits = 0
                        val formattedTotal = numberFormat.format(total)
                        binding.valTotal.text = formattedTotal
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected (optional)
                }
            }
    }


    private fun setPaymentSpinner() {
        val items = listOf("Cash", "Cashless")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinPayment.adapter = adapter
    }

    // ConfirmOrderFragment
    private fun onSubmit() {
        var discount = ""
        if (binding.spinDiscount.selectedItemPosition != 0) {
            discount = binding.spinDiscount.selectedItem.toString()
        }
        val payment = binding.spinPayment.selectedItem.toString()
        val note = binding.edNote.text.toString()
        val listOrderItem = mutableListOf<OrderItem>()

        cartData.forEach() {
            val item = OrderItem(it.id, it.qty)
            listOrderItem.add(item)
        }

        Log.d(TAG, "onSubmit data: $listOrderItem")

        val order = OrderStore(
            order_note = note,
            payment_type = payment,
            diskon_code = discount,
            order_list = listOrderItem
        )
        viewModel.storeOrder(api, order).observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            if (response.success) {
                // Clear the data in the adapter after successful transaction
                (binding.rvOrderDetail.adapter as? OrderAdapter)?.clearData()
                (binding.rvOrderDetail.adapter as? OrderAdapter)?.clearItemQtyMap()

                // Clear total after successful transaction
                total = 0.0
                val localeID = Locale("in", "ID")
                val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                numberFormat.maximumFractionDigits = 0
                val formattedTotal = numberFormat.format(total)
                binding.valTotal.text = formattedTotal

                // Clear SharedPreferences data
                clearSharedPreferencesData()

                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun clearSharedPreferencesData() {
        val editor = sharedPreferences.edit()
        editor.putFloat("total", 0.0f)
        editor.putString("cart", "")
        editor.putString("itemQtyMap", "")
        editor.apply()
    }






    companion object {
        const val TAG = "ConfirmOrderFragment"
    }
}
