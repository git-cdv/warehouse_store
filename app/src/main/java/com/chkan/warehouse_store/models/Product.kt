package com.chkan.warehouse_store.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val name: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val quantity: String = ""
) : Parcelable
