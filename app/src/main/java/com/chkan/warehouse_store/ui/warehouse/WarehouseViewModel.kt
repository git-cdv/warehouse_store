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

class WarehouseViewModel : ViewModel() {

    //для хранения списка товаров
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val db: FirebaseDatabase = Firebase.database
    private val database: DatabaseReference = db.getReference(Constans.KEY_DB_PRODUCTS)
    private val database_sales: DatabaseReference = db.getReference(Constans.KEY_DB_SALES)

    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId
    var sorted: List<Product>? = null

    init {
        getProducts()
    }

    private fun getProducts() {
        //TODO запускаем статус бар лоадера
        //создаем слушателя изменений в БД
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "WarehouseViewModel -> ValueEventListener")
                val list: MutableList<Product> = mutableListOf()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    var product = data.getValue(Product::class.java)
                    list.add(product as Product)
                }
                //отфильтровываем с 0 остатком и сортируем по названию суммок
                sorted = list.filter { it.quantity != 0 }.sortedBy { it.name }
                _products.value = sorted
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(Constans.TAG, "WarehouseViewModel -> onCancelled")
                // ...
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

            val clickedId = _clickedId.value
            val product = sorted!!.find { it.id == _clickedId.value }!!
            if (sorted != null) {
                //находим и уменьшаем остаток товара в БД
                database.child(clickedId.toString()).child("quantity")
                    .setValue(product.quantity - 1)
            }
            //записываем продажу в БД
            val fmt: DateTimeFormatter = DateTimeFormat.forPattern("MMMMyy")
            val month = fmt.print(LocalDate.now())// формате августа21

            //пытаюсь получить значение продаж по этому товару, если их нет - получаю null
            database_sales.child(month).child(clickedId.toString()).child("quantity").get()
                .addOnSuccessListener {
                    Log.d(Constans.TAG, "Got value ${it.value}")
                    if (it.value == null) {
                        //если значения нет - записываем обьект с 1 продажей
                        database_sales.child(month).child(clickedId.toString()).setValue(
                            Product(
                                clickedId!!,
                                product.name,
                                product.imageUrl,
                                product.category,
                                1,
                                month
                            )
                        )
                    } else {
                        val value: Int = Integer.valueOf(it.value.toString())
                        //если значение уже есть - увеличиваем его и обновляем
                        database_sales.child(month).child(clickedId.toString()).child("quantity")
                            .setValue(value + 1)
                    }

                }.addOnFailureListener {
                Log.d(Constans.TAG, "Error getting data", it)
            }

        }
    }
}