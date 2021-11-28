package com.example.magerapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentHome:Fragment(R.layout.fragment_home) {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseTransaksi: DatabaseReference
    private lateinit var viewNama: TextView
    private lateinit var viewBudget: TextView
    private lateinit var viewPemasukan: TextView
    private lateinit var viewPengeluaran: TextView
    private lateinit var viewPeminjaman: TextView
    private lateinit var btnLogout:Button
    private lateinit var btnTitik:Button
    private lateinit var imageWarn: ImageView
    private lateinit var transaksiRecyclerView: RecyclerView
    private lateinit var transaksiArrayList: ArrayList<Transaksi>
    private lateinit var sharedPreferences: SharedPreferences
    private var cek = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val displayView = inflater.inflate(R.layout.fragment_home, container, false)
        viewNama = displayView.findViewById(R.id.viewNama)
        viewBudget = displayView.findViewById(R.id.viewBudget)
        viewPemasukan = displayView.findViewById(R.id.viewPemasukan)
        viewPengeluaran = displayView.findViewById(R.id.viewPengeluaran)
        viewPeminjaman = displayView.findViewById(R.id.viewPeminjaman)
        imageWarn = displayView.findViewById(R.id.imageWarn)
        btnLogout = displayView.findViewById(R.id.btnLogout)
        btnTitik = displayView.findViewById(R.id.btnTitik)
        btnTitik.setOnClickListener {
            btnLogout.visibility = View.VISIBLE
        }

        sharedPreferences = requireActivity().getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)

        btnLogout.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            activity?.let {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        transaksiRecyclerView = displayView.findViewById(R.id.list)
        transaksiRecyclerView.layoutManager = LinearLayoutManager(activity)
        transaksiRecyclerView.layoutManager = object : LinearLayoutManager(activity){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        transaksiRecyclerView.setHasFixedSize(true)
        transaksiArrayList = arrayListOf<Transaksi>()

        auth = FirebaseAuth.getInstance()
        val currentUsers = auth.currentUser!!.uid
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUsers)
        databaseTransaksi = FirebaseDatabase.getInstance().getReference("Transaksi")

        databaseUser.addValueEventListener(object: ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPemasukkan = snapshot.child("pemasukkan").value.toString()
                val currentPengeluaran = snapshot.child("pengeluaran").value.toString()
                val currentPeminjaman = snapshot.child("peminjaman").value.toString()
                val currentUang = (currentPemasukkan.toInt() - currentPengeluaran.toInt()).toString()
                val localeID = Locale("in", "ID")
                val formatter = NumberFormat.getInstance(localeID)
                val formatPemasukkan = formatter.format(currentPemasukkan.toDouble())
                val formatPengeluaran = formatter.format(currentPengeluaran.toDouble())
                val formatPeminjaman = formatter.format(currentPeminjaman.toDouble())
                val formatUang = formatter.format(currentUang.toDouble())
                viewNama.text = snapshot.child("username").value.toString()

                if (currentPemasukkan=="0") viewPemasukan.text = "+ RP. 0,00"
                else viewPemasukan.text = "+ RP. $formatPemasukkan"

                if (currentPengeluaran=="0") viewPengeluaran.text = "- RP. 0,00"
                else viewPengeluaran.text = "- RP. $formatPengeluaran"

                if (currentPeminjaman=="0") viewPeminjaman.text = "RP. 0,00"
                else viewPeminjaman.text = "  RP. $formatPeminjaman"

                if (currentUang=="0") viewBudget.text = "RP. 0,00"
                else viewBudget.text = "RP. $formatUang"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        getRiwayat()
        if (cek==0)imageWarn.visibility = View.VISIBLE
        else imageWarn.visibility = View.INVISIBLE

        return displayView
    }

    private fun getRiwayat() {
        databaseTransaksi.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (transaksiSnapshot in snapshot.children){
                        if (transaksiSnapshot.child("id").getValue().toString() == auth.currentUser!!.uid){
                            val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                            transaksiArrayList.add(transaksi!!)
                            cek++
                            imageWarn.visibility = View.INVISIBLE
                        }
                    }
                    transaksiRecyclerView.adapter = RiwayatAdapter(transaksiArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Gagal", Toast.LENGTH_SHORT).show()
            }

            // apalu

        })
    }
}
