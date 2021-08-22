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
import kotlinx.coroutines.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class SalesViewModel : ViewModel() {

    //для хранения списка товаров
    private val _sales = MutableLiveData<List<Product>>()
    val sales: LiveData<List<Product>> = _sales
    private val database: DatabaseReference = Firebase.database.reference
    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId
    private lateinit var month : String

    init {
        getSales()
    }

    private fun getSales() {
        //TODO запускаем статус бар лоадера
        //вытягиваем текущий месяц
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("MMMMyy")
        month = fmt.print(LocalDate.now())// формате августа21
        //делаем выборку по текущему месяцу
        val monthQuery = database.child("sales").child(month)

        //создаем слушателя изменений в БД
        monthQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "SalesViewModel -> ValueEventListener")
                val list: MutableList<Product> = mutableListOf()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    var sale = data.getValue(Product::class.java)
                    list.add(sale as Product)
                }
                //сортируем по названию сумок и фильтрует по остаткам
                _sales.value = list.sortedBy { it.name }.filter { it.quantity != 0 }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(Constans.TAG, "SalesViewModel -> onCancelled")
                // ...
            }
        })
    }

    fun onSalesClicked(id: Int) {
        _clickedId.value = id
    }

    fun onReturn() {
        Log.d(Constans.TAG, "SalesViewModel -> onReturn")
        //создаем неподвязанный скоуп чтобы корутина доработала и после закрытия фрагмента
        //выбираем Dispatchers.IO потому что запросы в сеть
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            // добавляем в остатках
            //пытаюсь получить значение остатка по этому товару
            database.child(Constans.KEY_DB_PRODUCTS).child(clickedId.value.toString()).child("quantity").get()
                .addOnSuccessListener {
                    Log.d(Constans.TAG, "Got value ${it.value}")

                    val value: Int = Integer.valueOf(it.value.toString())
                    database.child(Constans.KEY_DB_PRODUCTS).child(clickedId.value.toString())
                        .child("quantity").setValue(value + 1)

                }.addOnFailureListener {
                    Log.d(Constans.TAG, "Error getting data", it)
                }
            //убираем с продаж
            //получаем текущее значение продаж
            val quantity = _sales.value!!.find { it.id==_clickedId.value }!!.quantity
            //где month - это выбранная текущая выборка месяца с БД
            database.child(Constans.KEY_DB_SALES).child(month).child(clickedId.value.toString()).child("quantity").setValue(quantity-1)
        }


    }
}

