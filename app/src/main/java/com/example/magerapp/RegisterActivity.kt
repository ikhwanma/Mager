package com.example.magerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var inputUsername : EditText
    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputKonfPass : EditText
    private lateinit var btnRegister : Button
    private lateinit var btnLogin : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseUser : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        inputUsername = findViewById(R.id.inputUsername)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputKonfPass = findViewById(R.id.inputKonfPass)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        auth = FirebaseAuth.getInstance()
        databaseUser = FirebaseDatabase.getInstance().getReference("Users")

        register()
        login()

    }

    private fun login() {
        btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun register(){
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val uname = inputUsername.text.toString()
        val pass = inputPassword.text.toString()
        val email = inputEmail.text.toString()
        val cek = isValidString(email)

        if (uname.isEmpty()){
            inputUsername.setError("Username Tidak Boleh Kosong")
            inputUsername.requestFocus()
            return
        }
        if (uname.length<6){
            inputUsername.setError("Username Minimal 6 Karakter")
            inputUsername.requestFocus()
            return
        }
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
            inputPassword.setError("Password Tidak Boleh Kosong")
            inputPassword.requestFocus()
            return
        }
        if(inputKonfPass.text.toString() != pass){
            inputKonfPass.setError("Password Tidak Sama")
            inputKonfPass.requestFocus()
            return
        }
        if (pass.length < 6){
            inputPassword.setError("Password minimal 6 karakter")
            inputPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener {
            val user = Users(uname,email,pass,0,0,0,0)
            databaseUser.child(auth.currentUser!!.uid).setValue(user).addOnSuccessListener {
                intent = Intent(this, SuksesActivity::class.java)
                startActivity(intent)
            }
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