package com.hqkhan.askai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

/** Created by HQKhan */

class SplashScreen : AppCompatActivity() {

    var mContext = this@SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(kotlinx.coroutines.Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }, 1000)

        supportActionBar?.hide()

    }
}