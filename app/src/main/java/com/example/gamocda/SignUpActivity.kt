package com.example.gamocda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamocda.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity: AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextPassword2: TextInputEditText
    private lateinit var buttonSignUp: Button
    private lateinit var buttonBack: Button
    private lateinit var name: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var phone: TextInputEditText

    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)

        init()
        signupListeners()
    }

    private fun init() {

        editTextEmail= findViewById(R.id.editTextEmail)
        editTextPassword= findViewById(R.id.editTextPassword)
        editTextPassword2= findViewById(R.id.editTextPassword2)
        buttonSignUp= findViewById(R.id.buttonSignUp)
        buttonBack= findViewById(R.id.buttonBack)
        name= findViewById(R.id.name)
        lastName= findViewById(R.id.lastName)
        phone= findViewById(R.id.phone)


    }

    private fun signupListeners() {

        buttonBack.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }

        buttonSignUp.setOnClickListener {

            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val password2 = editTextPassword2.text.toString()
            val name = name.text.toString()
            val lastName = lastName.text.toString()
            val phone = phone.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || phone.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "ცარიელია!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.isEmpty() || phone.length <9){
                findViewById<TextInputLayout>(R.id.editTextPhoneContainer).helperText = "ნომერი არასწორია!"
            }

            if (!email.matches(".*[@].*".toRegex())) {
                findViewById<TextInputLayout>(R.id.editTextEmailContainer).helperText = "ელ-ფოსტა არასწორია!"
            }


            if (!password.matches(".*[a-z].*".toRegex())) {
                findViewById<TextInputLayout>(R.id.editTextPasswordContainer).helperText = "პაროლი უნდა შეიცავდეს 1 პატარა ასოს მაინც."
            }


            if (password != editTextPassword2.text.toString()){
                findViewById<TextInputLayout>(R.id.editTextPassword2Container).helperText = "დამადასტურებელი პაროლი არასწორია!"
            }

            if(password.isEmpty() || password.length < 8) {
                findViewById<TextInputLayout>(R.id.editTextPasswordContainer).helperText = "პაროლი უნდა შეიცავდეს 8 სიმბოლოს."
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password,)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {

                        saveUserInfo()

                        startActivity(Intent(this, AccountActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                    }
                }


        }
    }

    private fun saveUserInfo() {
        val name = name.text.toString()
        val email = editTextEmail.text.toString()
        val lastName = lastName.text.toString()
        val phone = phone.text.toString()

        val currentUser = User(name, lastName, email, phone)

        dbUsers.child(auth.currentUser!!.uid).setValue(currentUser)

    }

}