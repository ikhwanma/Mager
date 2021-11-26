package com.example.magerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var inputEmail:EditText
    private lateinit var inputPass:EditText
    private lateinit var btnLogin:Button
    private lateinit var remember: CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    var isRemember = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        inputEmail = findViewById(R.id.inputEmail)
        inputPass = findViewById(R.id.inputPass)
        btnLogin = findViewById(R.id.btnLogin)
        remember = findViewById(R.id.remember)

        sharedPreferences = getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)
        isRemember = sharedPreferences.getBoolean("CHECKBOX", false)

        auth = FirebaseAuth.getInstance()

        if (isRemember){
            intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            login()
        }

    }

    private fun login() {
        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = inputEmail.text.toString()
        val pass = inputPass.text.toString()
        val checked = remember.isChecked

        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("EMAIL",email)
        editor.putString("PASSWORD",pass)
        editor.putBoolean("CHECKBOX",checked)
        editor.apply()

        auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener {
            Toast.makeText(this,"Berhasil",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,HomeActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this,"Gagal",Toast.LENGTH_SHORT).show()
        }
    }

}