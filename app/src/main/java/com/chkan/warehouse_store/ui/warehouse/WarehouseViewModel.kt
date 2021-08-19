package com.chkan.warehouse_store.ui.warehouse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chkan.warehouse_store.models.Product
import com.chkan.warehouse_store.utils.Constans
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * ViewModel отвечает за выполнение сетевого вызова для получения данных.
 * В ViewModel, вы используете LiveData привязку данных с учетом жизненного цикла
 * для обновления интерфейса при изменении данных.
 */

class WarehouseViewModel : ViewModel() {

    //для хранения списка товаров
    private val _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> = _products
    private val db: FirebaseDatabase = Firebase.database
    private val database: DatabaseReference = db.getReference(Constans.KEY_DB_PRODUCTS)

    init {
        getProducts()
    }

    private fun getProducts() {
        //TODO запускаем статус бар лоадера

        //создаем слушателя изменений в БД
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "WarehouseViewModel -> ValueEventListener")
                var list = ArrayList<Product>()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    var product = data.getValue(Product::class.java)
                    list.add(product as Product)
                }
                _products.value = list
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(Constans.TAG, "WarehouseViewModel -> onCancelled")
                // ...
            }
        })
    }
}