package com.bdi.kasiran

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
import com.bdi.kasiran.databinding.FragmentConfirmOrderBinding
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.diskon.Diskon
import com.bdi.kasiran.response.order.OrderItem
import com.bdi.kasiran.response.order.OrderStore

class ConfirmOrderFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private val viewModel: ConfirmOrderViewModel by viewModels()
    private lateinit var binding: FragmentConfirmOrderBinding
    private lateinit var cartData: List<Cart>
    private var discountNominal = 0.0
    private var total = 0.0

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
        val rvAdapter = ConfirmOrderAdapter(listCart, requireContext())
        binding.rvOrderDetail.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = rvAdapter
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
                        Log.d(TAG, "setDiscountSpinner total: $total")
                        binding.valTotal.text =
                            requireContext().getString(R.string.total_price, newTotal.toString())
                    } else {
                        binding.valTotal.text =
                            requireContext().getString(R.string.total_price, total.toString())
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
        viewModel.storeOrder(api, order).observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            if (it.success) {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    companion object {
        const val TAG = "ConfirmOrderFragment"
    }
}