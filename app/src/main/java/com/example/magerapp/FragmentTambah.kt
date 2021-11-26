package com.example.magerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentTambah:Fragment(R.layout.fragment_tambah) {
    private lateinit var btnPemasukan: Button
    private lateinit var btnPengeluaran: Button
    private lateinit var btnPinjaman: Button
    private lateinit var btnBudget: Button
    var jenis = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val displayView = inflater.inflate(R.layout.fragment_tambah, container, false)
        btnPemasukan = displayView.findViewById(R.id.btnPemasukan)
        btnPengeluaran = displayView.findViewById(R.id.btnPengeluaran)
        btnPinjaman = displayView.findViewById(R.id.btnPinjaman)
        btnBudget = displayView.findViewById(R.id.btnBudget)


        openPemasukan()
        openPengeluaran()
        openPinjaman()
        openBudget()

        return displayView
    }

    private fun openBudget() {
        btnBudget.setOnClickListener {
            jenis = "Budget"
            activity?.let {
                val intent = Intent(activity, TambahActivity::class.java)
                intent.putExtra("jenis", jenis)
                startActivity(intent)
            }
        }
    }

    private fun openPinjaman() {
        btnPinjaman.setOnClickListener {
            jenis = "Pinjaman"
            activity?.let {
                val intent = Intent(activity, TambahActivity::class.java)
                intent.putExtra("jenis", jenis)
                startActivity(intent)
            }
        }
    }

    private fun openPengeluaran() {
        btnPengeluaran.setOnClickListener {
            jenis = "Pengeluaran"
            activity?.let {
                val intent = Intent(activity, TambahActivity::class.java)
                intent.putExtra("jenis", jenis)
                startActivity(intent)
            }
        }
    }

    private fun openPemasukan() {
        btnPemasukan.setOnClickListener {
            jenis = "Pemasukan"
            activity?.let {
                val intent = Intent(activity, TambahActivity::class.java)
                intent.putExtra("jenis", jenis)
                startActivity(intent)
            }
        }
    }


}
