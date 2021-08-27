package com.chkan.warehouse_store.ui.warehouse

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chkan.warehouse_store.models.Product
import com.chkan.warehouse_store.utils.Constans
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


/**
 * ViewModel отвечает за выполнение сетевого вызова для получения данных.
 * В ViewModel, вы используете LiveData привязку данных с учетом жизненного цикла
 * для обновления интерфейса при изменении данных.
 */

class WarehouseViewModel(application: Application) : AndroidViewModel(application) {

    //для хранения списка товаров
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val db: FirebaseDatabase = Firebase.database
    private val productRef: DatabaseReference = db.getReference(Constans.KEY_DB_PRODUCTS)
    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId
    //полный список ассортимента
    var listFull: MutableList<Product> = mutableListOf()
    //сортированный список
    var sorted: List<Product>? = null

    init {
        loadProducts()
    }

    private fun loadProducts() {
        //создаем слушателя изменений в БД
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "WarehouseViewModel -> ValueEventListener")
                val list: MutableList<Product> = mutableListOf()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    val product = data.getValue(Product::class.java)
                    list.add(product as Product)
                }
                listFull = list
                //отфильтровываем с 0 остатком и сортируем по названию суммок
                sorted = list.filter { it.quantity != 0 }.sortedBy { it.name }
                _products.value = sorted
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(Constans.TAG, "WarehouseViewModel -> onCancelled - ${databaseError.details}")
                Toast.makeText(getApplication(), "У вас нет доступа к данным", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun onProductClicked(id: Int) {
        _clickedId.value = id
    }

    fun onSold() {

        //создаем неподвязанный скоуп чтобы корутина доработала и после закрытия фрагмента
        //выбираем Dispatchers.IO потому что запросы в сеть

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            val salesRef: DatabaseReference = db.getReference(Constans.KEY_DB_SALES)
            val clickedId = _clickedId.value
            val product = sorted!!.find { it.id == _clickedId.value }!!
            if (sorted != null) {
                //находим и уменьшаем остаток товара в БД
                productRef.child(clickedId.toString()).child("quantity")
                    .setValue(product.quantity - 1)
            }
            //записываем продажу в БД
            val year = LocalDate.now().yearOfCentury
            val month = LocalDate.now().monthOfYear// формате августа21

            //пытаюсь получить значение продаж по этому товару, если их нет - получаю null
            salesRef.child("$year-$month-$clickedId").child("quantity").get()
                .addOnSuccessListener {
                    Log.d(Constans.TAG, "Got value ${it.value}")
                    if (it.value == null) {
                        //если значения нет - записываем обьект с 1 продажей
                        salesRef.child("$year-$month-$clickedId").setValue(
                            Product(
                                clickedId!!,
                                product.name,
                                product.imageUrl,
                                product.category,
                                1,
                                month,
                                year
                            )
                        )
                    } else {
                        val value: Int = Integer.valueOf(it.value.toString())
                        //если значение уже есть - увеличиваем его и обновляем
                        salesRef.child("$year-$month-$clickedId").child("quantity")
                            .setValue(value + 1)
                    }

                }.addOnFailureListener {
                Log.d(Constans.TAG, "Error getting data", it)
            }

        }
    }

    fun getProductsOutStock() {
        //отфильтровываем с 0 остатком и сортируем по названию суммок
       _products.value = listFull.filter { it.quantity == 0 }.sortedBy { it.name }
    }

    fun getProductsAll() {
        //весь список и сортируем по названию суммок
        _products.value = listFull.sortedBy { it.name }
    }

    fun getProductsStock() {
        _products.value = sorted
    }

}