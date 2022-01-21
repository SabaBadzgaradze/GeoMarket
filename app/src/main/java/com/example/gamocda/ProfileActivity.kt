package com.example.gamocda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gamocda.adapter.ProductsAdapter
import com.example.gamocda.model.Product
import com.example.gamocda.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity: AppCompatActivity() {

    private lateinit var buttonLogOut: Button
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var buttonAddProduct: Button
    private lateinit var buttonBack: Button
    private lateinit var myProduct: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        init()

        signupListeners()
        loadProfileInfo()

    }



    private fun init() {

        buttonLogOut = findViewById(R.id.buttonLogOut)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        buttonBack = findViewById(R.id.buttonBack)
        myProduct = findViewById(R.id.myProduct)

        myProduct.setOnClickListener {
            startActivity(Intent(this, MyProducts::class.java))
        }

    }

    private fun signupListeners() {

        buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }
        buttonAddProduct.setOnClickListener {
            startActivity(Intent(this,ProductAdd::class.java))
            finish()
        }

        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }

    private fun loadProfileInfo() {

        db.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)?: return

                name.text = currentUser.userName + " " + currentUser.userLastname
                email.text = currentUser.userEmail
                phone.text = currentUser.userPhone

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}