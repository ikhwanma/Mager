package com.example.magerapp

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.components.Legend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class GrafikActivity : AppCompatActivity() {
    private lateinit var btnBack:Button
    private lateinit var btnFilter:Button
    private lateinit var imgFilter:ImageView
    private lateinit var btnLastWeek: Button
    private lateinit var btnLastMonth: Button
    private lateinit var btnLastYear: Button
    private lateinit var pieChart: PieChart
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseTransaksi: DatabaseReference
    private lateinit var auth: FirebaseAuth
    val dataEntries = ArrayList<PieEntry>()
    val colors: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafik)
        btnBack = findViewById(R.id.btnBack)
        btnFilter = findViewById(R.id.btnFilter)
        imgFilter = findViewById(R.id.imgFilter)
        btnLastWeek = findViewById(R.id.btnLastWeek)
        btnLastMonth = findViewById(R.id.btnLastMonth)
        btnLastYear = findViewById(R.id.btnLastYear)
        pieChart = findViewById(R.id.chart)

        auth = FirebaseAuth.getInstance()
        databaseUser = FirebaseDatabase.getInstance().getReference("Users")
        databaseTransaksi = FirebaseDatabase.getInstance().getReference("Transaksi")

        colors.add(Color.parseColor("#7A04D7"))
        colors.add(Color.parseColor("#FF56EE"))

        initPieChart()
        noFilter()
        filterLastWeek()
        filterLastMonth()
        filterLastYear()
        back()
        openFilter()
    }

    private fun openFilter() {
        btnFilter.setOnClickListener {
            imgFilter.visibility = View.VISIBLE
            btnLastYear.visibility = View.VISIBLE
            btnLastMonth.visibility = View.VISIBLE
            btnLastWeek.visibility = View.VISIBLE
        }
    }

    private fun back() {
        btnBack.setOnClickListener {
            intent = Intent(this,HomeActivity::class.java)
            intent.putExtra("angka", 3)
            startActivity(intent)
        }
    }

    private fun initPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.text = ""
        //hollow pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)
        //adding padding
        pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        pieChart.setUsePercentValues(true)
        pieChart.isRotationEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isWordWrapEnabled = true
    }

    private fun noFilter() {
        databaseUser.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pieChart.setUsePercentValues(false)

                var pemasukan = snapshot.child("pemasukkan").getValue().toString()
                var pengeluaran = snapshot.child("pengeluaran").getValue().toString()

                dataEntries.add(PieEntry(pengeluaran.toFloat(), "Pengeluaran"))
                dataEntries.add(PieEntry(pemasukan.toFloat(), "Pemasukan"))

                val dataSet = PieDataSet(dataEntries, "")
                val data = PieData(dataSet)

                // In Percentage
                data.setValueFormatter(PercentFormatter())
                dataSet.colors = colors
                pieChart.data = data
                data.setValueTextSize(15f)
                pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChart.animateY(1400, Easing.EaseInOutQuad)

                //create hole in center
                pieChart.holeRadius = 58f
                pieChart.transparentCircleRadius = 61f
                pieChart.isDrawHoleEnabled = true
                pieChart.setHoleColor(Color.WHITE)
                pieChart.legend.isEnabled = false

                dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                pieChart.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun filterLastWeek(){
        btnLastWeek.setOnClickListener {
            imgFilter.visibility = View.INVISIBLE
            btnLastYear.visibility = View.INVISIBLE
            btnLastMonth.visibility = View.INVISIBLE
            btnLastWeek.visibility = View.INVISIBLE
            rangeTanggal(1)
        }
    }

    private fun filterLastMonth(){
        btnLastMonth.setOnClickListener {
            imgFilter.visibility = View.INVISIBLE
            btnLastYear.visibility = View.INVISIBLE
            btnLastMonth.visibility = View.INVISIBLE
            btnLastWeek.visibility = View.INVISIBLE
            rangeTanggal(2)
        }
    }

    private fun filterLastYear(){
        btnLastYear.setOnClickListener {
            imgFilter.visibility = View.INVISIBLE
            btnLastYear.visibility = View.INVISIBLE
            btnLastMonth.visibility = View.INVISIBLE
            btnLastWeek.visibility = View.INVISIBLE
            rangeTanggal(3)
        }
    }

    private fun rangeTanggal(cek:Int){
        val calendar = Calendar.getInstance()
        if (cek == 1) calendar.add(Calendar.DAY_OF_YEAR,-7)
        if (cek == 2) calendar.add(Calendar.MONTH,-1)
        if (cek == 3) calendar.add(Calendar.YEAR,-1)
        val format = SimpleDateFormat("yyyy-MM-dd")
        val tanggal = format.format(calendar.time)
        filter(tanggal)
    }

    private fun filter(lastDate:String){
        dataEntries.clear()
        var pemasukanWeek = 0
        var pengeluaranWeek = 0
        val format = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = format.format(Date())
        databaseTransaksi.orderByChild("tanggal").startAt(lastDate).endAt(currentDate)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (transaksiSnap in snapshot.children){
                            if(transaksiSnap.child("id").getValue().toString() == auth.currentUser!!.uid){
                                if (transaksiSnap.child("jenis").getValue().toString()=="Pemasukan"){
                                    pemasukanWeek += transaksiSnap.child("uang").getValue().toString().toInt()
                                }else if (transaksiSnap.child("jenis").getValue().toString()=="Pengeluaran"){
                                    pengeluaranWeek += transaksiSnap.child("uang").getValue().toString().toInt()
                                }
                            }
                        }
                    }
                    pieChart.setUsePercentValues(false)

                    dataEntries.add(PieEntry(pengeluaranWeek.toFloat(), "Pengeluaran"))
                    dataEntries.add(PieEntry(pemasukanWeek.toFloat(), "Pemasukan"))

                    val dataSet = PieDataSet(dataEntries, "")
                    val data = PieData(dataSet)

                    // In Percentage
                    data.setValueFormatter(PercentFormatter())
                    dataSet.colors = colors
                    pieChart.data = data
                    data.setValueTextSize(15f)
                    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
                    pieChart.animateY(1400, Easing.EaseInOutQuad)

                    //create hole in center
                    pieChart.holeRadius = 58f
                    pieChart.transparentCircleRadius = 61f
                    pieChart.isDrawHoleEnabled = true
                    pieChart.setHoleColor(Color.WHITE)
                    pieChart.legend.isEnabled = false

                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                    pieChart.invalidate()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            } )
    }
}