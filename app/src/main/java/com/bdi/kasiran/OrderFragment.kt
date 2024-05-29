package com.bdi.kasiran

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.OrderAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.Branch
import com.bdi.kasiran.response.menu.Menu
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

class OrderFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var totalBayar: String

    val totalPrice = MutableLiveData<String>()
    val cartData = MutableLiveData<ArrayList<Cart>>()
    val branchData = MutableLiveData<Branch>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMenuData(api).observe(viewLifecycleOwner) { data ->
            setItemData(view, data)
            setUpSearch(view, data)
        }

        totalPrice.observe(viewLifecycleOwner) {
            totalBayar = it
            val txtTotalBayar = view.findViewById<TextView>(R.id.txtTotalPembayaran)
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.setMaximumFractionDigits(0);

            txtTotalBayar?.text = numberFormat.format(it.toDouble()).toString()
        }

        cartData.observe(viewLifecycleOwner) { data ->
            val btnBayar = view.findViewById<Button>(R.id.btnBayar)
            btnBayar.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelableArrayList(CART_DATA, data)
                branchData.observe(viewLifecycleOwner) { branch ->
                    if (branchData.value != null) bundle.putParcelable("BRANCH", branch)
                }
                bundle.putString(TOTAL, totalBayar)

                findNavController().navigate(R.id.confirmOrderFragment, bundle)
            }
        }
    }

    private fun setItemData(view: View, data: List<Menu>) {
        val rvTransaksi = view.findViewById<RecyclerView>(R.id.rcv_listmenuorder)
        rvTransaksi.setHasFixedSize(true)
        rvTransaksi.layoutManager = LinearLayoutManager(activity)
        val rvAdapter = OrderAdapter(data)
        rvTransaksi.adapter = rvAdapter

        rvAdapter.callBackInterface = object : CallBackInterface {
            override fun passResultCallback(total: String, cart: ArrayList<Cart>, branch: Branch) {
                totalPrice.value = total
                cartData.value = cart
                branchData.value = branch
            }

        }
    }

    private fun setUpSearch(view: View, allMenu: List<Menu>) {
        view.findViewById<TextInputEditText>(R.id.ed_search)
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        view.findViewById<TextInputLayout>(R.id.il_search).endIconMode =
                            TextInputLayout.END_ICON_NONE

                        setItemData(view, allMenu)
                    } else {
                        view.findViewById<TextInputLayout>(R.id.il_search).endIconMode =
                            TextInputLayout.END_ICON_CLEAR_TEXT

                        viewModel.search(api, s.toString()).observe(viewLifecycleOwner) { newList ->
                            if (newList.isNotEmpty()) {
                                setItemData(view, newList)
                            } else {
                                view.findViewById<RecyclerView>(R.id.rcv_listmenuorder).visibility = View.GONE
                                view.findViewById<TextView>(R.id.tv_no_data).visibility = View.VISIBLE
                            }
                        }
                    }
                    view.findViewById<RecyclerView>(R.id.rcv_listmenuorder).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_no_data).visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        totalPrice.value = 0.toString()
    }

    companion object {
        const val TOTAL = "TOTAL"
        const val CART_DATA = "MY CART"
    }
}