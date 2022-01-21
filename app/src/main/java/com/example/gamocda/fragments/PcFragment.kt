package com.example.gamocda.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamocda.AccountActivity
import com.example.gamocda.ProductInfoActivity
import com.example.gamocda.R
import com.example.gamocda.adapter.ProductsAdapter
import com.example.gamocda.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PcFragment: Fragment(R.layout.fragment_pc), ProductsAdapter.OnItemClickListener {

    private val db = FirebaseDatabase.getInstance().getReference("products")
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var productList: ArrayList<Product>
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        loadProducts()

    }

    private fun init(view: View) {
        productList = arrayListOf()
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts)
        val layoutManager = LinearLayoutManager(activity)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewProducts.layoutManager = layoutManager
    }

    private fun loadProducts() {

        db.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

                for (currentSnapshot in snapshot.children) {
                    val currentProduct = currentSnapshot.getValue(Product::class.java)?: return

                    if (currentProduct.productCategory == "კომპიუტერები") {
                        productList.add(currentProduct)
                    }
                }

                if (activity != null) {
                    recyclerViewProducts.adapter = ProductsAdapter(requireActivity(), productList, this@PcFragment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onItemClick(position: Int) {

        val currentItem: Product = productList[position]

        val intent = Intent(activity, ProductInfoActivity::class.java)
        intent.putExtra("productId", currentItem.productId)
        startActivity(intent)
    }

}














