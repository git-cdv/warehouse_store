package com.chkan.warehouse_store.ui.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.databinding.FragmentWhBinding

class WarehouseFragment : Fragment() {

    private lateinit var binding: FragmentWhBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wh, container, false)

        return binding.root
    }

}