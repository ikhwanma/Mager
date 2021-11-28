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
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var inputEmail:EditText
    private lateinit var inputPass:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnBuatAkun: Button
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
        btnBuatAkun = findViewById(R.id.btnBuatAkun)

        sharedPreferences = getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)
        isRemember = sharedPreferences.getBoolean("CHECKBOX", false)

        auth = FirebaseAuth.getInstance()

        openRegister()

        if (isRemember){
            intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            login()
        }

    }

    private fun openRegister(){
        btnBuatAkun.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
        val cek = isValidString(email)

        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("EMAIL",email)
        editor.putString("PASSWORD",pass)
        editor.putBoolean("CHECKBOX",checked)
        editor.apply()
        if (email.isEmpty()){
            inputEmail.setError("Email Tidak Boleh Kosong")
            inputEmail.requestFocus()
            return
        }
        if (!cek){
            inputEmail.setError("Email Tidak Sesuai Format")
            inputEmail.requestFocus()
            return
        }
        if (pass.isEmpty()){
            inputPass.setError("Password Tidak Boleh Kosong")
            inputPass.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this,"Email Atau Password Yang Anda Masukkan Salah, Silakan Coba Lagi",Toast.LENGTH_SHORT).show()
        }
    }

    fun isValidString(str: String): Boolean{
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }



}