package com.test.askai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class Contact : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        var textView222222: TextView =findViewById(R.id.textView222222)
        var imageView2222: ImageView =findViewById(R.id.imageView2222)
        imageView2222.visibility= View.GONE
        textView222222.text = "Contact"
    }
}