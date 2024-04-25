package com.bdi.kasiran

import ApiEndpoint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdi.kasiran.response.menu.Menu
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel : ViewModel() {

    fun getMenuData(api: ApiEndpoint): LiveData<List<Menu>> {
        val data = MutableLiveData<List<Menu>>()
        val token = LoginActivity.sessionManager.getString("TOKEN")

        api.getMenuData(token.toString()).enqueue(object : Callback<MenuResponse> {
            override fun onResponse(
                call: Call<MenuResponse>,
                response: Response<MenuResponse>
            ) {
                Log.d("TransaksiData", response.body().toString())

                if (response.isSuccessful) {
                    data.value = response.body()?.data
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                Log.e("Error", t.toString())
            }

        })

        return data
    }

    fun search(api: ApiEndpoint, key: String): LiveData<List<Menu>> {
        val data = MutableLiveData<List<Menu>>()
        val token = LoginActivity.sessionManager.getString("TOKEN")
        api.searchMenu(token.toString(), key).enqueue(object : Callback<MenuResponse> {
            override fun onResponse(
                call: Call<MenuResponse>,
                response: Response<MenuResponse>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()?.data
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                Log.e("Error", t.toString())
            }

        })
        return data
    }
}