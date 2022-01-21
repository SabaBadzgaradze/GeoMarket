package com.example.gamocda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class CartActivity : AppCompatActivity(), MyProductsAdapter.OnItemClickListener {

    private lateinit var recyclerViewCartProducts: RecyclerView
    private lateinit var productList: ArrayList<Product>

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")
    private val dbProducts = FirebaseDatabase.getInstance().getReference("products")
    private val currentUser = db.child(auth.currentUser!!.uid)

    private lateinit var buttonBack:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_cart)
        init()
        loadCartProducts()
        signupListeners()
    }

    private fun init() {
        productList = arrayListOf()
        recyclerViewCartProducts = findViewById(R.id.recyclerViewCartProducts)
        buttonBack = findViewById(R.id.buttonBack)

        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewCartProducts.layoutManager = layoutManager
    }

    private fun loadCartProducts() {

        currentUser.child("cart").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    productList.clear()

                    for (prodSnapshot in snapshot.children) {

                        val currentProductId = prodSnapshot.getValue(String::class.java)?: return
                        Log.d("Show", currentProductId)

                        dbProducts.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val currentProduct = snapshot.child(currentProductId).getValue(Product::class.java)?: return
                                productList.add(currentProduct)
                                recyclerViewCartProducts.adapter = MyProductsAdapter(this@CartActivity, productList, this@CartActivity)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    }

                } else {
                    Log.d("Show", "er2")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onItemClick(position: Int) {

        val currentItem: Product = productList[position]

        val intent = Intent(this, ProductInfoActivity::class.java)
        intent.putExtra("productId", currentItem.productId)
        startActivity(intent)
    }

    override fun onItemDelete(position: Int, view: ImageView) {

        val currentItem: Product = productList[position]

        db.child(auth.currentUser!!.uid).child("cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    Log.d("SHOW_LOG", "DATA: ${data.value}")
                    Log.d("SHOW_LOG", "CURRENT: ${currentItem.productId}")
                    if (data.value == currentItem.productId.toString()) {
                        Log.d("SHOW_LOG", snapshot.toString())
                        snapshot.ref.child(data.key.toString()).removeValue().addOnSuccessListener {
                            loadCartProducts()
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

    private fun signupListeners() {

        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}

