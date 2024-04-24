package com.bdi.kasiran

import ApiEndpoint
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.OrderAdapter
import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel : ViewModel() {

    val totalPrice = MutableLiveData<String>()

    fun getTransaksi(
        api: ApiEndpoint,
        view: View,
        activity: FragmentActivity?
    ): LiveData<ArrayList<Cart>> {
        val data = MutableLiveData<ArrayList<Cart>>()
        val token = LoginActivity.sessionManager.getString("TOKEN")

        api.getMenuData(token.toString()).enqueue(object : Callback<MenuResponse> {
            override fun onResponse(
                call: Call<MenuResponse>,
                response: Response<MenuResponse>
            ) {
                Log.d("TransaksiData", response.body().toString())

                val rvTransaksi = view.findViewById<RecyclerView>(R.id.rcv_listmenuorder)
                rvTransaksi.setHasFixedSize(true)
                rvTransaksi.layoutManager = LinearLayoutManager(activity)
                val rvAdapter = OrderAdapter(response.body()!!.data)
                rvTransaksi.adapter = rvAdapter

                rvAdapter.callBackInterface = object : CallBackInterface {
                    override fun passResultCallback(total: String, cart: ArrayList<Cart>) {
                        totalPrice.value = total
                        data.value = cart
                        Log.d("myCart", cart.toString())
                    }

                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                Log.e("Error", t.toString())
            }

        })

        return data
    }
}