package com.chkan.warehouse_store.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.adapters.ProductListListener
import com.chkan.warehouse_store.adapters.ProductsAdapter
import com.chkan.warehouse_store.databinding.FragmentSalesBinding
import com.chkan.warehouse_store.ui.warehouse.WarehouseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SalesFragment : Fragment() {

    private lateinit var binding: FragmentSalesBinding
    private val viewModel: SalesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales, container, false)

        //назначаем ресайклеру адаптер и слушатель кликов с обработкой в viewModel
        binding.rvSales.adapter = ProductsAdapter(ProductListListener { productId ->
            viewModel.onSalesClicked(productId) } )
        // Позволяет привязке данных наблюдать за LiveData в течение жизненного цикла этого фрагмента
        binding.lifecycleOwner = this

        // Предоставление привязки доступа к WarehouseViewModel (xml олжна быть эта переменная)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.clickedId.observe(viewLifecycleOwner,
            { id -> showDialog()
            })
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sale_return))
            .setCancelable(true)
            .setNegativeButton(getString(R.string.sale_cancel)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.onReturn()
            }
            .show()
    }
}
