package com.chkan.warehouse_store.ui.sales

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chkan.warehouse_store.models.Product
import com.chkan.warehouse_store.utils.Constans
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class SalesViewModel(application: Application) : AndroidViewModel(application) {

    //для хранения списка товаров
    private val _sales = MutableLiveData<List<Product>>()
    val sales: LiveData<List<Product>> = _sales
    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId
    private val db: FirebaseDatabase = Firebase.database
    private val salesRef: DatabaseReference = db.getReference(Constans.KEY_DB_SALES)
    val listMonthPrev: MutableList<Product> = mutableListOf()
    var listMonthCur: MutableList<Product> = mutableListOf()
    var listSumYearCur: MutableList<Product> = mutableListOf()

    init {
        getSales()
    }

    private fun getSales() {
        Log.d(Constans.TAG, "getSales()")
        //вытягиваем текущий месяц
        val month = LocalDate.now().monthOfYear
        //делаем выборку по текущему месяцу
        val monthQuery = salesRef.orderByChild("month").equalTo(month.toDouble())

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
                //сохраняем в общий лист для переиспользования
                listMonthCur = ArrayList(list)
                //сортируем по названию сумок и фильтрует по остаткам
                _sales.value = list.sortedBy { it.name }.filter { it.quantity != 0 }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(Constans.TAG, "SalesViewModel -> onCancelled - ${databaseError.details}")
                Toast.makeText(getApplication(), "У вас нет доступа к данным", Toast.LENGTH_LONG).show()
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

            val productRef: DatabaseReference = db.getReference(Constans.KEY_DB_PRODUCTS)
            // добавляем в остатках
            //пытаюсь получить значение остатка по этому товару
            productRef.child(clickedId.value.toString()).child("quantity").get()
                .addOnSuccessListener {
                    Log.d(Constans.TAG, "Got value ${it.value}")

                    val value: Int = Integer.valueOf(it.value.toString())
                    productRef.child(clickedId.value.toString()).child("quantity").setValue(value + 1)

                }.addOnFailureListener {
                    Log.d(Constans.TAG, "Error getting data", it)
                }
            //убираем с продаж
            //получаем текущее звено с продажами
            val item = _sales.value!!.find { it.id==_clickedId.value }
            //где month - это выбранная текущая выборка месяца с БД
            if (item != null) {
                val year = item.year
                val month = item.month
                val quantity = item.quantity-1
                salesRef.child("$year-$month-${clickedId.value}").child("quantity").setValue(quantity)
            }
        }

    }

    fun getSalesCurrentMonth() {
        Log.d(Constans.TAG, "getSalesCurrentMonth() -> listMonthCur: - ${listMonthCur.size} ")
        _sales.value = listMonthCur.sortedBy { it.name }.filter { it.quantity != 0 }
    }

    fun getSalesPreviousMonth() {

        if (listMonthPrev.size > 0) {
            _sales.value = listMonthPrev.sortedBy { it.name }
        } else {
            val mMonth = LocalDate.now().monthOfYear - 1
            val prevQuery = salesRef.orderByChild("month").equalTo(mMonth.toDouble())
            prevQuery.get().addOnSuccessListener {
                Log.d(Constans.TAG, "getSalesPreviousMonth() -> dataSnapshot: - ${it.value} ")
                for (data in it.children) {
                    var sale = data.getValue(Product::class.java)
                    listMonthPrev.add(sale as Product)
                }
                _sales.value = listMonthPrev.sortedBy { it.name }

            }.addOnFailureListener {
                Log.d(Constans.TAG, "Error getting data", it)
            }
        }
    }

    fun getSalesCurrentYear() {

        if (listSumYearCur.size > 0) {
            _sales.value = listSumYearCur.sortedBy { it.name }
        } else {

            val mYear = LocalDate.now().yearOfCentury
            val curYearQuery = salesRef.orderByChild("year").equalTo(mYear.toDouble())
            curYearQuery.get().addOnSuccessListener {
                Log.d(Constans.TAG, "getSalesCurrentYear() -> dataSnapshot: - ${it.value} ")
                val list: MutableList<Product> = mutableListOf()
                for (data in it.children) {
                    var sale = data.getValue(Product::class.java)
                    list.add(sale as Product)
                }

                //пока размер списка больше нуля перебираем его
                while(list.size>0){
                    //берем первый id и фильтруем по нем
                    val filtered = list.filter { it.id==list[0].id }
                    //если элемент 1 сразу добавляем его в listSum и удаляем из list
                    if (filtered.size==1){
                        listSumYearCur.add(filtered[0])
                        list.remove(filtered[0])
                    } else {
                        var count = 0
                        //если элементов несколько - перебираем и плюсуем их quantity в обьект-бланк
                        for (product in filtered) {
                            count += product.quantity
                        }
                        val blankProduct = filtered[0]
                        blankProduct.quantity=count
                        //добавляем clearProduct в listSum и удаляем filtered из list
                        listSumYearCur.add(blankProduct)
                        list.removeAll(filtered)
                    }

                }

                _sales.value = listSumYearCur.sortedBy { it.name }.filter { it.quantity != 0 }

            }.addOnFailureListener {
                Log.d(Constans.TAG, "Error getting data", it)
            }
        }
    }
}

