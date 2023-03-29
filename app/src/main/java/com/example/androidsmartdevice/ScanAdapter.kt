package com.example.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidsmartdevice.databinding.CellBleBinding

class ScanAdapter(val context : Context, var onDeviceClickListener: (BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<ScanAdapter.CellViewHolder>() {

    //rssiValues est une liste de valeur rssi , myList est une liste de valeur BluetoothDevice
    private var rssiValues : MutableList<Int> = mutableListOf()
    private var myList : MutableList<BluetoothDevice> = mutableListOf()


    class CellViewHolder(binding: CellBleBinding) : RecyclerView.ViewHolder(binding.root) {
        //On bind correctement les élements pour les modifiés dans l'adapter.
        val textC = binding.textCell
        val textN = binding.textCellname
        val textR = binding.textRssi
        val pointRssi = binding.cercleRssi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val binding = CellBleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScanAdapter.CellViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return (myList.size)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        //La valeur actuelle du device en [position], on display l'address et le nom ('Unknown' si null)
        holder.textC.text = myList[position].address
        holder.textN.text = myList[position].name ?:"Unknown"
        //La valeur actuelle du rssi en [position], on display sa valeur dans textR
        holder.textR.text = "${rssiValues[position]}"

        /* --- La coloration des cercles selon le rssi --- */
        //Loin de nous (valeur de 120 choisie arbitrairement)
        if(rssiValues[position] < -120){
            //On modifie la couleur, en précisant le context (on force sans app:tint, plus simple que de creer 4 images coloré)
            holder.pointRssi.imageTintList = ContextCompat.getColorStateList(context,R.color.scarlet_red)
        }
        else if(rssiValues[position] <= -80){
            holder.pointRssi.imageTintList = ContextCompat.getColorStateList(context,R.color.orange)
        }
        else if(rssiValues[position] <= -40){
            holder.pointRssi.imageTintList = ContextCompat.getColorStateList(context,R.color.yellow)
        }
        //Vraiment proche
        else{
            holder.pointRssi.imageTintList = ContextCompat.getColorStateList(context,R.color.green_light)
        }

        //Le clickListener on envoie la valeur qui sera traiter ensuite avec un putExtra
        holder.itemView.setOnClickListener{
            onDeviceClickListener(myList[position])
        }
    }


    fun addDevice(newDevice: ScanResult){
        //On a besoin du ScanResult pour obtenir le rssi
        var shouldAddDevice = true
        myList.forEachIndexed { index, bluetoothDevice ->
            //Dans le ScanResult on récupère le .device qui contient les informations recherché
            if(bluetoothDevice.address == newDevice.device.address){
                myList[index] = newDevice.device
                //On reset le rssi qui peut changer
                rssiValues[index] = newDevice.rssi
                shouldAddDevice = false
            }
        }
        if(shouldAddDevice){
            myList.add(newDevice.device)
            //Si on l'ajoute on pense aussi à la valeur du rssi
            rssiValues.add(newDevice.rssi)
        }
    }
}