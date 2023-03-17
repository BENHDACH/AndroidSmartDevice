package com.example.androidsmartdevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.androidsmartdevice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bluetoothLogo.setBackgroundResource(R.drawable.bluetooth_icone)

        binding.buttonScan.setOnClickListener{
            val intent = Intent(this, ScanActivity::class.java)
            Toast.makeText(this, "Lets Scan !", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }


    }
}