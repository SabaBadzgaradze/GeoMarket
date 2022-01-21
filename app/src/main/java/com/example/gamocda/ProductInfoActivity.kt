package com.example.gamocda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.gamocda.model.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProductInfoActivity : AppCompatActivity() {

    var productId: String? = null
    private val db = FirebaseDatabase.getInstance().getReference("products")
    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var productName: TextView
    private lateinit var productCategory: TextView
    private lateinit var productDescription: TextView
    private lateinit var productPrice: TextView
    private lateinit var buttonBack: Button
    private lateinit var imageViewProduct: ImageView
    private lateinit var addCarat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_info)

        init()
        registerListener()

        productId = intent.extras!!.getString("productId").toString()
        Log.d("SHOW_LOG", productId.toString())
        loadProductInfo(productId)
    }

    private fun registerListener() {
        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        addCarat.setOnClickListener {
            if (auth.currentUser != null) {
                addToCart(productId)
                addCarat.setColorFilter(R.color.white)
            } else {
                startActivity(Intent(this, AccountActivity::class.java))
            }
        }

    }

    private fun init() {
        productName = findViewById(R.id.name)
        productCategory = findViewById(R.id.category)
        productDescription = findViewById(R.id.description)
        productPrice = findViewById(R.id.Price)
        buttonBack = findViewById(R.id.buttonBack)
        imageViewProduct = findViewById(R.id.imageViewProduct)
        addCarat = findViewById(R.id.addCart)
    }


    private fun loadProductInfo(productId: String?) {

        db.child(productId.toString()).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentProduct = snapshot.getValue(Product::class.java)?: return
                Log.d("MY_TAG", currentProduct.toString())

                //

                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://geomarket-d6f06.appspot.com/")
                storageReference.child("ItemImages/${currentProduct.productOwnerId}/${currentProduct.productTitle}").downloadUrl.addOnSuccessListener {
                    Glide.with(this@ProductInfoActivity).load(it).into(imageViewProduct)
                }.addOnFailureListener {
                    Log.d("MY_TAG", "Error (Image)")
                }

                //

                productName.text = currentProduct.productTitle
                productCategory.text = currentProduct.productCategory
                productDescription.text = currentProduct.productdescription
                productPrice.text = "${currentProduct.productPrice}₾"

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addToCart(productId: String?) {

        dbUsers.child(auth.currentUser!!.uid).child("cart").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val contextView = findViewById<RelativeLayout>(R.id.content)

                val list: ArrayList<String> = arrayListOf()

                for (snap in snapshot.children) {
                    val id = snap.getValue(String::class.java)?: return
                    list.add(id)
                }

                if (!list.contains(productId.toString())) {
                    snapshot.ref.push().setValue(productId.toString()).addOnCompleteListener {
                        Snackbar.make(contextView, "პროდუქტი დაემატა კალათაში.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(
                        contextView,
                        "ეს პროდუქტი უკვე დამატებულია კალათაში.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}








