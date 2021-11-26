package com.example.magerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val firstFragment = FragmentHome()
        val secondFragment=FragmentTambah()
        val thirdFragment=FragmentRiwayat()
        val bundle:Bundle? = intent.extras
        val angka = bundle?.getInt("angka")

        if (angka == 2)setCurrentFragment(secondFragment)
        else if (angka == 3)setCurrentFragment(thirdFragment)
        else setCurrentFragment(firstFragment)


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(firstFragment)
                R.id.tambah->setCurrentFragment(secondFragment)
                R.id.riwayat->setCurrentFragment(thirdFragment)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

}
