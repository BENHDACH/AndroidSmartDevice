package com.example.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidsmartdevice.databinding.ActivityScanBinding


class ScanActivity : AppCompatActivity() {

    /*- Variable et valeurs déclarer -*/

    companion object{
        //Oui ! je le met en haut le companion object
        private const val SCAN_PERIOD: Long = 10000
    }

    private lateinit var binding: ActivityScanBinding
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var adapter: ScanAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val permissionG = permissions.all { it.value }
            if (permissionG) {
                initToggleAction()
            }
            else{
                Toast.makeText(this, "Pas de perm pas d'accès !", Toast.LENGTH_LONG).show()
            }
        }

   private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth doit être activé !", Toast.LENGTH_LONG).show()
        }else{
            //Bluetooth actif
            scanDeviceWithPermissions()
        }

        //On place une image pour retourner dans le menu principale
        binding.imageHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun scanDeviceWithPermissions() {
        if (allPermissionsGranted()) {
            initToggleAction()
        } else {
            //Au moins une permission n'a pas étais accordées, on l'a demande donc
            requestPermissionLauncher.launch(getAllPermissions())
        }
    }
    private fun allPermissionsGranted(): Boolean {
        val allPermissions = getAllPermissions()
        //On verifie que toute les permissions sont accordées ? => True/False
        return (allPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        })
    }
    private fun getAllPermissions(): Array<String> {
        //On crée une liste de permissions pour les passer en revue plus simplement
        val listOfPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
                //Manifest.permission.BLUETOOTH_SCAN,
                //Manifest.permission.BLUETOOTH_CONNECT,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        return listOfPermissions
    }
    private fun initToggleAction(){
        binding.button.setOnClickListener {
            scanBleDevices()
        }

        //On prépare l'adpater/recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ScanAdapter(this){
            val intent = Intent(this, DeviceActivity::class.java)
            intent.putExtra("device",it)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

    }
    @SuppressLint("MissingPermission")
    private fun scanBleDevices() {
        //On vérifie si le scan est en cours
        if (!scanning) {
            //Si il ne l'est pas, on start et après SCAN_PERIOD on stop
            handler.postDelayed({
                scanning = false
                bluetoothAdapter.bluetoothLeScanner?.stopScan(leScanCallback)
                togglePlayPause()
            }, SCAN_PERIOD)
            scanning = true
            bluetoothAdapter.bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            //Si il l'est, on stop lors du rappel de cette fonction
            scanning = false
            bluetoothAdapter.bluetoothLeScanner?.stopScan(leScanCallback)
        }
        togglePlayPause()
    }
    private fun togglePlayPause(){
        if(scanning){
            binding.textEnCours.visibility = View.VISIBLE
            binding.button.text = getString(R.string.ButtonScanOff)
            binding.button.backgroundTintList = getColorStateList(R.color.scarlet_red)
            binding.progressBar.isVisible = true

        }else{
            binding.textEnCours.visibility = View.GONE
            binding.button.text = getString(R.string.ButtonScanOn)
            binding.button.backgroundTintList = getColorStateList(R.color.blue_soft)
            binding.progressBar.isVisible = false
        }
    }
    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if(bluetoothAdapter?.isEnabled == true && allPermissionsGranted()){
            scanning = false
            //On arrête le scan proprement avant de quitter
            bluetoothAdapter.bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            //Log.e("Scan","result : $result")
            adapter.addDevice(result)
            adapter.notifyDataSetChanged()
        }
    }

}