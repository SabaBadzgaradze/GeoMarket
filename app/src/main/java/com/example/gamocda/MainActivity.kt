package com.example.gamocda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gamocda.adapter.ProductsAdapter
import com.example.gamocda.fragments.HomeFragment
import com.example.gamocda.model.Product
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(){


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var cart:ImageView
    private lateinit var account:ImageView
    private lateinit var burgerButton:ImageView
    private lateinit var burgerNav: NavigationView
    private val auth = FirebaseAuth.getInstance()
    


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        init()
        registerListener()
        navigation()

    }

    private fun init() {
        cart = findViewById(R.id.cart)
        account = findViewById(R.id.account)
        burgerButton = findViewById(R.id.burgerButton)
        burgerNav = findViewById(R.id.burgerNav)
        drawerLayout = findViewById(R.id.drawerLayout)



    }
    private fun registerListener() {

        account.setOnClickListener {
            startActivity(Intent(this,AccountActivity::class.java))
            finish()
        }
        burgerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        cart.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                startActivity(Intent(this, AccountActivity::class.java))
            }
        }

    }

    private fun navigation(){
        val controller= findNavController(R.id.nav_host_fragment)

        val appBarConfiguration= AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.accessoriesFragment,
            R.id.pcFragment,
            R.id.componentFragment
        ))

        setupActionBarWithNavController(controller ,appBarConfiguration)
        burgerNav.setupWithNavController(controller)
    }


}













