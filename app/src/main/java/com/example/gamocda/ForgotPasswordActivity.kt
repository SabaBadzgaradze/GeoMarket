package com.example.gamocda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity: AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var buttonSendEmail: Button
    private lateinit var buttonBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_forgot_password)

        init()
        signupListeners()
    }

    private fun init() {

        editTextEmail=findViewById(R.id.editTextEmail)
        buttonSendEmail=findViewById(R.id.buttonSendEmail)
        buttonBack=findViewById(R.id.buttonBack)

    }

    private fun signupListeners() {

        buttonBack.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }

        buttonSendEmail.setOnClickListener {
            val email = editTextEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Check email!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AccountActivity::class.java))
                        finish()
                    }else {
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}