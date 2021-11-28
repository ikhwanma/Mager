package com.example.magerapp

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TambahActivity : AppCompatActivity() {
    private lateinit var viewJenis:TextView
    private lateinit var textJtgl:TextView
    private lateinit var btnTanggal:Button
    private lateinit var viewTanggal:TextView
    private lateinit var imageTanggal:ImageView
    private lateinit var inputUang:EditText
    private lateinit var btnSimpan:Button
    private lateinit var btnBack:Button
    private lateinit var databaseTransaksi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var auth:FirebaseAuth
    var pemasukanUser = 0
    var pengeluaranUser = 0
    var budgetUser = 0
    var peminjamanUser = 0
    var tanggal = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah)
        viewJenis = findViewById(R.id.viewJenis)
        btnTanggal = findViewById(R.id.btnTanggal)
        viewTanggal = findViewById(R.id.viewTanggal)
        inputUang = findViewById(R.id.inputUang)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBack = findViewById(R.id.btnBack)
        textJtgl = findViewById(R.id.textJtgl)
        imageTanggal = findViewById(R.id.imageTanggal)
        auth = FirebaseAuth.getInstance()
        databaseTransaksi = FirebaseDatabase.getInstance().getReference("Transaksi")
        databaseUser = FirebaseDatabase.getInstance().getReference("Users")

        val bundle:Bundle? = intent.extras
        val jenis = bundle?.getString("jenis")
        viewJenis.text = jenis

        openCalendar()
        addUang()
        back()
        cekJenis()
    }

    private fun cekJenis() {
        if (viewJenis.text.toString()=="Budget"){
            btnTanggal.isClickable = false
            imageTanggal.visibility = View.INVISIBLE
            textJtgl.text = "Anggaran Harian"
            databaseUser.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pemasukan = snapshot.child("pemasukkan").getValue().toString().toInt()
                    val pengeluaran = snapshot.child("pengeluaran").getValue().toString().toInt()
                    val budget = (pemasukan-pengeluaran)/30
                    viewTanggal.text = budget.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun back() {
        btnBack.setOnClickListener {
            intent = Intent(this,HomeActivity::class.java)
            intent.putExtra("angka", 2)
            startActivity(intent)
        }
    }

    private fun addUang() {
        var jumlahUang = 0
        btnSimpan.setOnClickListener {
            val key = databaseTransaksi.push().key.toString()
            val uang = inputUang.text.toString().toInt()
            val jenis = viewJenis.text.toString()
            val transaksi = Transaksi(auth.currentUser!!.uid,uang,tanggal,jenis)

            databaseUser.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    pemasukanUser = snapshot.child("pemasukkan").getValue().toString().toInt()
                    pengeluaranUser = snapshot.child("pengeluaran").getValue().toString().toInt()
                    budgetUser = snapshot.child("budget").getValue().toString().toInt()
                    peminjamanUser = snapshot.child("peminjaman").getValue().toString().toInt()
                    if (viewJenis.text.toString()=="Pemasukan"){
                        jumlahUang = pemasukanUser + uang
                        databaseUser.child(auth.currentUser!!.uid).child("pemasukkan").setValue(jumlahUang)
                    }
                    if (viewJenis.text.toString()=="Pengeluaran"){
                        jumlahUang = pengeluaranUser + uang
                        databaseUser.child(auth.currentUser!!.uid).child("pengeluaran").setValue(jumlahUang)
                    }
                    if (viewJenis.text.toString()=="Budget"){
                        databaseUser.child(auth.currentUser!!.uid).child("budget").setValue(uang)
                        textJtgl.visibility = View.INVISIBLE
                        btnTanggal.visibility = View.INVISIBLE
                        imageTanggal.visibility = View.INVISIBLE
                        viewTanggal.visibility = View.INVISIBLE
                    }
                    if (viewJenis.text.toString()=="Pinjaman"){
                        jumlahUang = peminjamanUser + uang
                        databaseUser.child(auth.currentUser!!.uid).child("peminjaman").setValue(jumlahUang)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            if (viewJenis.text.toString()=="Budget"){
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }else{
                databaseTransaksi.child(key).setValue(transaksi)
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        btnTanggal.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val calendar = Calendar.getInstance()
                calendar.set(mYear,mMonth,mDay)
                val format = SimpleDateFormat("yyyy-MM-dd")
                tanggal = format.format(calendar.time)
                val formatText = SimpleDateFormat("dd/MM/yyyy")
                val tgl = formatText.format(calendar.time)
                viewTanggal.text = tgl
            }, year,month,day)
            dpd.show()
        }
    }


}