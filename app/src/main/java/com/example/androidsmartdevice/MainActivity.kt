package com.example.androidsmartdevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.androidsmartdevice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Lors du click buttonScan on passe à l'activité suivante (si plusieurs bouton j'en ferai une fonction)
        binding.buttonScan.setOnClickListener{
            val intent = Intent(this, ScanActivity::class.java)
            Toast.makeText(this, "C'est parti !", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }


    }
}