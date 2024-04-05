package com.bdi.kasiran


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.DiskonAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.diskon.DiskonResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiskonFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_diskon, container, false)
        recyclerView = view.findViewById(R.id.rcv_listDiskon)
        recyclerView.layoutManager = LinearLayoutManager(context)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        getDiskon()
        return view
    }

    private fun getDiskon() {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        api.getDiskon(token.toString()).enqueue(object : Callback<DiskonResponse> {
            override fun onResponse(call: Call<DiskonResponse>, response: Response<DiskonResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    handleDiskonDataResponse(response.body())
                } else {
                    Log.e("Error", "Failed to get diskon data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DiskonResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("Error", "Failed to get diskon data", t)
            }
        })
    }

    private fun handleDiskonDataResponse(diskonResponse: DiskonResponse?) {
        if (diskonResponse != null && diskonResponse.success) {
            val diskonList = diskonResponse.data
            val rvAdapter = DiskonAdapter(diskonList)

            recyclerView.adapter = rvAdapter
        } else {
            Log.e("Error", "Failed to get diskon data.")
        }
    }
}
