    package com.bdi.kasiran

    import android.app.Activity
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
    import com.bdi.kasiran.network.BaseRetrofit
    import com.bdi.kasiran.response.menu.MenuResponsePost
    import com.bdi.kasiran.ui.auth.LoginActivity
    import com.bumptech.glide.Glide
    import com.bumptech.glide.request.RequestOptions
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import okhttp3.MultipartBody
    import okhttp3.RequestBody
    import okhttp3.RequestBody.Companion.asRequestBody
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import java.io.File
    import java.util.Locale

    class MenuAddFragment : Fragment() {

        private val api by lazy { BaseRetrofit().endpoint }
        private lateinit var selectedImageUri: Uri
        private lateinit var etGambarMenu: ImageView
        private var imageFile: File? = null
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_menu_add, container, false)

            val btnCreate = view.findViewById<Button>(R.id.btn_tambah)
            val etNamaMenu = view.findViewById<EditText>(R.id.edt_nama)
            val etHargaMenu = view.findViewById<EditText>(R.id.edt_harga)
            val etStokMenu = view.findViewById<EditText>(R.id.edt_stok)
            val spinnerTypeMenu = view.findViewById<Spinner>(R.id.spinner_type) // Tambahkan ini
            val etDescMenu = view.findViewById<EditText>(R.id.edt_decs)
            etGambarMenu = view.findViewById<ImageView>(R.id.img_menu)

            btnCreate.setOnClickListener {
                val namaMenu = etNamaMenu.text.toString().trim()
                val hargaMenu = etHargaMenu.text.toString().trim()
                val stokMenu = etStokMenu.text.toString().trim()
                val typeMenu = spinnerTypeMenu.selectedItem.toString()
                val descMenu = etDescMenu.text.toString().trim()

                if (namaMenu.isNotEmpty() && hargaMenu.isNotEmpty() && stokMenu.isNotEmpty() &&
                    typeMenu.isNotEmpty() && descMenu.isNotEmpty() && imageFile != null
                ) {
                    tambahMenu(namaMenu, hargaMenu, stokMenu, typeMenu, descMenu)
                } else {
                    Toast.makeText(requireContext(), "Harap isi semua kolom dan pilih gambar", Toast.LENGTH_SHORT).show()
                }
            }

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.tipe_options, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTypeMenu.adapter = adapter
            }

            etGambarMenu.setOnClickListener {
                selectImage()
            }

            return view
        }

        private fun tambahMenu(namaMenu: String, hargaMenu: String, stokMenu: String, typeMenu: String, descMenu: String) {
            val token = LoginActivity.sessionManager.getString("TOKEN")
            token?.let {
                if (isValidImageType(imageFile)) {
                    val imageRequestBody: RequestBody? = imageFile?.asRequestBody("file/*".toMediaTypeOrNull())
                    val imagePart: MultipartBody.Part? = imageRequestBody?.let {
                        MultipartBody.Part.createFormData("menu_image", imageFile!!.name, it)
                    }

                    // Convert other parameters to RequestBody if needed
                    val namaMenuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), namaMenu)
                    val hargaMenuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaMenu)
                    val stokMenuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), stokMenu)
                    val typeMenuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), typeMenu) // Ubah ini
                    val descMenuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), descMenu)
                    Log.d("MenuAddFragment", "typeMenu: $typeMenu")

                    if (imagePart != null) {
                        api.tambahMenu(it, namaMenuRequestBody, hargaMenuRequestBody, stokMenuRequestBody, typeMenuRequestBody, descMenuRequestBody, imagePart)
                            .enqueue(object : Callback<MenuResponsePost> {
                                override fun onResponse(call: Call<MenuResponsePost>, response: Response<MenuResponsePost>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(requireContext(), "Tambah menu berhasil", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.menuFragment)
                                    } else {
                                        Toast.makeText(requireContext(), "Gagal menambah menu", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<MenuResponsePost>, t: Throwable) {
                                    Log.e("ERROR", t.toString())
                                }
                            })
                    }
                } else {
                    Toast.makeText(requireContext(), "Pilih file gambar dengan tipe yang benar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun selectImage() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                selectedImageUri = data.data ?: return
                imageFile = File(getRealPathFromURI(selectedImageUri))

                // Validasi imageFile tidak null sebelum menggunakan Glide
                if (imageFile != null && imageFile!!.exists()) {
                    Glide.with(requireContext())
                        .load(selectedImageUri)
                        .apply(RequestOptions().centerCrop())
                        .into(etGambarMenu)
                } else {
                    Log.e("YJW", "Image file is null or doesn't exist")
                }
            }
        }
        private fun getRealPathFromURI(uri: Uri): String {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                it.moveToFirst()
                val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                val path = it.getString(index)
                it.close()
                return path ?: ""
            }
            return ""
        }
        companion object {
            private const val IMAGE_PICKER_REQUEST_CODE = 123
        }

        private fun isValidImageType(file: File?): Boolean {
            val validImageTypes = listOf("jpeg", "jpg", "png")
            return file?.extension?.toLowerCase(Locale.ROOT) in validImageTypes
        }
    }
