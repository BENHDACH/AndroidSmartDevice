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

    @SuppressLint("MissingPermission")
    private fun ledClick(){
        var textNombre = ""

        /** Je vous remercie d'avance de prendre en considération les exemples en /*...*/, j'aimerais ne pas devoir
         passer de ratrapage du fait de ne pas avoir accès à une carte STM32. **/
        /* ------ > Etoilé le code pour allumer/éteindre une led (non tester) : <----------
        //Le TD parle d'un service numéro 3 mais je n'ai pas d'uuid donc "xxxxx")
        val service =
            bluetoothGatt!!.getService(UUID.fromString("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"))
        //La characteristic pour pouvoir modifié la valeur dans la carte
        val ledCharacteristic =
            service.getCharacteristic(UUID.fromString("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"))
        //On initialise les leds en éteinte (j'estime que 0 est éteint mais ce n'est peut être pas le cas)
        val ledsValue = byteArrayOf(
            0x00,
            0x00,
            0x00
        )
        ledCharacteristic.value = ledsValue
        //On envoie la valeur initialiser.
        bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
        */

        //Lors du click sur l'une des Led on incrémente le nombre
        binding.imageLed1.setOnClickListener{
            if(ledStatus[0]){
                binding.imageLed1.imageTintList = getColorStateList(R.color.black)
                /*
                ledsValue[0] = 0x00
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                */

            }else{
                /*
                //Je ne sais pas qu'elle valeur l'allume donc je vais mettre 1
                ledsValue[0] = 0x01
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                 */
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
                /*
                ledsValue[1] = 0x00
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                 */

            }else{
                /*
                ledsValue[1] = 0x01
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                 */

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
                /*
                ledsValue[2] = 0x00
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                 */

            }else{
                /*
                ledsValue[2] = 0x01
                ledCharacteristic.value = ledsValue
                bluetoothGatt!!.writeCharacteristic(ledCharacteristic)
                */
                countClick++
                textNombre = "Nombre:   $countClick"
                binding.numberText.text = textNombre
                binding.imageLed3.imageTintList = getColorStateList(R.color.yellow)
            }
            ledStatus[2]=!ledStatus[2]
        }


    }

    /* Le code permettant d'être notifié d'un changement pour les leds par exemple (voir aussi le callBack en bas)
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        bluetoothGatt?.let { gatt ->
            gatt.setCharacteristicNotification(characteristic, enabled)

            // This is specific to Heart Rate Measurement.
            if (BluetoothService.UUID_HEART_RATE_MEASUREMENT == characteristic.uuid) {
                val descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG))
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        } ?: run {
            Log.w(BluetoothService.TAG, "BluetoothGatt not initialized")
        }
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        when (characteristic.uuid) {
            UUID_HEART_RATE_MEASUREMENT -> {
                val flag = characteristic.properties
                val format = when (flag and 0x01) {
                    0x01 -> {
                        Log.d(TAG, "Heart rate format UINT16.")
                        BluetoothGattCharacteristic.FORMAT_UINT16
                    }
                    else -> {
                        Log.d(TAG, "Heart rate format UINT8.")
                        BluetoothGattCharacteristic.FORMAT_UINT8
                    }
                }
                val heartRate = characteristic.getIntValue(format, 1)
                Log.d(TAG, String.format("Received heart rate: %d", heartRate))
                intent.putExtra(EXTRA_DATA, (heartRate).toString())
            }
            else -> {
                // For all other profiles, writes the data formatted in HEX.
                val data: ByteArray? = characteristic.value
                if (data?.isNotEmpty() == true) {
                    val hexString: String = data.joinToString(separator = " ") {
                        String.format("%02X", it)
                    }
                    intent.putExtra(EXTRA_DATA, "$data\n$hexString")
                }
            }
        }
        sendBroadcast(intent)
    }
     */

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
        /*
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
        */
        /** Source pour la notifcation exemple : https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-ble-data **/

    }
}
