package com.example.gamocda

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.gamocda.model.Product
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProductAdd : AppCompatActivity() {

    private lateinit var buttonAddProduct: Button
    private lateinit var buttonBack: Button
    private lateinit var productList: ArrayList<Product>
    private lateinit var productTitle: EditText
    private lateinit var productPrice: EditText
    private lateinit var productDescription: EditText
    private lateinit var productNumber: EditText
    private lateinit var selectPhoto: ImageView
    private lateinit var textfield: AutoCompleteTextView

    private val db = FirebaseDatabase.getInstance().getReference("products")
    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    private var imageUri: Uri = Uri.parse("android.resource://com.example.gamocda/drawable/def")
    var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_add)

        init()

        signupListeners()


        //

        val items = listOf("PC აქსესუარები", "PC ნაწილები", "კომპიუტერები")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (findViewById<TextInputLayout>(R.id.menu).editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //

    }

    private fun init() {

        textfield = findViewById(R.id.textfield)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        productList = arrayListOf()
//        productCategory = findViewById(R.id.productCategory)
        productDescription = findViewById(R.id.productDescription)
        productTitle = findViewById(R.id.productName)
        productNumber = findViewById(R.id.productNumber)
        productPrice = findViewById(R.id.productPrice)
        buttonBack = findViewById(R.id.buttonBack)
        selectPhoto = findViewById(R.id.selectPhoto)

    }

    private fun signupListeners() {

        selectPhoto.setOnClickListener {
            selectPhoto()
        }

        buttonAddProduct.setOnClickListener {
            addProduct()
        }

        buttonBack.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }

    }

    private fun addProduct() {

        getProductListSize()

    }

    private fun getProductListSize() {

        db.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val productTitle = productTitle.text.toString()
                val productCategory = textfield.text.toString()
                val productDescription = productDescription.text.toString()
                val productNumber = productNumber.text.toString()
                val productPrice = productPrice.text.toString()
                val textfield = textfield.text.toString()
                val productOwnerId = auth.currentUser!!.uid

                if (productTitle.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty() || productNumber.isEmpty() || textfield.isEmpty()){
                    Toast.makeText(this@ProductAdd, "გთხოვთ შეავსოთ ყველა მოთხოვნა!", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    Toast.makeText(this@ProductAdd, "პროდუქტი დამატებულია.", Toast.LENGTH_SHORT).show()
                }

                uploadPhoto()

                val id = snapshot.ref.push().key
                val productId = id

                dbUsers.child(auth.currentUser!!.uid).child("myProducts").push().setValue(id)

                val product = Product(productTitle, productPrice.toDouble(), "https://www.freeiconspng.com/uploads/no-image-icon-4.png",productCategory, productDescription, productNumber, productOwnerId, productId)
                snapshot.ref.child(id.toString()).setValue(product).addOnSuccessListener {
                    Handler().postDelayed({
                        goToMainActivity()
                    }, 1500)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun selectPhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    private fun uploadPhoto() {

        val folderName = auth.currentUser!!.uid
        val fileName = productTitle.text.toString()
        val storage = FirebaseStorage.getInstance().getReference("ItemImages/$folderName/$fileName")
        storage.putFile(imageUri).addOnSuccessListener {
            Log.d("MY_TAG", "Success")
        }.addOnFailureListener {
            Log.d("MY_TAG", "Failed")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            selectPhoto.setImageURI(imageUri)
        }

    }
//    private fun addclick() {
//
//        val productPrice = productPrice.text.toString()
//        val productName = productTitle.text.toString()
//        val productDescription = productDescription.text.toString()
//        val productNumber = productNumber.text.toString()
//        val textfield = textfield.text.toString()
//
//        if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty() || productNumber.isEmpty() || textfield.isEmpty()){
//            Toast.makeText(this, "გთხოვთ შეავსოთ ყველა მოთხოვნა!", Toast.LENGTH_SHORT).show()
//        }
//    }

}


/*



private fun uploadImage() {
        val fileName = auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance().getReference("ProfilePictures/$fileName")
        storage.putFile(imageUri).addOnSuccessListener {
            Log.d("MY_TAG", "Success")
        }.addOnFailureListener {
            Log.d("MY_TAG", "Failed")
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

/////////////////////////

dbPosts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    id = snapshot.childrenCount

                    dbStudents.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val postOwner = snapshot.child(auth.currentUser!!.uid).getValue(Student::class.java)?: return
                            val postContent = binding.editTextPost.text.toString()
                            val postOwnerId = auth.currentUser?.uid.toString()
                            dbPosts.child(id.toString()).setValue(Post(postContent, postOwner.name + " " + postOwner.surname, postOwnerId)).addOnSuccessListener {
                                goToBack()
                            }
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

*/