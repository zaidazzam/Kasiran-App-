package com.bdi.kasiran.ui.laporan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdi.kasiran.R
import com.bdi.kasiran.adapter.LaporanDetailAdapter
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.login.User
import com.bdi.kasiran.response.order.Order
import com.bdi.kasiran.response.order.OrderCompleteResponse
import com.bdi.kasiran.ui.auth.LoginActivity
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LaporanDetailFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }  // Make sure this instance correctly provides your ApiService
    private var loggedInUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_laporan_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Mendapatkan informasi pengguna yang login dari shared preferences
        val sharedPrefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val userJson = sharedPrefs.getString("loggedInUser", null)
        loggedInUser = Gson().fromJson(userJson, User::class.java)
        val args = arguments
        var order: Order? = null

        if (args != null) {
            order = args.getParcelable("transaksi")
            if (order != null) {
                displayOrderDetails(view, order)
            }
        }

        setupButtons(view, order)

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_detail_Laporan)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (order != null) {
            recyclerView.adapter = LaporanDetailAdapter(order.order_list)
        }

    }

    private fun displayOrderDetails(view: View, order: Order) {
        view.findViewById<TextView>(R.id.txt_invoice).text = order.order_no
        view.findViewById<TextView>(R.id.txt_metode_pembayaran).text = order.payment_type
        view.findViewById<TextView>(R.id.status_order).text = order.status
        view.findViewById<TextView>(R.id.hasil_tgl_order).text = order.updated_at
        view.findViewById<TextView>(R.id.txt_diskon).text = order.total_diskon?.toString() ?: "0"
        view.findViewById<TextView>(R.id.hasil_total_order).text = order.total_transaksi.toString()
        view.findViewById<TextView>(R.id.hasil_order_note).text = order.order_note
    }

    private fun setupButtons(view: View, order: Order?) {
        view.findViewById<Button>(R.id.btn_complete).apply {
            visibility = if (order?.status != "pending") View.GONE else View.VISIBLE
            setOnClickListener {
                order?.order_uuid?.let { orderId ->
                    showLoadingIndicator()
                    completeOrder(orderId)
                }
            }
        }
        view.findViewById<Button>(R.id.btn_cancel).apply {
            visibility = if (order?.status != "pending") View.GONE else View.VISIBLE
            setOnClickListener {
                order?.order_uuid?.let { orderId ->
                    showLoadingIndicator()
                    cancelOrder(orderId)
                }
            }
        }
        view.findViewById<Button>(R.id.btn_download).apply {
            order?.let {
                setOnClickListener {
                    showLoadingIndicator()
                    printData(order)
                }
            }
        }
    }

    private fun completeOrder(orderId: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        token?.let {
            val authToken = "Bearer $it"
            api.getCompleteOrder(authToken, orderId)
                .enqueue(object : Callback<OrderCompleteResponse> {
                    override fun onResponse(
                        call: Call<OrderCompleteResponse>,
                        response: Response<OrderCompleteResponse>
                    ) {
                        hideLoadingIndicator()
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Order completed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.laporanFragment)  // Assuming you want to navigate away
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to complete the order",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<OrderCompleteResponse>, t: Throwable) {
                        hideLoadingIndicator()
                        Log.e("ERROR", "Network error or API failure", t)
                        Toast.makeText(
                            requireContext(),
                            "Network error or API failure: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } ?: run {
            hideLoadingIndicator()
            Toast.makeText(
                requireContext(),
                "Authentication token is not available",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun cancelOrder(orderId: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        token?.let {
            val authToken = "Bearer $it"
            api.cancelOrder(authToken, orderId).enqueue(object : Callback<OrderCompleteResponse> {
                override fun onResponse(
                    call: Call<OrderCompleteResponse>,
                    response: Response<OrderCompleteResponse>
                ) {
                    hideLoadingIndicator()
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Order cancelled!", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.laporanFragment)  // Assuming you want to navigate away
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to cancel the order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderCompleteResponse>, t: Throwable) {
                    hideLoadingIndicator()
                    Log.e("ERROR", "Network error or API failure", t)
                    Toast.makeText(
                        requireContext(),
                        "Network error or API failure: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } ?: run {
            hideLoadingIndicator()
            Toast.makeText(
                requireContext(),
                "Authentication token is not available",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun printData(data: Order) {
        // Menampilkan indikator loading sebelum memulai operasi cetak
        showLoadingIndicator()
        fun formatToRupiah(number: Int): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(number)
        }

        var listOrder = ""
        for (i in 0 until data.order_list.size) {
            val total = data.order_list[i].menu_qty.toInt() * data.order_list[i].menu_price
            val formattedTotal = formatToRupiah(total) // Memformat total menjadi rupiah
            listOrder += "[L]<b>${data.order_list[i].menu_name}</b>\n" +
                    "[L]${data.order_list[i].menu_price} x${data.order_list[i].menu_qty} pcs[R]" +
                    formattedTotal + "\n"
        }

        try {
            val connections: BluetoothConnection? = BluetoothPrintersConnections.selectFirstPaired()
            if (connections != null) {
                // Initialize deviceConnection with connections
                val deviceConnection = connections

                val printer = EscPosPrinter(
                    deviceConnection,
                    203,
                    48f,
                    32,
                    EscPosCharsetEncoding("windows-1252", 16)
                )
                val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                val formattedDate = dateFormatter.format(Date())
                // Pastikan pengguna yang login sudah tersedia

                val text =
                    "[c]<b>RESTORAN PUTRA PAGARUYUNG</b>\n" +
                            "[c]Jl.Maluk Sekongkang, Kec. Maluk\n" +
                            "[c]Nusa Tenggara Barat\n" +
                            "[C]--------------------------------\n" +
                            "[L]kasir  : - \n" +
                            "[L]Waktu  : $formattedDate\n" +
                            "[C]--------------------------------\n" +
                            listOrder +
                            "[C]--------------------------------\n" +
                            "[L]<b>Tipe Pembayaran</b>   : ${data.payment_type}\n" +
                            "[L]<b>Total Diskon</b>      : ${formatToRupiah(data.total_diskon ?: 0)}\n" +
                            "[L]<b>Total Pembayaran</b>  : ${formatToRupiah(data.total_transaksi)}\n" +
                            "[C]--------------------------------\n" +
                            "[C]Terima Kasih Sudah Berbelanja\n"
                printer.printFormattedText(text)
                connections.disconnect()
            } else {
                val msg = "Tidak ada printer Bluetooth yang terhubung."
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            println("Message BDI POS :")
            e.printStackTrace()
            val msg = "Perangkat tidak terhubung dengan thermal printer, " +
                    "silahkan hubungkan melalui bluetooth"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        } finally {
            // Menyembunyikan indikator loading setelah operasi cetak selesai, baik berhasil atau gagal
            hideLoadingIndicator()
        }
    }

    private fun showLoadingIndicator() {
        view?.findViewById<CardView>(R.id.loading_indicator)?.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        view?.findViewById<CardView>(R.id.loading_indicator)?.visibility = View.GONE
    }

}

