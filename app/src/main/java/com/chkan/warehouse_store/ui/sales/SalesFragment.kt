package com.chkan.warehouse_store.ui.sales

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.databinding.FragmentSalesBinding
import com.chkan.warehouse_store.models.Product
import com.chkan.warehouse_store.utils.Constans
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SalesFragment : Fragment() {

    private lateinit var binding: FragmentSalesBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales, container, false)
      /*  binding.button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(Constans.TAG, "SalesFragment -> onClick ()")
                database = Firebase.database.reference
                for (x in 0..98) {
                    database.child("products").child(x.toString()).child("id").setValue(x)
                }

            }

        })*/

        return binding.root
    }
}
