package com.example.gamocda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamocda.adapter.MyProductsAdapter
import com.example.gamocda.adapter.ProductsAdapter
import com.example.gamocda.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyProducts : AppCompatActivity(), MyProductsAdapter.OnItemClickListener {

    private lateinit var recyclerViewMyProducts: RecyclerView
    private lateinit var arrayListMyProducts: ArrayList<Product>
    private lateinit var buttonBack: Button

    private val dbProducts = FirebaseDatabase.getInstance().getReference("products")
    private val db = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = db.child(auth.currentUser!!.uid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_products)

        init()
        loadMyProducts()

    }

    private fun init() {
        arrayListMyProducts = arrayListOf()
        recyclerViewMyProducts = findViewById(R.id.recyclerViewMyProducts)
        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewMyProducts.layoutManager = layoutManager

        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMyProducts() {

        Log.d("SHOW_LOG", "TEST")

        currentUser.child("myProducts").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("SHOW_LOG", "TEST!")
                arrayListMyProducts.clear()

                for (prodSnapshot in snapshot.children) {

                    val currentProductId = prodSnapshot.value.toString()

                    dbProducts.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentProduct = snapshot.child(currentProductId).getValue(Product::class.java)?: return
                            arrayListMyProducts.add(currentProduct)
                            recyclerViewMyProducts.adapter = MyProductsAdapter(this@MyProducts, arrayListMyProducts, this@MyProducts)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onItemClick(position: Int) {
        val currentItem: Product = arrayListMyProducts[position]

        val intent = Intent(this, ProductInfoActivity::class.java)
        intent.putExtra("productId", currentItem.productId)
        startActivity(intent)
    }

    override fun onItemDelete(position: Int, view: ImageView) {

        AlertDialog.Builder(this)
            .setTitle("პროდუქტის წაშლა")
            .setMessage("ნამდვილად გსურთ პროდუქტის წაშლა?")
            .setPositiveButton("დიახ") { dialog, _ ->
                val currentItem: Product = arrayListMyProducts[position]
                dbProducts.child(currentItem.productId.toString()).removeValue()

                db.child(auth.currentUser!!.uid).child("myProducts").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data: DataSnapshot in snapshot.children) {
                            Log.d("SHOW_LOG", "DATA: ${data.value}")
                            Log.d("SHOW_LOG", "CURRENT: ${currentItem.productId}")
                            if (data.value == currentItem.productId.toString()) {
                                Log.d("SHOW_LOG", snapshot.toString())
                                snapshot.ref.child(data.key.toString()).removeValue().addOnSuccessListener {
                                    loadMyProducts()
                                    dialog.dismiss()
                                }
                                Log.d("SHOW_LOG", data.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }
            .setNegativeButton("არა") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}