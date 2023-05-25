package com.test.askai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        var context= this@SplashScreen   //  init context here

        Handler().postDelayed(kotlinx.coroutines.Runnable {
             val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        },1000)

//        getActivity().getSupportedActionBar().hide();
        supportActionBar?.hide()


    }
}