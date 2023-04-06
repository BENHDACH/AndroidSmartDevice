package com.example.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.androidsmartdevice.databinding.ActivityDeviceBinding
import java.util.*


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

    //Récupère parmis les 4 services (=> n°3)
    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    //charactéristic du service 4 pour led
    private val characteristicLedUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")
    //private val characteristicButtonUUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")

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
        //bluetoothGatt?.connect()
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

    @SuppressLint("MissingPermission")
    private fun ledClick(){
        var textNombre = ""

        //Lors du click sur l'une des Led on incrémente le nombre
        binding.imageLed1.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if(ledStatus[0]){
                binding.imageLed1.imageTintList = getColorStateList(R.color.black)
                //Eteins tout malheuresement (je ne trouve pas comment éteindre 1 seul led)
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)

            }else{
                //Allumage Led 1
                characteristic?.value = byteArrayOf(0x01)
                bluetoothGatt?.writeCharacteristic(characteristic)

                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed1.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[0]=!ledStatus[0]
        }

        binding.imageLed2.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if(ledStatus[1]){
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
                binding.imageLed2.imageTintList = getColorStateList(R.color.black)

            }else{
                characteristic?.value = byteArrayOf(0x02)
                bluetoothGatt?.writeCharacteristic(characteristic)

                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed2.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[1]=!ledStatus[1]
        }

        binding.imageLed3.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if(ledStatus[2]){
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
                binding.imageLed3.imageTintList = getColorStateList(R.color.black)

            }else{
                characteristic?.value = byteArrayOf(0x03)
                bluetoothGatt?.writeCharacteristic(characteristic)

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
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //Connection établie
                //Log.e("Conn","I am conected !")
                runOnUiThread{
                    finChargement(true)
                }
                bluetoothGatt?.discoverServices()

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
