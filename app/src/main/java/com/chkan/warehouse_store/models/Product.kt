package com.chkan.warehouse_store.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Product (
    val id: Int = 0,
    val name: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val month: Int = 0,
    val year: Int = 0
) : Parcelable
