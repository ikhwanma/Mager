package com.example.magerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    private lateinit var sharedPreferences: SharedPreferences
    var isRemember = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isRemember = sharedPreferences.getBoolean("CHECKBOX", false)

        if (isRemember){
            intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            openLogin()
            openRegister()
        }

    }

    private fun openRegister() {
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openLogin() {
        btnLogin.setOnClickListener {
            intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}