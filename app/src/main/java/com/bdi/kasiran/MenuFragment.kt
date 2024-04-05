package com.bdi.kasiran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.adapter.MenuAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.ui.auth.LoginActivity.Companion.sessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var recyclerView: RecyclerView  // Tambahkan ini

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerView = view.findViewById(R.id.rcv_listmenu) // Initialize recyclerView here
        recyclerView.layoutManager = LinearLayoutManager(context)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        getMenuData()
        val btnTambah = view.findViewById<FloatingActionButton>(R.id.btn_tambah)
        btnTambah.setOnClickListener {
            Toast.makeText(requireContext(), "Tambah Menu", Toast.LENGTH_LONG).show() // Fixed toast display
            findNavController().navigate(R.id.menuAddFragment)
        }

        return view
    }

    private fun getMenuData() {
        val token = sessionManager.getString("TOKEN")

        api.getMenuData(token.toString()).enqueue(object : Callback<MenuResponse> {
            override fun onResponse(
                call: Call<MenuResponse>,
                response: Response<MenuResponse>
            ) {
                if (isAdded) { // Check if Fragment is still added to its context
                    if (response.isSuccessful) {
                        handleMenuDataResponse(response.body())
                    } else {
                        Log.e("Error", "Failed to get menu data. Code: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                if (isAdded) { // Check if Fragment is still added to its context
                    Log.e("Error", "Failed to get menu data", t)
                }
            }
        })
    }


    private fun handleMenuDataResponse(menuResponse: MenuResponse?) {
        if (menuResponse != null && menuResponse.success) {
            val menuList = menuResponse.data
            val rvAdapter = MenuAdapter(menuList)

            // Set adapter to RecyclerView
            recyclerView.adapter = rvAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager

        } else {
            // Handle the case when the response is not successful or data is null
            Log.e("Error", "Failed to get menu data.")
        }
    }
}