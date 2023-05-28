package com.hqkhan.askai

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Created by HQKhan */

class Contact : AppCompatActivity() {

    lateinit var mTitle: TextView
    lateinit var mInfoIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        mTitle = findViewById(R.id.title_screen)
        mInfoIcon = findViewById(R.id.iv_info)

        mTitle.text = "Contact"
        mInfoIcon.visibility = View.GONE
    }
}