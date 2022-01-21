package com.example.gamocda.adapter

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamocda.R
import com.example.gamocda.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MyProductsAdapter(val context: Context, private val productList: List<Product>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<MyProductsAdapter.ViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val textViewPrice: TextView = view.findViewById(R.id.textViewPrice)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val imageViewProduct: ImageView = view.findViewById(R.id.imageViewProduct)
        val productCategory: TextView = view.findViewById(R.id.productCategory)
        val productDelete: ImageView = view.findViewById(R.id.close)

        val item: RelativeLayout = view.findViewById(R.id.product)

        init {
            item.setOnClickListener(this)

            productDelete.setOnClickListener {
                listener.onItemDelete(position, item.findViewById(R.id.close))
            }

        }

        override fun onClick(view: View?) {

            val position: Int = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.remove_products, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProduct = productList[position]

        Glide.with(context)
            .load(currentProduct.productImageUrl)
            .into(holder.imageViewProduct)

        holder.textViewTitle.text = currentProduct.productTitle.toString()
        holder.textViewPrice.text = currentProduct.productPrice.toString() + "₾"
        holder.productCategory.text = currentProduct.productCategory.toString()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://geomarket-d6f06.appspot.com/")
        Log.d("MY_TAG", currentProduct.productOwnerId.toString())
        Log.d("MY_TAG", "${currentProduct.productOwnerId}/${currentProduct.productTitle}")
        storageReference.child("ItemImages/${currentProduct.productOwnerId}/${currentProduct.productTitle}").downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it).into(holder.imageViewProduct)
        }.addOnFailureListener {
            Log.d("MY_TAG", "Error (Image)")
        }

    }

    override fun getItemCount() = productList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int, view: ImageView)
    }

}