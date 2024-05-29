package com.bdi.kasiran.ui.menu

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bdi.kasiran.R
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.response.menu.MenuResponsePost
import com.bdi.kasiran.ui.auth.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLConnection

class MenuEditFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var etGambarMenu: ImageView
    private var imageFile: File? = null
    private val args: MenuEditFragmentArgs by navArgs()

    // Menambahkan variabel untuk menyimpan menu_uuid yang akan diupdate
    private var menuUuid: String = ""
    private lateinit var loadingIndicator: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu_edit, container, false)
        menuUuid = args.menuUuid

        loadingIndicator = view.findViewById(R.id.loading_indicator)
        // Initialize views
        val btnEdit = view.findViewById<Button>(R.id.btn_simpan)
        val btnBatal = view.findViewById<Button>(R.id.btn_batal)
        val etNamaMenu = view.findViewById<EditText>(R.id.edt_nama)
        val etHargaMenu = view.findViewById<EditText>(R.id.edt_harga)
        val etStokMenu = view.findViewById<EditText>(R.id.edt_stok)
        val spinnerTypeMenu = view.findViewById<Spinner>(R.id.spinner_type)
        val etDescMenu = view.findViewById<EditText>(R.id.edt_decs)
        etGambarMenu = view.findViewById<ImageView>(R.id.img_menu)

        fetchDataMenu(menuUuid)
        setupSpinner(spinnerTypeMenu)
        setupListeners(
            btnEdit,
            btnBatal,
            etNamaMenu,
            etHargaMenu,
            etStokMenu,
            spinnerTypeMenu,
            etDescMenu
        )

        // Ambil menu_uuid dari bundle atau argumen jika fragment ini dibuka untuk edit
        return view
    }

    private fun setupSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipe_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupListeners(
        btnEdit: Button,
        btnBatal: Button,
        etNamaMenu: EditText,
        etHargaMenu: EditText,
        etStokMenu: EditText,
        spinnerTypeMenu: Spinner,
        etDescMenu: EditText
    ) {
        btnBatal.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnEdit.setOnClickListener {
            val namaMenu = etNamaMenu.text.toString().trim()
            val hargaMenu = etHargaMenu.text.toString().trim()
            val stokMenu = etStokMenu.text.toString().trim()
            val typeMenu = spinnerTypeMenu.selectedItem.toString()
            val descMenu = etDescMenu.text.toString().trim()

            Log.d("MenuEdit", "$namaMenu - $hargaMenu - $stokMenu - $typeMenu - $descMenu")

            if (validateInput(namaMenu, hargaMenu, stokMenu, typeMenu, descMenu)) {
                showConfirmationDialog(namaMenu, hargaMenu, stokMenu, typeMenu, descMenu)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Harap isi semua kolom dan pilih gambar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        etGambarMenu.setOnClickListener {
            selectImage()
        }
    }

    private fun showConfirmationDialog(
        namaMenu: String,
        hargaMenu: String,
        stokMenu: String,
        typeMenu: String,
        descMenu: String
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin ingin ubah data menu?")
            .setPositiveButton("Ya") { dialog, _ ->
                editMenu(namaMenu, hargaMenu, stokMenu, typeMenu, descMenu)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun validateInput(
        namaMenu: String,
        hargaMenu: String,
        stokMenu: String,
        typeMenu: String,
        descMenu: String
    ): Boolean {
        return namaMenu.isNotEmpty() && hargaMenu.isNotEmpty() && stokMenu.isNotEmpty() &&
                typeMenu.isNotEmpty() && descMenu.isNotEmpty()
    }

    private fun editMenu(
        namaMenu: String,
        hargaMenu: String,
        stokMenu: String,
        typeMenu: String,
        descMenu: String
    ) {
        val token = LoginActivity.sessionManager.getString("TOKEN")

        token?.let { authToken ->
            val namaMenuRequestBody = namaMenu.toRequestBody("text/plain".toMediaType())
            val hargaMenuRequestBody = hargaMenu.toInt()
            val stokMenuRequestBody = stokMenu.toRequestBody("text/plain".toMediaType())
            val typeMenuRequestBody = typeMenu.toRequestBody("text/plain".toMediaType())
            val descMenuRequestBody = descMenu.toRequestBody("text/plain".toMediaType())

            // Menampilkan indikator loading sebelum mengirim permintaan
            loadingIndicator.visibility = View.VISIBLE

            api.editMenu(
                "Bearer $authToken",
                menuUuid,
                namaMenuRequestBody,
                hargaMenuRequestBody,
                stokMenuRequestBody,
                typeMenuRequestBody,
                descMenuRequestBody,
                null
            ).enqueue(object : Callback<MenuResponsePost> {
                override fun onResponse(
                    call: Call<MenuResponsePost>,
                    response: Response<MenuResponsePost>
                ) {
                    // Sembunyikan indikator loading setelah mendapatkan respons
                    loadingIndicator.visibility = View.GONE

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Edit menu berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.menuFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengedit menu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MenuResponsePost>, t: Throwable) {
                    // Sembunyikan indikator loading ketika terjadi kegagalan
                    loadingIndicator.visibility = View.GONE

                    Log.e("ERROR", t.toString())
                }
            })
        }
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data ?: return
            val filePath = getRealPathFromURI(selectedImageUri)
            if (filePath.isNotEmpty()) {
                imageFile = File(filePath)
                Glide.with(requireContext()).load(selectedImageUri)
                    .apply(RequestOptions().centerCrop()).into(etGambarMenu)
            } else {
                Toast.makeText(requireContext(), "Error in getting image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return if (cursor == null) {
            uri.path!!
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val path = cursor.getString(index)
            cursor.close()
            path
        }
    }

    private fun isValidImageType(file: File): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(file.name)
        return mimeType?.startsWith("image/") ?: false
    }

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 100
    }

    private fun fetchDataMenu(uuid: String) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        loadingIndicator.visibility = View.VISIBLE

        api.getMenuData("Bearer $token").enqueue(object : Callback<MenuResponse> {
            override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.find { it.menu_uuid == uuid }?.let { menu ->
                        val price = menu.menu_price.toDouble()
                        view?.findViewById<EditText>(R.id.edt_nama)?.setText(menu.menu_name)
                        view?.findViewById<EditText>(R.id.edt_harga)?.setText(price.toInt().toString())
                        view?.findViewById<EditText>(R.id.edt_stok)?.setText(menu.menu_qty)
                        view?.findViewById<EditText>(R.id.edt_decs)?.setText(menu.menu_desc)
                        loadingIndicator.visibility = View.GONE

                        // Set the image using Glide
                        val imageUrl = menu.menu_image
                        Glide.with(this@MenuEditFragment)
                            .load(imageUrl)
                            .into(etGambarMenu)

                        // Set the spinner value
                        val spinner = view?.findViewById<Spinner>(R.id.spinner_type)
                        ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.tipe_options,
                            android.R.layout.simple_spinner_item
                        ).also { adapter ->
                            spinner?.adapter = adapter
                            val position = adapter.getPosition(menu.menu_type)
                            spinner?.setSelection(position)
                        }
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
                loadingIndicator.visibility = View.GONE
                Log.e("Menu Edit ", "Error fetching menu data: ${t.message}")
            }
        })
    }
}

