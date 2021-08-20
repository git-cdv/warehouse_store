package com.chkan.warehouse_store.ui.warehouse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.adapters.ProductListListener
import com.chkan.warehouse_store.adapters.ProductsAdapter
import com.chkan.warehouse_store.databinding.FragmentWhBinding
import com.chkan.warehouse_store.ui.BaseFragment
import com.chkan.warehouse_store.utils.Constans
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Раздувает макет с помощью привязки данных, устанавливает его владельца жизненного цикла на WarehouseFragment,
 * чтобы включить привязку данных для наблюдения за LiveData, и настройки RecyclerView с адаптером.
 */

class WarehouseFragment : BaseFragment() {

    private val viewModel: WarehouseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentWhBinding.inflate(inflater)
        //назначаем ресайклеру адаптер и слушатель кликов с обработкой в viewModel
        binding.rvWarehouse.adapter = ProductsAdapter(ProductListListener { productId ->
            viewModel.onProductClicked(productId) } )
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
            .setTitle(getString(R.string.question))
            .setCancelable(true)
            .setNegativeButton(getString(R.string.returned)) { _, _ ->
                viewModel.onReturn()
            }
            .setPositiveButton(getString(R.string.sold)) { _, _ ->
                viewModel.onSold()
            }
            .show()
    }

}