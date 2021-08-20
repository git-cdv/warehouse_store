package com.chkan.warehouse_store

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.chkan.warehouse_store.adapters.ProductsAdapter
import com.chkan.warehouse_store.models.Product


    //автоматически обновляет ресайклер когда изменяется ливдата в моделвью
    @BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView,
                         data: List<Product>?) {
        val adapter = recyclerView.adapter as ProductsAdapter
        adapter.submitList(data)
    }

    /**
     * Uses the Coil library to load an image by URL into an [ImageView]
     */
    @BindingAdapter("imageUrl")
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.load(imgUri) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
        }
    }

