package com.example.sampleflickrapp.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.sampleflickrapp.R

fun ImageView.load(url: String, placeholder: Int = R.drawable.placeholder) {


    Glide.with(context)
        .load(url)
        .centerCrop()
        .placeholder(placeholder)
        .error(placeholder)
        .into(this)


}