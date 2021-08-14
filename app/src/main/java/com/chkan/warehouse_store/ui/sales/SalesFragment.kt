package com.chkan.warehouse_store.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.databinding.FragmentSalesBinding

class SalesFragment : Fragment() {

    private lateinit var binding: FragmentSalesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales, container, false)

        return binding.root
    }
}