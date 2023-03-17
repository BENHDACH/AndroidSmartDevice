package com.example.androidsmartdevice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidsmartdevice.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}