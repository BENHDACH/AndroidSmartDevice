package com.example.androidsmartdevice

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.androidsmartdevice.databinding.ActivityDeviceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class DeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBinding
    private var countClick = 0
    private var ledStatus = mutableListOf(false, false, false)
    private var bluetoothGatt: BluetoothGatt? = null

    //Aurait pu servir pour le service des leds sur carte...
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //On recupère l'objet device cliquer précedement
        val recup = intent.getParcelableExtra<BluetoothDevice>("device")
        if (recup != null) {
            binding.addressText.text =  recup?.address
        } else {
            binding.addressText.text =  "Error Null"
        }

        //On lance la connection
        bluetoothGatt = recup?.connectGatt(this, false, bluetoothGattCallback)
        bluetoothGatt?.connect()
        binding.ledText.text = getString(R.string.chargement)

        //On retourne au menu principale
        binding.imageHomeOfDevice.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



    private fun finChargement(success : Boolean) {
        binding.progressBar2.isVisible = false
        //On verifie si la connection à était établie => True/False
        if(success){
            //On affiche les led et le texte + on peut cliquer sur les leds
            binding.ledText.text = getString(R.string.LedShow)
            binding.imageLed1.isVisible = true
            binding.imageLed2.isVisible = true
            binding.imageLed3.isVisible = true
            binding.numberText.visibility = View.VISIBLE
            ledClick()
        }
        else{
            binding.ledText.text = getString(R.string.bleError)
            binding.imageBleError.isVisible = true
            binding.imageError.isVisible = true

        }

    }

    private fun ledClick(){
        var textNombre = ""
        //Lors du click sur l'une des Led on incrémente le nombre
        binding.imageLed1.setOnClickListener{
            if(ledStatus[0]){
                binding.imageLed1.imageTintList = getColorStateList(R.color.black)

            }else{
                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed1.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[0]=!ledStatus[0]
        }

        binding.imageLed2.setOnClickListener{
            if(ledStatus[1]){
                binding.imageLed2.imageTintList = getColorStateList(R.color.black)

            }else{
                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed2.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[1]=!ledStatus[1]
        }

        binding.imageLed3.setOnClickListener{
            if(ledStatus[2]){
                binding.imageLed3.imageTintList = getColorStateList(R.color.black)

            }else{
                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed3.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[2]=!ledStatus[2]
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        //On arrête proprement la connexion quand on quitte
        bluetoothGatt?.close()
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //Connection établie
                //Log.e("Conn","I am conected !")
                runOnUiThread{
                    finChargement(true)
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Echec de la connection
                //Log.e("ConnErr","I am an Ero:or 404 :_|'{=2~>")
                runOnUiThread{
                    finChargement(false)
                }
            }
        }
    }

}