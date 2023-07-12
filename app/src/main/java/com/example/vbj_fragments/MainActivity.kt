package com.example.vbj_fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.vbj_fragments.fragments.FirstFragment_Name
import com.example.vbj_fragments.fragments.FragmentAdapter
import com.example.vbj_fragments.fragments.SecondFragment_DOB
import com.example.vbj_fragments.fragments.ThirdFragment_Emp

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: FragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        adapter = FragmentAdapter(supportFragmentManager, listOf(FirstFragment_Name(), SecondFragment_DOB(), ThirdFragment_Emp()))
        viewPager.adapter = adapter
    }
    fun showNext() {
        viewPager.currentItem += 1
    }
}