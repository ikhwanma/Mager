package com.example.magerapp

import android.app.DatePickerDialog
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList

class FragmentRiwayat:Fragment(R.layout.fragment_riwayat) {
    private lateinit var btnFilter: Button
    private lateinit var btnTanggal: Button
    private lateinit var btnGrafik: Button
    private lateinit var imgFilter: ImageView
    private lateinit var databaseTransaksi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var transaksiRecyclerView: RecyclerView
    private lateinit var transaksiArrayList: ArrayList<Transaksi>
    var tanggal = ""
    var cek = "a"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val displayView = inflater.inflate(R.layout.fragment_riwayat, container, false)
        btnFilter = displayView.findViewById(R.id.btnFilter)
        btnTanggal = displayView.findViewById(R.id.btnTanggal)
        btnGrafik = displayView.findViewById(R.id.btnGrafik)
        imgFilter = displayView.findViewById(R.id.imgFilter)

        transaksiRecyclerView = displayView.findViewById(R.id.list)
        transaksiRecyclerView.layoutManager = LinearLayoutManager(activity)
        transaksiRecyclerView.setHasFixedSize(true)
        transaksiArrayList = arrayListOf<Transaksi>()

        auth = FirebaseAuth.getInstance()
        databaseTransaksi = FirebaseDatabase.getInstance().getReference("Transaksi")
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid)

        if (cek=="a") filter()
        if (cek=="b") closeFilter()

        if (tanggal.isEmpty()){
            getRiwayat()
        }


        return displayView
    }

    private fun closeFilter() {
        btnFilter.setOnClickListener {
            imgFilter.visibility = View.INVISIBLE
            btnGrafik.visibility = View.INVISIBLE
            btnTanggal.visibility = View.INVISIBLE
            cek = "a"
        }
    }

    private fun filter() {
        btnFilter.setOnClickListener {
            imgFilter.visibility = View.VISIBLE
            btnGrafik.visibility = View.VISIBLE
            btnTanggal.visibility = View.VISIBLE
            cek = "b"
            openCalendar()
            openGrafik()
        }
    }

    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        btnTanggal.setOnClickListener {
            imgFilter.visibility = View.INVISIBLE
            btnGrafik.visibility = View.INVISIBLE
            btnTanggal.visibility = View.INVISIBLE
            val dpd = activity?.let { it1 ->
                DatePickerDialog(
                    it1,
                    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                        val calendar = Calendar.getInstance()
                        calendar.set(mYear, mMonth, mDay)
                        val format = SimpleDateFormat("yyyy-MM-dd")
                        tanggal = format.format(calendar.time)

                        getRiwayatFilter()
                    },
                    year,
                    month,
                    day
                )
            }
            dpd!!.show()
        }

    }

    private fun getRiwayatFilter() {
        transaksiArrayList.clear()
        databaseTransaksi.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (transaksiSnapshot in snapshot.children){
                        if (transaksiSnapshot.child("id").getValue().toString() == auth.currentUser!!.uid&&
                            transaksiSnapshot.child("tanggal").getValue().toString() == tanggal){
                                val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                                transaksiArrayList.add(transaksi!!)
                        }
                    }
                    transaksiRecyclerView.adapter = RiwayatAdapter(transaksiArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun openGrafik() {
        btnGrafik.setOnClickListener {
            startActivity(Intent(activity,GrafikActivity::class.java))
        }
    }

    private fun getRiwayat() {
        databaseTransaksi.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (transaksiSnapshot in snapshot.children){
                            if (transaksiSnapshot.child("id").getValue().toString() == auth.currentUser!!.uid ){
                                val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                                transaksiArrayList.add(transaksi!!)
                            }
                    }
                    transaksiRecyclerView.adapter = RiwayatAdapter(transaksiArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

}

