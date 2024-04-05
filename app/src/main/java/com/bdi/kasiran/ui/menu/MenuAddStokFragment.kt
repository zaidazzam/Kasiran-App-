package com.bdi.kasiran.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bdi.kasiran.R
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.response.menu.MenuResponsePost
import com.bdi.kasiran.ui.auth.LoginActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuAddStokFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }
    private val args: MenuAddStokFragmentArgs by navArgs()

    private var menuUuid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu_add_stok, container, false)
        menuUuid = args.menuUuid

        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
        val btnBatal = view.findViewById<Button>(R.id.btn_batal)
        val edtStok = view.findViewById<EditText>(R.id.edt_stok)

        setupListeners(btnSimpan, btnBatal, edtStok)
        fetchDataMenu(menuUuid)

        return view
    }

    private fun setupListeners(btnSimpan: Button, btnBatal: Button, edtStok: EditText) {
        btnBatal.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSimpan.setOnClickListener {
            val stokBaru = edtStok.text.toString().trim()
            if (stokBaru.isNotEmpty()) {
                updateStok(stokBaru)
            } else {
                Toast.makeText(requireContext(), "Harap isi jumlah stok", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDataMenu(uuid: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")

        api.getMenuData("Bearer $token").enqueue(object : Callback<MenuResponse> {
            override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.find { it.menu_uuid == uuid }?.let { menu ->
                        view?.findViewById<TextView>(R.id.stok_saat_ini)?.text = "Stok Saat Ini: ${menu.menu_qty}"
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil data menu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                Log.e("MenuAddStokFragment", "Error fetching menu data: ${t.message}")
            }
        })
    }


    private fun updateStok(stokMenu: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        token?.let { authToken ->
            val stokMenuRequestBody = stokMenu.toRequestBody("text/plain".toMediaType())
            api.updateMenuStock(
                "Bearer $authToken",
                menuUuid,
                stokMenuRequestBody
            ).enqueue(object : Callback<MenuResponsePost> {
                override fun onResponse(
                    call: Call<MenuResponsePost>,
                    response: Response<MenuResponsePost>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Edit stok berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.menuFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Tipe daily_stok tidak bisa menambah stok.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MenuResponsePost>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                }
            })
        }
    }
}
