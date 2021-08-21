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
    val products : LiveData<List<Product>> = _products
    private val db: FirebaseDatabase = Firebase.database
    private val database: DatabaseReference = db.getReference(Constans.KEY_DB_PRODUCTS)
    private val database_sales: DatabaseReference = db.getReference(Constans.KEY_DB_SALES)
    //для передачи состояния
    private val _clickedId = MutableLiveData<Int>()
    val clickedId: LiveData<Int> = _clickedId
    var sorted: List<Product>?=null

    init {
        getProducts()
    }

    private fun getProducts() {
        //TODO запускаем статус бар лоадера
        //создаем слушателя изменений в БД
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constans.TAG, "WarehouseViewModel -> ValueEventListener")
                val list : MutableList<Product> = mutableListOf()
                // Здесь получаем список "детей" и проходимся по ним в цикле
                for (data in dataSnapshot.children) {
                    var product = data.getValue(Product::class.java)
                    list.add(product as Product)
                }
                //отфильтровываем с 0 остатком и сортируем по названию суммок
                sorted = list.filter { it.quantity!=0 }.sortedBy { it.name }
                _products.value = sorted
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(Constans.TAG, "WarehouseViewModel -> onCancelled")
                // ...
            }
        })
    }

    fun onProductClicked(id:Int){
        _clickedId.value = id
    }

    fun onReturn(){
        if(sorted!=null){
            //находим и увеличиваем остаток товара в БД
            val value = sorted!!.filter { it.id==_clickedId.value }.get(0).quantity
            database.child(_clickedId.value.toString()).child("quantity").setValue(value+1)
        }
    }

    fun onSold(){
        val clickedId = _clickedId.value
        val product = sorted!!.filter { it.id==clickedId }.get(0)
        if(sorted!=null){
        //находим и уменьшаем остаток товара в БД
        database.child(clickedId.toString()).child("quantity").setValue(product.quantity-1)
        }
        //записываем продажу в БД
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("MMMMyy")
        val month = fmt.print(LocalDate.now())// формате августа21

        val sale =
            clickedId?.let { Product(it,product.name,product.imageUrl,product.category,1,month) }

        database_sales.child(month).child(clickedId.toString()).setValue(sale)

    }
}