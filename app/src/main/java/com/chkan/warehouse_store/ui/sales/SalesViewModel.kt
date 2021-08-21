package com.chkan.warehouse_store.ui.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chkan.warehouse_store.models.Product
import com.chkan.warehouse_store.utils.Constans
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class SalesViewModel : ViewModel() {

    //для хранения списка товаров
    private val _sales = MutableLiveData<List<Product>>()
    val sales : LiveData<List<Product>> = _sales
    private val database: DatabaseReference = Firebase.database.reference
    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId

    init {
        getSales()
    }

    private fun getSales() {
        //TODO запускаем статус бар лоадера
        //вытягиваем текущий месяц
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("MMMMyy")
        val month = fmt.print(LocalDate.now())// формате августа21
        //делаем выборку по текущему месяцу
        val monthQuery = database.child("sales").child(month)

        //создаем слушателя изменений в БД
        monthQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "SalesViewModel -> ValueEventListener")
                val list : MutableList<Product> = mutableListOf()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    var sale = data.getValue(Product::class.java)
                    list.add(sale as Product)
                }
                //сортируем по названию сумок
                list.sortedBy { it.name }
                _sales.value = list
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(Constans.TAG, "SalesViewModel -> onCancelled")
                // ...
            }
        })
    }

    fun onSalesClicked(id:Int){
        _clickedId.value = id
    }

    fun onReturn() {
        Log.d(Constans.TAG, "SalesViewModel -> onReturn")
    }

}