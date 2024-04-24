package com.bdi.kasiran

import ApiEndpoint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdi.kasiran.response.diskon.Diskon
import com.bdi.kasiran.response.diskon.DiskonResponse
import com.bdi.kasiran.response.order.OrderCompleteResponse
import com.bdi.kasiran.response.order.OrderStore
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmOrderViewModel : ViewModel() {

    fun getDiskon(api: ApiEndpoint): LiveData<List<Diskon>> {
        val data = MutableLiveData<List<Diskon>>()
        val token = LoginActivity.sessionManager.getString("TOKEN")
        api.getDiskon(token.toString()).enqueue(object : Callback<DiskonResponse> {
            override fun onResponse(
                call: Call<DiskonResponse>,
                response: Response<DiskonResponse>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()?.data
                } else {
                    Log.e("Error", "Failed to get diskon data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DiskonResponse>, t: Throwable) {
                Log.e("Error", "Failed to get diskon data", t)
            }
        })
        return data
    }

    fun storeOrder(api: ApiEndpoint, order: OrderStore): LiveData<OrderCompleteResponse> {
        val data = MutableLiveData<OrderCompleteResponse>()
        val token = LoginActivity.sessionManager.getString("TOKEN")
        api.storeOrder(token.toString(), order).enqueue(object : Callback<OrderCompleteResponse> {
            override fun onResponse(
                call: Call<OrderCompleteResponse>,
                response: Response<OrderCompleteResponse>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    Log.e("Error", "Failed to get diskon data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OrderCompleteResponse>, t: Throwable) {
                Log.e("Error", "Failed to get diskon data", t)
            }
        })
        return data
    }
}