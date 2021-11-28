package com.example.magerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class RiwayatAdapter(private val transaksiList: ArrayList<Transaksi>): RecyclerView.Adapter<RiwayatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transaksiList[itemCount-1-position]
        var tgl = currentItem.tanggal!!.split("-")
        var formatTgl = tgl[2] + "/" + tgl[1] + "/" + tgl[0]
        holder.viewJenis.text = currentItem.jenis
        holder.viewTanggal.text = formatTgl
        val uang = currentItem.uang
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getInstance(localeID)
        val hasilFormat = formatter.format(uang.toString().toDouble())
        if (currentItem.jenis=="Pemasukan") holder.viewUang.text = "+ RP. $hasilFormat"
        if (currentItem.jenis=="Pengeluaran") holder.viewUang.text = "- RP. $hasilFormat"
        if (currentItem.jenis=="Pinjaman") holder.viewUang.text = "  RP. $hasilFormat"
    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val viewJenis: TextView = itemView.findViewById(R.id.viewJenis)
        val viewUang: TextView = itemView.findViewById(R.id.viewUang)
        val viewTanggal: TextView = itemView.findViewById(R.id.viewTanggal)
    }
}