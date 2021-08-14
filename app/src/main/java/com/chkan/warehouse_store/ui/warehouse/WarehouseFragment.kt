package com.chkan.warehouse_store.ui.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.adapters.ProductsAdapter
import com.chkan.warehouse_store.databinding.FragmentWhBinding

/**
 * Раздувает макет с помощью привязки данных, устанавливает его владельца жизненного цикла на WarehouseFragment,
 * чтобы включить привязку данных для наблюдения за LiveData, и настройки RecyclerView с адаптером.
 */

class WarehouseFragment : Fragment() {

    //private lateinit var binding: FragmentWhBinding
    private val viewModel: WarehouseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wh, container, false)
        val binding = FragmentWhBinding.inflate(inflater)
        //назначаем ресайклеру адаптер
        binding.rvWarehouse.adapter = ProductsAdapter()
        // Позволяет привязке данных наблюдать за LiveData в течение жизненного цикла этого фрагмента
        binding.lifecycleOwner = this

        // Предоставление привязки доступа к WarehouseViewModel (xml олжна быть эта переменная)
        binding.viewModel = viewModel

        return binding.root
    }

}