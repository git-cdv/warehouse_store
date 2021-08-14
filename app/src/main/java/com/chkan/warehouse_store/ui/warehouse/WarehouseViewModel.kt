package com.chkan.warehouse_store.ui.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chkan.warehouse_store.models.Product

/**
 * ViewModel отвечает за выполнение сетевого вызова для получения данных.
 * В ViewModel, вы используете LiveData привязку данных с учетом жизненного цикла
 * для обновления интерфейса при изменении данных.
 */

class WarehouseViewModel : ViewModel() {

    //для хранения списка товаров
    private val _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> = _products

    init {
        getProducts()
    }

    private fun getProducts() {
        //TODO запускаем статус бар лоадера

        val productsList = listOf<Product>(Product("Prestige Серая, 22 л", "https://mommys.com.ua/image/cache/catalog/Prestige/24-100x100.jpg", "Сумки","4"),
            Product("Cube Геометрия, 13 л", "https://mommys.com.ua/image/cache/catalog/CubeSum/sumka_dlya_mam_cuberomb_000-100x100.jpg", "Bags","2"),
            Product("Ahong Лисички, 22 л", "https://mommys.com.ua/image/cache/catalog/Fun/9135644580_251991099-100x100.jpg", "Bags","0"))
        _products.value = productsList

        //TODO здесь получаем список Товаров через FirestoreClass и применяем его ливдате через трай кетч
    }
}