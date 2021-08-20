package com.chkan.warehouse_store.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chkan.warehouse_store.databinding.ListItemWhBinding
import com.chkan.warehouse_store.models.Product

/**
 * ListAdapter является подклассом RecyclerView.Adapter класса для представления данных списка в RecyclerView,
 * включая вычисление различий между списками в фоновом потоке.
 * Преимущество использования DiffUtil заключается в том, что каждый раз,
 * когда какой-либо элемент в RecyclerView добавляется, удаляется или изменяется, весь список не обновляется.
 * Обновляются только те элементы, которые были изменены.
 */

class ProductsAdapter(val clickListener:ProductListListener): ListAdapter<Product,
        ProductsAdapter.ProductViewHolder>(DiffCallback) {

    /**
     * Предоставляем ссылку на представления для каждого элемента данных
     *
     * Конструктор ProductViewHolder берет переменную привязки из связанного
     * ListItemWhBinding, который прекрасно дает доступ к полной информации [Product].
     */
    class ProductViewHolder(
        private var binding: ListItemWhBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mProduct: Product, clickListener: ProductListListener) {
            //где product название <data><variable> из list_item_wh.xml
            //т.е. какие вью мы с <variable> во list_item_wh.xml связали - такие и покажутся
            binding.product = mProduct
            binding.clickListener = clickListener
            // Это важно, потому что это приводит к немедленному выполнению привязки данных,
            // что позволяет RecyclerView производить правильные измерения размера представления
            binding.executePendingBindings()
        }
    }

    /**
     * Создаем новые представления элементов [RecyclerView] (вызываемые диспетчером макета)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductsAdapter.ProductViewHolder {
        return ProductViewHolder(
            ListItemWhBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ProductsAdapter.ProductViewHolder, position: Int) {
        val mProduct = getItem(position)
        holder.bind(mProduct,clickListener)
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of
     * [MarsPhoto] has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {

        //Этот метод вызывается, DiffUtil чтобы решить, представляют ли два объекта один и тот же элемент
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            //return oldItem.id == newItem.id
            return true
        }
        /*
        * Этот метод вызывается, DiffUtil когда он хочет проверить, имеют ли два элемента одинаковые данные.
        * Важными данными в Product является значение остатков.
        * */
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.quantity == newItem.quantity
        }
    }

}

class ProductListListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(product: Product) = clickListener(product.id)
}

