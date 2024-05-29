package com.bdi.kasiran

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdi.kasiran.adapter.MenuAdapter
import com.bdi.kasiran.databinding.FragmentMenuBinding
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.menu.Menu
import com.google.android.material.textfield.TextInputLayout

class MenuFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var binding: FragmentMenuBinding
    private lateinit var loadingIndicator: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        viewModel.getMenuData(api).observe(viewLifecycleOwner) { data ->
            setItemData(data)
            setUpSearch(data)
        }

        binding.btnTambah.setOnClickListener {
            Toast.makeText(requireContext(), "Tambah Menu", Toast.LENGTH_LONG)
                .show() // Fixed toast display
            findNavController().navigate(R.id.menuAddFragment)
        }
    }

    private fun setItemData(data: List<Menu>) {
        val rvTransaksi = binding.rcvListmenu
        rvTransaksi.setHasFixedSize(true)
        rvTransaksi.layoutManager = LinearLayoutManager(activity)
        val rvAdapter = MenuAdapter(data, object : MenuAdapter.OnItemClickListener {
            override fun onDelete(item: Menu, position: Int) {
                viewModel.deleteMenu(api, item.menu_uuid).observe(viewLifecycleOwner) {
                    if (it.success) {
                        loadingIndicator.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Delete ${item.menu_name} Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.menuFragment)
                    }
                }
            }

        })
        rvTransaksi.adapter = rvAdapter
    }

    private fun setUpSearch(allMenu: List<Menu>) {
        binding.edSearchMenu
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
                        binding.ilSearchMenu.endIconMode = TextInputLayout.END_ICON_NONE
                        setItemData(allMenu)
                    } else {
                        binding.ilSearchMenu.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                        viewModel.search(api, s.toString()).observe(viewLifecycleOwner) { newList ->
                            if (newList.isNotEmpty()) {
                                setItemData(newList)
                            } else {
                                binding.rcvListmenu.visibility = View.INVISIBLE
                                binding.tvNoData.visibility = View.VISIBLE
                            }
                        }
                    }
                    binding.rcvListmenu.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.edSearchMenu.text = null
    }
}